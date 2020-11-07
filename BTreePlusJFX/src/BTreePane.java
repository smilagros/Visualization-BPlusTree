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
        rect.setStroke(Color.AQUAMARINE);
        rect.setArcHeight(10);
        rect.setArcWidth(12);
        Text txt = new Text(x + 11 - s.length(), y + 20, s);
        txt.setFill(Color.BLACK);
        txt.setFont(Font.font("Arial", FontWeight.MEDIUM, fontSize));
        this.getChildren().addAll(rect, txt);
    }

    private void DrawBTree(Node node, double x, double y) {
        if (node != null) {
            // Draw keys of node
            for (int i = 0; i < node.numKeys; i++) {
                String label = String.valueOf(node.keys[i]);
                DrawNode(label, x + i * rectangleWidth, y, Color.web("#DDEEDD"));
            }
            // Draw line
            double startY = y + 2 * fontSize;
            if (node.getChildren()[0] != null) {
                for (int i = 0; i < node.numKeys + 1; i++) {
                    // startX, endX = start, end of Line
                    // startX2 = start of child nodes
                    double startX = x + i * rectangleWidth;
                    double startX2 = 0;
                    double endX = 0;

                    if (i > node.getSize() / 2) {
                        startX2 = startX  + ((bTree.getOrder() - 1) * (bTree.getHeight(node.children[i]) - 1) * rectangleWidth) / 2;
                        endX = startX2 + node.children[i].numKeys/ rectangleWidth;
                    } else if (i < node.getSize() / 2) {
                        endX = startX - ((bTree.getOrder() - 1) * (bTree.getHeight(node.children[i]) - 1) * rectangleWidth )/ 2
                                - (node.children[i].numKeys) / rectangleWidth;
                        startX2 = endX - node.children[i].numKeys/ rectangleWidth;
                    } else {
                        startX2 = startX - node.children[i].numKeys / rectangleWidth;
                        endX = startX;
                    }
                    //
                    if (i == 0) {
                        startX2 -= rectangleWidth * 2;
                        endX -= rectangleWidth * 2;
                    } else if (i == node.getSize()) {
                        startX2 += rectangleWidth * 2;
                        endX += rectangleWidth * 2;
                    }

                    // Draw child nodes
                    if (node.getChildren()[0] != null) {
                        Line line = new Line(startX, startY, endX, y + rowSpace);
                        line.setStroke(Color.SILVER);
                        line.setStrokeWidth(1.5);
                        this.getChildren().add(line);
                    }
                    DrawBTree(node.children[i], startX2, y + rowSpace);
                }
            }
        }
    }


    /*public int getWidth(int numLabels) {
        if (numLabels > 0) {
            return (this.widthPerElement * numLabels);
        } else {
            return MIN_WIDTH;
        }
    }*/

    public void searchPathColoring(BPlusTree bTree, double key) throws Exception {
        updatePane(bTree);
        if (!bTree.isEmpty()) {
            Node currentNode = bTree.getRoot();
            double x = originalX, y = originalY;
            double delay = 0;
            while  (currentNode!= null) {
                int i = 0;
                while (i < currentNode.getSize()) {
                    makeNodeAnimation(currentNode.getKey(i).toString(), x, y, delay);
                    delay += 1;
                    if (currentNode.getKey(i).equals(key)) {
                        return;
                    } else if (currentNode.getKey(i) > key) {//	} else if (currentNode.getKey(i).compareTo(key) > 0) {
                        y += rowSpace;
                        if (i < currentNode.getSize()/ 2) {
                            x = x - (bTree.getOrder() - 1) * (bTree.getHeight(currentNode.children[i]) - 1) * rectangleWidth
                                    / 2 - ((double) currentNode.children[i].getSize()) * rectangleWidth;
                        } else {
                            x = x - ((double) currentNode.children[i].getSize()) / 2 * rectangleWidth;
                        }
                        if (i == 0) {
                            x -= rectangleWidth * 2;
                        }

                        currentNode = currentNode.children[i];
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
                    x = x + (bTree.getOrder() - 1) * (bTree.getHeight(currentNode.children[i]) - 1) * rectangleWidth / 2
                            + rectangleWidth * 2;

                    currentNode = currentNode.children[currentNode.getSize()];
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
        rect.setFill(Color.web("#DDEEDD"));
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
        fill.setFromValue(Color.web("#DDEEDD"));
        fill.setToValue(Color.web("#f57f7f"));
        fill.setShape(rect);
        fill.play();
    }

}
