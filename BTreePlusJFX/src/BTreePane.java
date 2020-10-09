import javafx.animation.FillTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class BTreePane extends Pane {
    private BPlusTree bTree;
    private double originalX, originalY;

    // TODO: make node size relate to pane's size
    private final int fontSize = 14;
    private final int rectangleWidth = 36;
    private final int rowSpace = 60;

    public BTreePane(double x, double y, BPlusTree bTree) {
        this.originalX = x;
        this.originalY = y;
        this.bTree = bTree;
    }

    /*
     * Draw Tree & Node
     */
    public void updatePane(BPlusTree bTree) {
        this.getChildren().clear();
        this.bTree = bTree;
        DrawBTree(bTree.getRoot(), originalX, originalY);
    }

    private void DrawNode(String s, double x, double y, Color color) {
        Rectangle rect = new Rectangle(x, y, rectangleWidth, rectangleWidth);
        rect.setFill(color);
        rect.setStroke(Color.WHITESMOKE);
        rect.setArcHeight(10);
        rect.setArcWidth(12);
        Text txt = new Text(x + 11 - s.length(), y + 20, s);
        txt.setFill(Color.WHITE);
        txt.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, fontSize));
        this.getChildren().addAll(rect, txt);
    }

    private void DrawBTree(Node root, double x, double y) {
        if (root != null) {
            // Draw keys of node
            for (int i = 0; i < root.getSize(); i++) {
                String label = String.valueOf(root.getKeys().get(i));
                DrawNode(label, x + i * rectangleWidth, y, Color.web("#6ab5ff"));
            }
            // Draw line
            double startY = y + 2 * fontSize;
            if (!root.isLastInternalNode()) {
                for (int i = 0; i < root.getChildren().size(); i++) {
                    // startX, endX = start, end of Line
                    // startX2 = start of child nodes
                    double startX = x + i * rectangleWidth;
                    double startX2 = 0;
                    double endX = 0;

                    if ((double) i > ((double) root.getSize()) / 2) {
                        startX2 = startX
                                + (bTree.getOrder() - 1) * (bTree.getHeight(root.getChildren().get(i)) - 1) * rectangleWidth / 2;
                        endX = startX2 + ((double) root.getChildren().get(i).getSize()) / rectangleWidth;
                    } else if ((double) i < ((double) root.getSize()) / 2) {
                        endX = startX - (bTree.getOrder() - 1) * (bTree.getHeight(root.getChildren().get(i)) - 1) * rectangleWidth / 2
                                - ((double) root.getChildren().get(i).getSize()) /  rectangleWidth;
                        startX2 = endX - ((double) root.getChildren().get(i).getSize()) /rectangleWidth;
                    } else {
                        startX2 = startX - ((double) root.getChildren().get(i).getSize()) / rectangleWidth;
                        endX = startX;
                    }
                    //
                    if (i == 0) {
                        startX2 -= rectangleWidth * 2;
                        endX -= rectangleWidth * 2;
                    } else if (i == root.getSize()) {
                        startX2 += rectangleWidth * 2;
                        endX += rectangleWidth * 2;
                    }

                    // Draw child nodes
                    if (!root.getChildren().get(i).isNull()) {
                        Line line = new Line(startX, startY, endX, y + rowSpace);
                        line.setStroke(Color.SILVER);
                        line.setStrokeWidth(1.5);
                        this.getChildren().add(line);
                    }
                    DrawBTree(root.getChildren().get(i), startX2, y + rowSpace);
                }
            }
        }
    }

    public void searchPathColoring(BPlusTree bTree, double key) throws Exception {
        updatePane(bTree);
        if (!bTree.isEmpty()) {
            Node currentNode = bTree.getRoot();
            double x = originalX, y = originalY;
            double delay = 0;
            boolean isFound = false;
            while (!isFound) {//while (!currentNode.equals(bTree.nullBTNode)) {
                int i = 0;
                while (i < currentNode.getSize()) {
                    makeNodeAnimation(currentNode.getKey(i).toString(), x, y, delay);
                    delay += 1;
                    // so sanh voi key can tim
                    if (currentNode.getKey(i).equals(key)) {
                        return;
                    } else if (currentNode.getKey(i) > key) {//	} else if (currentNode.getKey(i).compareTo(key) > 0) {
                        // di xuong key ben trai
                        y += rowSpace;
                        if ((double) i < ((double) currentNode.getSize()) / 2) {
                            x = x - (bTree.getOrder() - 1) * (bTree.getHeight(currentNode.getChildren().get(i)) - 1) * rectangleWidth
                                    / 2 - ((double) currentNode.getChildren().get(i).getSize()) * rectangleWidth;
                        } else {
                            x = x - ((double) currentNode.getChildren().get(i).getSize()) / 2 * rectangleWidth;
                        }
                        if (i == 0) {
                            x -= rectangleWidth * 2;
                        }

                        currentNode = currentNode.getChildren().get(i);
                        i = 0;
                    } else {
                        // Mover a la siguiente clave en el nodo
                        i++;
                        x += rectangleWidth;
                    }
                }
                // Mueva hacia abajo la tecla a la derecha del nodo
                if (!currentNode.isNull()) {
                    y += rowSpace;
                    x = x + (bTree.getOrder() - 1) * (bTree.getHeight(currentNode.getChildren().get(i)) - 1) * rectangleWidth / 2
                            + rectangleWidth * 2;

                    currentNode = currentNode.getChildren().get(currentNode.getSize());
                }
            }
        }
        throw new Exception("Not in the tree!");
    }

    /*
     * Draw Animation
     */

    // TODO: refactor
    private void makeNodeAnimation(String s, double x, double y, double delay) {
        // Draw a node
        Rectangle rect = new Rectangle(x, y, rectangleWidth, rectangleWidth);
        rect.setFill(Color.web("#6ab5ff"));
        rect.setStroke(Color.WHITESMOKE);
        rect.setArcHeight(10);
        rect.setArcWidth(10);
        Text txt = new Text(x + 11 - s.length(), y + 20, s);
        txt.setFill(Color.WHITE);
        txt.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, fontSize));
        this.getChildren().addAll(rect, txt);

        // make fill transition
        FillTransition fill = new FillTransition();

        fill.setAutoReverse(false);
        fill.setCycleCount(1);
        fill.setDelay(Duration.seconds(delay));
        fill.setDuration(Duration.seconds(1));
        fill.setFromValue(Color.web("#6ab5ff"));
        fill.setToValue(Color.web("#f57f7f"));
        fill.setShape(rect);
        fill.play();
    }

}
