import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tree.BPlusTree;
import visualization.BTreePane;
import visualization.LimitedTextField;

import java.awt.*;
import java.util.LinkedList;

/**
 * Main
 */
public class Main extends Application {
    final ScrollPane sp = new ScrollPane();
    public Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public double windowWidth = screenSize.getWidth() * .75;
    public double windowHeight = screenSize.getHeight() * .75;
    BPlusTree bTree = new BPlusTree();
    private double key;
    private int order = 3;
    private double key1;
    private double key2;
    private double generate;
    private BTreePane btPane;
    private LimitedTextField keyText = new LimitedTextField();
    private LimitedTextField numberOne = new LimitedTextField();
    private LimitedTextField numberTwo = new LimitedTextField();
    private LimitedTextField generateText = new LimitedTextField();
    private LinkedList<BPlusTree> bTreeLinkedList = new LinkedList<BPlusTree>();
    private Canvas canvas;

    /**
     * Start
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        bTree.initialize(order);
        canvas = new Canvas(windowWidth, windowHeight);
        BorderPane borderPane = new BorderPane();
        borderPane.setBorder(new Border(new BorderStroke(Color.GREEN,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        borderPane.setPadding(new Insets(0));

        // Bind the width/height property to the wrapper Pane
        canvas.widthProperty().bind(borderPane.widthProperty());
        canvas.heightProperty().bind(borderPane.heightProperty());

        // Resize
        canvas.widthProperty().addListener(event -> resize(canvas));
        canvas.heightProperty().addListener(event -> resize(canvas));

        // Create button HBox on top
        HBox hBox = new HBox(15);
        hBox.setBorder(new Border(new BorderStroke(Color.valueOf("#DDEEDD"),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        hBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#DDEEDD"), CornerRadii.EMPTY, Insets.EMPTY)));
        hBox.setPadding(new Insets(20, 10, 20, 10));


        // Create button HBox on top
        HBox hBoxBottom = new HBox(15);
        hBoxBottom.setBorder(new Border(new BorderStroke(Color.valueOf("#DDEEDD"),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        hBoxBottom.setBackground(new Background(new BackgroundFill(Color.valueOf("#DDEEDD"), CornerRadii.EMPTY, Insets.EMPTY)));
        hBoxBottom.setPadding(new Insets(10, 10, 10, 10));


        // TextField
        keyText.setPrefWidth(60);
        keyText.setMaxLength(4);
        keyText.setAlignment(Pos.BASELINE_RIGHT);

        // Between search
        numberOne.setPrefWidth(60);
        numberOne.setAlignment(Pos.BOTTOM_CENTER);
        numberOne.setMaxLength(4);

        numberTwo.setPrefWidth(60);
        numberTwo.setAlignment(Pos.BOTTOM_CENTER);
        numberTwo.setMaxLength(4);
        //Generate
        generateText.setPrefWidth(60);
        generateText.setAlignment(Pos.BOTTOM_LEFT);
        generateText.setMaxLength(4);
        // Button
        Button insertButton = new Button("Insertar");
        Button deleteButton = new Button("Eliminar");
        Button searchButton = new Button("Buscar");
        Button searchBetweenButton = new Button("Buscar");
        Button resetButton = new Button("Reset");
        Button generarButton = new Button("Generar");
        resetButton.setId("reset");
        resetButton.setStyle("-fx-base: red;");

        Label nullLabel = new Label();
        nullLabel.setPrefWidth(30);

        hBox.getChildren().addAll(new Label("Ingresa un número: "), keyText, insertButton, deleteButton, searchButton,
                new Label("Ingresa el rango de números a buscar: "), numberOne, numberTwo, searchBetweenButton, resetButton, nullLabel);
        hBox.setAlignment(Pos.CENTER);

        // Create a combo box
        String orderList[] = {"3", "4", "5", "6", "7"};
        ComboBox comboBox = new ComboBox(FXCollections.observableArrayList(orderList));
        //Select default value
        comboBox.getSelectionModel().selectFirst();
        //Create action event
        EventHandler<ActionEvent> event =
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        reset();
                        order = Integer.parseInt((String) comboBox.getValue());
                        bTree.initialize(order);
                    }
                };

        // Set on action
        comboBox.setOnAction(event);

        hBoxBottom.getChildren().addAll(new Label("Order: "), comboBox, generateText, generarButton, nullLabel);
        hBoxBottom.setAlignment(Pos.BOTTOM_RIGHT);

        // Create TreePane in center

        btPane = new BTreePane(windowWidth / 2, 50);
        sp.setVmax(440);
        sp.setPrefSize(115, 150);
        sp.setContent(btPane);


        //Set Top
        borderPane.setTop(hBox);
        //Set Center
        borderPane.setCenter(sp);
        //Set Bottom
        borderPane.setBottom(hBoxBottom);


        //Buttons events
        insertButton.setOnMouseClicked(e -> insertValue());
        deleteButton.setOnMouseClicked(e -> deleteValue());
        searchButton.setOnMouseClicked(e -> searchValue());
        searchBetweenButton.setOnMouseClicked(e -> searchBetweenValues());
        resetButton.setOnMouseClicked(e -> reset());
        generarButton.setOnMouseClicked(e -> insertValues());

        // Create a scene
        Scene scene = new Scene(borderPane, windowWidth, windowHeight);
        scene.getStylesheets().add(getClass().getResource("/visualization/BtreeStyle.css").toExternalForm());
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
                scene.setRoot(borderPane);
                borderPane.getChildren().clear();
                borderPane.getChildren().add(newValue);
                scene.rootProperty().addListener(this);
            }
        });

    }


    /**
     * @param canvas
     */
    public void resize(Canvas canvas) {
        this.windowWidth = canvas.getWidth();
        this.windowHeight = canvas.getHeight();
    }


    /**
     *
     */
    private void insertValue() {
        try {
            key = Double.parseDouble(keyText.getText());
            keyText.setText("");
            bTree.insertElement(key);
            btPane.updatePane(bTree, this.windowWidth);
            btPane.searchPathColoring3(bTree, key);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Dato de entrada incorrecto!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void insertValues() {
        try {
            generate = Double.parseDouble(generateText.getText());
            generateText.setText("");
            reset();
            for (int i = 0; i < generate; i++) {
                bTree.insertElement(i);

            }
            btPane.updatePane(bTree, this.windowWidth);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Dato de entrada incorrecto!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void deleteValue() {
        try {
            key = Double.parseDouble(keyText.getText());
            keyText.setText("");
            if (bTree.getNode(key) == null) {
                throw new Exception("Not in the tree!");
            }
            bTree.deleteElement(key);
            btPane.updatePane(bTree, this.windowWidth);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Dato de entrada incorrecto!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    /**
     *
     */
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

    /**
     *
     */
    private void searchBetweenValues() {
        try {
            key1 = Double.parseDouble(numberOne.getText());
            numberOne.setText("");
            key2 = Double.parseDouble(numberTwo.getText());
            numberTwo.setText("");
            btPane.searchPathColoring2(bTree, key1, key2);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Datos de entrada incorrectos!", ButtonType.OK);
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }


    /**
     *
     */
    private void reset() {
        keyText.setText("");
        bTree.setRoot(null);
        bTreeLinkedList.clear();
        resize(canvas);
        btPane.updatePane(bTree);
    }


}