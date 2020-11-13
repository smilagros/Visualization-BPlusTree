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
    private final int WIDTH_PER_ELEM = 40;
    private final int NODE_SPACING = 30;
    private final int HEIGHT_DELTA = 50;


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
        DrawBTree(bTree.getRoot(), originalX, originalY, 2);
    }

    private void DrawNode(String s, double x, double y, Color color) {
        Rectangle rect = new Rectangle(x, y, rectangleWidth, rectangleWidth);
        rect.setFill(color);
        rect.setStroke(Color.DARKGREEN);
        rect.setArcHeight(10);
        rect.setArcWidth(12);
        Text txt = new Text(x + 11 - s.length(), y + 20, s);
        txt.setFill(Color.BLACK);
        txt.setFont(Font.font("Arial", FontWeight.MEDIUM, fontSize));
        this.getChildren().addAll(rect, txt);
    }

    private void DrawBTree(Node node, double x, double y, int h) {

        if (node != null) {
            this.resizeWidths(node);
            this.setNewPositions(node, x, y);
            // Draw keys of node
            for (int i = 0; i < node.numKeys; i++) {
                String label = String.valueOf(node.keys[i]);
                DrawNode(label, node.x + i * rectangleWidth, node.y, Color.web("#DDEEDD"));

            }


            // Draw line
            double startY = node.y + 2 * fontSize;
            int numChildren = node.numKeys + 1;
            if (node.getChildren()[0] != null) {
                for (int i = 0; i < numChildren; i++) {

                    double startX = node.x + i * rectangleWidth;
                    double startX2 = 0, endX = 0;

                    if ((double) i > ((double) node.numKeys) / 2) {
                        // TODO: fix
                        startX2 =  node.children[i].x;
                        endX = startX2 + ((double) node.children[i].numKeys) / 2 * rectangleWidth;
                    } else if ((double) i < ((double) node.getSize()) / 2) {
                        endX = node.children[i].x + (node.children[i].numKeys * rectangleWidth)/2;
                    } else {
                        endX =  node.children[i].x;
                    }


                    // Draw child nodes
                    if (node.getChildren()[0] != null) {
                        Line line = new Line(startX, startY, endX, y + rowSpace);
                        line.setStroke(Color.SILVER);
                        line.setStrokeWidth(1.5);
                        this.getChildren().add(line);
                    }

                    DrawBTree(node.children[i], node.children[i].x, node.children[i].y, h * 2);
                }
            }
        }
    }



    public double resizeWidths(Node node) {
        int width;
        if (node == null) {
            return 0;
        }
        if (node.isLeaf) {
            for (int i = 0; i < node.numKeys + 1; i++) {
                node.widths[i] = 0;
            }
            width = node.numKeys * WIDTH_PER_ELEM + NODE_SPACING;
            return width;
        } else {
            double treeWidth = 0;
            for (int i = 0; i < node.numKeys + 1; i++) {
                node.widths[i] = this.resizeWidths(node.children[i]);
                treeWidth = treeWidth + node.widths[i];
            }
            treeWidth = Math.max(treeWidth, node.numKeys * WIDTH_PER_ELEM + NODE_SPACING);
            node.width = treeWidth;
            return treeWidth;
        }
    }


    public void setNewPositions(Node tree, double xPosition, double yPosition) {
        if (tree != null) {
            tree.y = yPosition;
            tree.x = xPosition;
            if (!tree.isLeaf) {
                double leftEdge = xPosition - tree.width / 2;
                double priorWidth = 0;
                for (int i = 0; i < tree.numKeys + 1; i++) {
                    this.setNewPositions(tree.children[i], leftEdge + priorWidth + tree.widths[i] / 2, yPosition + HEIGHT_DELTA);
                    priorWidth += tree.widths[i];
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
            while (currentNode != null) {
                int i = 0;
                while (i < currentNode.getSize()) {
                    makeNodeAnimation(currentNode.getKey(i).toString(), x, y, delay);
                    delay += 1;
                    if (currentNode.getKey(i).equals(key)) {
                        return;
                    } else if (currentNode.getKey(i) > key) {//	} else if (currentNode.getKey(i).compareTo(key) > 0) {
                        y += rowSpace;
                        if (i < currentNode.getSize() / 2) {
                            x = x - (bTree.getOrder() - 1) * (bTree.getHeight(currentNode.children[i]) - 1) * rectangleWidth
                                    / 2 - (currentNode.children[i].getSize()) * rectangleWidth;
                        } else {
                            x = x - (currentNode.children[i].getSize()) / 2 * rectangleWidth;
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
