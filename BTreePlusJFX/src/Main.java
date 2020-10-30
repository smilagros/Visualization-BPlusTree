import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.LinkedList;

public class Main extends Application {

    private double key;
    private int order = 3;

    private BTreePane btPane;
    private TextField keyText = new TextField();
    private TextField orderText = new TextField();


    private LinkedList<BPlusTree> bTreeLinkedList = new LinkedList<BPlusTree>();

    BPlusTree bTree = new BPlusTree();

    @Override
    public void start(Stage primaryStage) {

        final int windowHeight = 480;
        final int windowWidth = 720;

        BorderPane borderPane = new BorderPane();

        // Create button HBox on top
        HBox hBox = new HBox(15);
        borderPane.setTop(hBox);
        BorderPane.setMargin(hBox, new Insets(10, 10, 10, 10));
        // TextField
        keyText.setPrefWidth(60);
        keyText.setAlignment(Pos.BASELINE_RIGHT);
        // OrderField
        orderText.setPrefWidth(60);
        orderText.setAlignment(Pos.BOTTOM_CENTER);
        // Button
        Button insertButton = new Button("Insertar");
        Button deleteButton = new Button("Eliminar");
        Button searchButton = new Button("Buscar");
        Button resetButton = new Button("Reset");
        resetButton.setId("reset");
        resetButton.setStyle("-fx-base: red;");

        Label nullLabel = new Label();
        nullLabel.setPrefWidth(30);

        hBox.getChildren().addAll(new Label("Ingresa un número: "), keyText, new Label("Order: "), orderText, insertButton, deleteButton, searchButton,
                resetButton, nullLabel);
        hBox.setAlignment(Pos.CENTER);

        //Set Order
        orderText.setText("3");

        orderText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                System.out.println("Key Pressed: " + ke.getText());
                if (!(ke.getText().equalsIgnoreCase(""))) {
                    order = Integer.parseInt(ke.getText());
                    bTree.initialize(order);
                } else {
                    //orderText.setText("3");
                    bTree.initialize(3);

                }

            }
        });

        keyText.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent  ke) {
                System.out.println("Key Pressed: " + orderText.getText());
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
        //Buttons events
        insertButton.setOnMouseClicked(e -> insertValue());
        deleteButton.setOnMouseClicked(e -> deleteValue());
        searchButton.setOnMouseClicked(e -> searchValue());
        resetButton.setOnMouseClicked(e -> reset());


        // Create a scene
        Scene scene = new Scene(borderPane, 720, 360);
        scene.getStylesheets().add(getClass().getResource("BtreeStyle.css").toExternalForm());
        primaryStage.setTitle("Visualizador B+ Tree");
        primaryStage.setScene(scene);
        primaryStage.show();


    }


    private void insertValue() {
        try {
            key = Double.parseDouble(keyText.getText());
            keyText.setText("");
            bTree.setStepsTree(new LinkedList<BPlusTree>());
            bTree.insertElement(key);
            bTreeLinkedList = bTree.getStepsTree();
            int size = bTreeLinkedList.size();
            btPane.updatePane(bTreeLinkedList.get(size - 1));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Dato de entrada incorrecto!", ButtonType.OK);
            alert.show();
        }
    }


    private void deleteValue() {
        try {
            key = Double.parseDouble(keyText.getText());
            keyText.setText("");
            //if (bTree.getNode(key) == null) {
            //  throw new Exception("Not in the tree!");
            //}
            bTree.setStepsTree(new LinkedList<BPlusTree>());

            bTree.deleteElement(key);

            bTreeLinkedList = bTree.getStepsTree();
            int size = bTreeLinkedList.size();
            btPane.updatePane(bTreeLinkedList.get(size - 1));
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


    private void reset() {
        keyText.setText("");
        orderText.setText("3");
        bTree.setRoot(null);
        bTreeLinkedList.clear();
        btPane.updatePane(bTree);
    }
}