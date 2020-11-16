import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.awt.*;
import java.util.LinkedList;

public class Main extends Application {

    private double key;
    private int order = 3;
    private double key1;
    private double key2;

    private BTreePane btPane;
    private TextField keyText = new TextField();
    private TextField orderText = new TextField();

    private TextField numberOne = new TextField();
    private TextField numberTwo = new TextField();


    private LinkedList<BPlusTree> bTreeLinkedList = new LinkedList<BPlusTree>();

    BPlusTree bTree = new BPlusTree();


    public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public double windowWidth = screenSize.getWidth() * .75;
    public double windowHeight = screenSize.getHeight() * .75;

    private Canvas canvas;
    private GraphicsContext g;


    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas(windowWidth, windowHeight);
        g = canvas.getGraphicsContext2D();
        BorderPane borderPane = new BorderPane();

        // Bind the width/height property to the wrapper Pane
        canvas.widthProperty().bind(borderPane.widthProperty());
        canvas.heightProperty().bind(borderPane.heightProperty());
        // redraw when resized
        canvas.widthProperty().addListener(event -> resize(canvas));
        canvas.heightProperty().addListener(event -> resize(canvas));

        // Create button HBox on top
        HBox hBox = new HBox(15);
        borderPane.setTop(hBox);
        BorderPane.setMargin(hBox, new Insets(10, 10, 10, 10));
        // Create button HBox on top
        HBox hBoxBottom = new HBox(15);
        borderPane.setBottom(hBoxBottom);
        BorderPane.setMargin(hBoxBottom, new Insets(10, 10, 10, 10));
        // TextField
        keyText.setPrefWidth(60);
        keyText.setAlignment(Pos.BASELINE_RIGHT);
        // OrderField
        orderText.setPrefWidth(60);
        orderText.setAlignment(Pos.BOTTOM_CENTER);
        // Between search
        numberOne.setPrefWidth(60);
        numberOne.setAlignment(Pos.BOTTOM_CENTER);
        numberTwo.setPrefWidth(60);
        numberTwo.setAlignment(Pos.BOTTOM_CENTER);
        // Button
        Button insertButton = new Button("Insertar");
        Button deleteButton = new Button("Eliminar");
        Button searchButton = new Button("Buscar");
        Button searchBetweenButton = new Button("Buscar2");
        Button resetButton = new Button("Reset");
        resetButton.setId("reset");
        resetButton.setStyle("-fx-base: red;");

        Label nullLabel = new Label();
        nullLabel.setPrefWidth(30);

        hBox.getChildren().addAll(new Label("Ingresa un número: "), keyText, insertButton, deleteButton, searchButton,
                numberOne, numberTwo, searchBetweenButton, resetButton, nullLabel);
        hBox.setAlignment(Pos.CENTER);
        hBoxBottom.getChildren().addAll(new Label("Order: "), orderText, nullLabel);
        hBoxBottom.setAlignment(Pos.CENTER);
        //Set Order
        orderText.setText("3");
        bTree.initialize(order);

        orderText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (!(ke.getText().equalsIgnoreCase(""))) {
                    order = Integer.parseInt(ke.getText());
                    bTree.initialize(order);
                } else {
                    //orderText.setText("3");
                    bTree.initialize(3);
                }
                // System.out.println("Key Pressed: " + ke.getText());

            }
        });

        keyText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent ke) {
                //System.out.println("Key Pressed: " + orderText.getText());
                if (orderText.getText().equalsIgnoreCase("")) {
                    orderText.setText("3");
                    //bTree.initialize(3);
                }
            }
        });
        // Create TreePane in center
        btPane = new BTreePane(windowWidth / 2, 50, bTree);
        btPane.setPrefSize(windowHeight, windowWidth);
        borderPane.setCenter(btPane);


        BorderPane root = new BorderPane(borderPane);
        root.setPadding(new Insets(10));
        //Buttons events
        insertButton.setOnMouseClicked(e -> insertValue());
        deleteButton.setOnMouseClicked(e -> deleteValue());
        searchButton.setOnMouseClicked(e -> searchValue());
        searchBetweenButton.setOnMouseClicked(e -> searchBetweenValues());
        resetButton.setOnMouseClicked(e -> reset());


        // Create a scene
        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.getStylesheets().add(getClass().getResource("BtreeStyle.css").toExternalForm());
        primaryStage.setTitle("Visualizador B+ Tree");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(60);
        primaryStage.setMinHeight(100);
        primaryStage.setResizable(true);
        scene.rootProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> arg0, Parent oldValue, Parent newValue) {
                scene.rootProperty().removeListener(this);
                scene.setRoot(root);
                ((Region) newValue).setPrefWidth(windowWidth);     //make sure is a Region!
                ((Region) newValue).setPrefHeight(windowHeight);   //make sure is a Region!
                root.getChildren().clear();
                root.getChildren().add(newValue);
                scene.rootProperty().addListener(this);
            }
        });

    }


    public void resize(Canvas canvas) {
        this.windowWidth = canvas.getWidth();
        this.windowHeight = canvas.getHeight();
    }


    private void insertValue() {
        try {
            key = Double.parseDouble(keyText.getText());
            keyText.setText("");
            bTree.setStepsTree(new LinkedList<BPlusTree>());
            bTree.insertElement(key);
            bTreeLinkedList = bTree.getStepsTree();
            int size = bTreeLinkedList.size();
            btPane.updatePane(bTreeLinkedList.get(size - 1), this.windowWidth);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Dato de entrada incorrecto!", ButtonType.OK);
            alert.show();
        }
    }


    private void deleteValue() {
        try {
            key = Double.parseDouble(keyText.getText());
            keyText.setText("");
            if (bTree.getNode(key) == null) {
                throw new Exception("Not in the tree!");
            }
            bTree.setStepsTree(new LinkedList<BPlusTree>());

            bTree.deleteElement(key);

            bTreeLinkedList = bTree.getStepsTree();
            int size = bTreeLinkedList.size();
            btPane.updatePane(bTreeLinkedList.get(size - 1), this.windowWidth);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Dato de entrada incorrecto!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void searchValue() {
        try {
            key = Double.parseDouble(keyText.getText());
            keyText.setText("");
            btPane.searchPathColoring(bTree, key);

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Dato de entrada incorrecto!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    private void searchBetweenValues() {
        try {
            key1 = Double.parseDouble(numberOne.getText());
            numberOne.setText("");
            key2 = Double.parseDouble(numberTwo.getText());
            numberTwo.setText("");
            btPane.searchPathColoring2(bTree, key1, key2);

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Datos de entrada incorrecto!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }


    private void reset() {
        keyText.setText("");
        orderText.setText("3");
        bTree.setRoot(null);
        bTreeLinkedList.clear();
        btPane.updatePane(bTree);
    }
}