import javafx.animation.FillTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class BTreePane extends Pane {
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
    }

    /*
     * Draw Tree & Node
     */
    public void updatePane(BPlusTree bTree) {
        this.getChildren().clear();
        DrawBTree(bTree.getRoot(), originalX, originalY);
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

    private void DrawBTree(Node node, double x, double y) {
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
                        startX2 = node.children[i].x;
                        endX = startX2 + ((double) node.children[i].numKeys) / 2 * rectangleWidth;
                    } else if ((double) i < ((double) node.getSize()) / 2) {
                        endX = node.children[i].x + (node.children[i].numKeys * rectangleWidth) / 2;
                    } else {
                        endX = node.children[i].x;
                    }


                    // Draw child nodes
                    if (node.getChildren()[0] != null) {
                        Line line = new Line(startX, startY, endX, y + rowSpace);
                        line.setStroke(Color.SILVER);
                        line.setStrokeWidth(1.5);
                        this.getChildren().add(line);
                    }

                    if (node.children[i].isLeaf) {
                        System.out.println("Ingresa a hoja  " + i);

                        Node next = node.children[i].next;
                        if(next != null){

                            Arrow arrow = new Arrow(node.children[i].x, node.children[i].y +rectangleWidth/2, next.x, next.y+rectangleWidth/2);
                            this.getChildren().add(arrow);
                        }
                    }
                    DrawBTree(node.children[i], node.children[i].x, node.children[i].y);
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
        Node curr = bTree.getRoot();
        if (curr != null) {
            double delay = 0;
            // Traverse to the corresponding external node that would 'should'
            // contain this key
            while (curr.getChildren().length != 0) {
                int index = binarySearchWithinInternalNode(key, curr.keys, curr.numKeys);
                for (int i = 0; i < index; i++) {
                    if (curr.getKey(i).equals(key) && curr.isLeaf) {
                        makeNodeAnimation(curr.getKey(i).toString(), curr.x + i * rectangleWidth, curr.y, delay);
                        return;
                    } else {
                        makeNodeAnimation(curr.getKey(i).toString(), curr.x + i * rectangleWidth, curr.y, delay);
                        delay += 0.5;
                    }
                }
                if (index == 0) {
                    makeNodeAnimation(curr.getKey(0).toString(), curr.x + 0 * rectangleWidth, curr.y, delay);
                    delay += 0.5;
                }
                System.out.println("index" + index);
                curr = curr.children[binarySearchWithinInternalNode(key, curr.keys, curr.numKeys)];
            }

            throw new Exception("Not in the tree!");
        }
    }

    public void searchPathColoring2(BPlusTree bTree, double key1, double key2) throws Exception {
        updatePane(bTree);
        Node currNode = bTree.getRoot();
        if (currNode != null) {
            double delay = 0;
            System.out.println("Searching between keys " + key1 + ", " + key2);
            List searchKeys = new ArrayList<>();
            // Traverse to the corresponding external node that would 'should'
            // contain starting key (key1)

            while (currNode.getChildren()[0] != null) {
                currNode = currNode.getChildren()[binarySearchWithinInternalNode(key1, currNode.getKeys(), currNode.numKeys)];
            }

            // Start from current node and add keys whose value lies between key1 and key2 with their corresponding pairs
            // Stop if end of list is encountered or if value encountered in list is greater than key2

            boolean endSearch = false;
            while (null != currNode && !endSearch) {
                for (int i = 0; i < currNode.numKeys; i++) {
                    Double key = currNode.getKeys()[i];
                    makeNodeAnimation(currNode.getKey(i).toString(), currNode.x + i * rectangleWidth, currNode.y, delay);

                    if (key >= key1 && key <= key2)
                        searchKeys.add(currNode.getKeys()[i]);
                    delay += 0.5;
                    if (currNode.getKeys()[i] > key2) {

                        endSearch = true;
                    }
                }
                currNode = currNode.getNext();
            }




            throw new Exception("Not in the tree!");
        }


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


    public int binarySearchWithinInternalNode(double key, Double[] keyList, int length) {
        int st = 0;
        int end = length - 1;
        int mid;
        int index = -1;
        // Return first index if key is less than the first element
        if (key < keyList[st]) {
            return 0;
        }
        // Return array size + 1 as the new position of the key if greater than
        // last element
        if (key >= keyList[end]) {
            return length;
        }
        while (st <= end) {
            mid = (st + end) / 2;
            // Following condition ensures that we find a location s.t. key is
            // smaller than element at that index and is greater than or equal
            // to the element at the previous index. This location is where the
            // key would be inserted
            if (key < keyList[mid] && key >= keyList[mid - 1]) {
                index = mid;
                break;
            } // Following conditions follow normal Binary Search
            else if (key >= keyList[mid]) {
                st = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        return index;
    }

}