package visualization;

import javafx.animation.FillTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tree.BPlusTree;
import tree.Node;
import tree.Key;

/**
 * B TreePane Class
 */
public class BTreePane extends Pane {
    private final int fontSize = 14;
    private final int rectangleWidth = 42;
    private final int rowSpace = 60;
    private final int WIDTH_PER_ELEM = 40;
    private final int NODE_SPACING = 40;
    private final int HEIGHT_DELTA = 50;
    private double originalX, originalY;

    public BTreePane(double x, double y) {
        this.originalX = x;
        this.originalY = y;
    }

    /**
     * @param bTree
     * @param x
     */
    /*
     * Draw Tree & tree.Node
     */
    public void updatePane(BPlusTree bTree, double x) {
        this.getChildren().clear();
        this.originalX = x / 2;
        DrawBTree(bTree.getRoot(), originalX, originalY);

    }

    /**
     * @param bTree
     */
    public void updatePane(BPlusTree bTree) {
        this.getChildren().clear();
        DrawBTree(bTree.getRoot(), originalX, originalY);
    }

    /**
     * Draw Node
     *
     * @param s     Label
     * @param x     coordenate
     * @param y     coordenate
     * @param color font
     */
    private void DrawNode(String s, double x, double y, Color color) {

        Rectangle rect = new Rectangle(x, y, rectangleWidth, rectangleWidth);
        String label = normalizeNumber(s, 4);
        rect.setFill(color);
        rect.setStroke(Color.DARKGREEN);
        rect.setArcHeight(10);
        rect.setArcWidth(12);
        Text txt = new Text(x + 10 - label.length(), y + 20, label);
        txt.setFill(Color.DARKGREEN);
        txt.setFont(Font.font("Arial", FontWeight.MEDIUM, fontSize));
        this.getChildren().addAll(rect, txt);
    }

    /**
     * Normalize number
     *
     * @param input
     * @param maxLen
     * @return
     */

    public static String normalizeNumber(String input, int maxLen) {
        int len = input.length();
        double numEntero = Double.parseDouble(input);
        if (numEntero >= 0) {

            StringBuffer dest = new StringBuffer(len);

            int toAdd = maxLen - len;
            for (int j = 0; j < toAdd; j++) {
                dest.append('0');
            }
            for (int i = 0; i < len; i++) {
                dest.append(input.charAt(i));
            }
            return dest.toString();
        }

        return input;
    }


    /**
     * Draw Tree
     *
     * @param node
     * @param x
     * @param y
     */
    private void DrawBTree(Node node, double x, double y) {
        if (node != null) {
            double result = resizeWidths(node);

            if (result > x * 2) {
                x = result / 2;
            }
            this.setNewPositions(node, x, y);
            // Draw keys of node
            for (int i = 0; i < node.numKeys; i++) {
                double number = node.getKey(i);
                long iPart = (long) number;
                String label = String.valueOf(iPart);
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
                        //System.out.println("Ingresa a hoja  " + i);

                        Node next = node.children[i].next;
                        if (next != null) {

                            Arrow arrow = new Arrow(node.children[i].x, node.children[i].y + rectangleWidth / 2, next.x, next.y + rectangleWidth / 2);
                            this.getChildren().add(arrow);
                        }
                    }
                    DrawBTree(node.children[i], node.children[i].x, node.children[i].y);
                }
            }
        }
    }

    /**
     * Calculate widths     *
     *
     * @param node
     * @return
     */
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


    /**
     * Calculate position
     *
     * @param tree
     * @param xPosition
     * @param yPosition
     */
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


    /**
     * Draw search path     *
     *
     * @param bTree
     * @param key   the key to be search
     * @throws Exception
     */
    public void searchPathColoring(BPlusTree bTree, double key) throws Exception {
        updatePane(bTree);
        Node curr = bTree.getRoot();
        if (curr != null) {
            double delay = 0;
            // Traverse to the corresponding external node that would 'should'
            // contain this key
            while (curr != null && curr.getChildren().length != 0) {
                int index = binarySearchWithinInternalNode(key, curr.getKeys(), curr.numKeys);

                //Animation
                for (int i = 0; i < index; i++) {
                    double number = curr.getKey(i);
                    long iPart = (long) number;
                    String label = String.valueOf(iPart);

                    if (curr.getKey(i).equals(key) && curr.isLeaf) {
                        makeNodeAnimation(label, curr.x + i * rectangleWidth, curr.y, delay);
                        return;
                    } else {
                        makeNodeAnimation(label, curr.x + i * rectangleWidth, curr.y, delay);
                        delay += 0.5;
                    }
                }
                if (index == 0) {
                    double number = curr.getKey(0);
                    long iPart = (long) number;
                    String label = String.valueOf(iPart);
                    makeNodeAnimation(label, curr.x + 0 * rectangleWidth, curr.y, delay);
                    delay += 0.5;
                }
                //Next
                curr = curr.children[index];
            }


            throw new Exception("Not in the tree!");
        }
    }

    /**
     * Search between two keys
     *
     * @param bTree
     * @param key1  the key1 to be search
     * @param key2  the key2 to be search
     * @throws Exception
     */
    public void searchPathColoring2(BPlusTree bTree, double key1, double key2) throws Exception {
        updatePane(bTree);
        Node currNode = bTree.getRoot();
        if (currNode != null) {
            double delay = 0;
            System.out.println("Searching between keys " + key1 + ", " + key2);
            // Traverse to the corresponding external node that would 'should'
            // contain starting key (key1)
            while (currNode != null && currNode.getChildren()[0] != null) {
                int index = binarySearchWithinInternalNode(key1, currNode.getKeys(), currNode.numKeys);
                currNode = currNode.getChildren()[index];
            }
            // Start from current node and add keys whose value lies between key1 and key2 with their corresponding pairs
            // Stop if end of list is encountered or if value encountered in list is greater than key2
            while (currNode != null) {
                for (int i = 0; i < currNode.numKeys; i++) {
                    Double key = currNode.getKey(i);
                    double number = currNode.getKey(i);
                    long iPart = (long) number;
                    String label = String.valueOf(iPart);

                    if (key >= key1 && key <= key2) {
                        makeNodeAnimation(label, currNode.x + i * rectangleWidth, currNode.y, delay);
                        delay += 0.5;
                    }
                    if (currNode.getKey(i)> key2) {
                        return;
                    }
                }

                currNode = currNode.getNext();
            }

            throw new Exception("Not in the tree!");
        }


    }


    /**
     * Draw insert path
     *
     * @param bTree
     * @param key   the key to search
     */
    public void searchPathColoring3(BPlusTree bTree, double key) {
        updatePane(bTree);
        Node curr = bTree.getRoot();
        if (curr != null) {
            double delay = 0;
            // Traverse to the corresponding external node that would 'should'
            // contain this key
            while (curr != null && curr.getChildren().length != 0) {
                int index = binarySearchWithinInternalNode(key, curr.keys, curr.numKeys);
                for (int i = 0; i < index; i++) {
                    double number = curr.getKey(i);
                    long iPart = (long) number;
                    String label = String.valueOf(iPart);

                    if (curr.getKey(i).equals(key) && curr.isLeaf) {
                        makeNodeAnimation3(label, curr.x + i * rectangleWidth, curr.y, delay);
                        return;
                    } else {
                        makeNodeAnimation3(label, curr.x + i * rectangleWidth, curr.y, delay);
                        delay += 0.5;
                    }
                }
                if (index == 0) {
                    double number = curr.getKey(0);
                    long iPart = (long) number;
                    String label = String.valueOf(iPart);
                    makeNodeAnimation3(label, curr.x + 0 * rectangleWidth, curr.y, delay);
                    delay += 0.5;
                }
                //System.out.println("index" + index);
                curr = curr.children[index];
            }

        }
    }

    /*
     * Draw Animation
     * @param s Label
     * @param x position
     * @param y position
     * @param delay
     */
    // TODO: refactor
    private void makeNodeAnimation(String s, double x, double y, double delay) {
        // Draw a node
        Rectangle rect = new Rectangle(x, y, rectangleWidth, rectangleWidth);
        String label = normalizeNumber(s, 4);
        rect.setFill(Color.web("#f57f7f"));
        rect.setArcHeight(10);
        rect.setArcWidth(10);
        Text txt = new Text(x + 10 - label.length(), y + 20, label);
        txt.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, fontSize));
        this.getChildren().addAll(rect, txt);

        // make fill transition
        FillTransition fill = new FillTransition();

        fill.setAutoReverse(false);
        fill.setCycleCount(1);
        fill.setDelay(Duration.seconds(delay));
        fill.setDuration(Duration.seconds(1));
        rect.setStroke(Color.ORANGE);
        rect.setStrokeWidth(2);
        DropShadow e = new DropShadow();
        e.setWidth(10);
        e.setHeight(10);
        e.setColor(Color.YELLOW);
        rect.setEffect(e);
        fill.setToValue(Color.TRANSPARENT);
        txt.setFill(Color.DARKGREEN);
        fill.setShape(rect);
        fill.play();
    }

    /**
     * @param s
     * @param x
     * @param y
     * @param delay
     */
    private void makeNodeAnimation3(String s, double x, double y, double delay) {
        // Draw a node
        Rectangle rect = new Rectangle(x, y, rectangleWidth, rectangleWidth);
        String label = normalizeNumber(s, 4);
        rect.setFill(Color.valueOf("#f57f7f"));
        rect.setArcHeight(10);
        rect.setStroke(Color.DARKGREEN);
        rect.setArcWidth(10);
        Text txt = new Text(x + 10 - label.length(), y + 20, label);
        txt.setFill(Color.WHITE);
        txt.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize));
        this.getChildren().addAll(rect, txt);

        // make fill transition
        FillTransition fill = new FillTransition();

        fill.setAutoReverse(false);
        fill.setCycleCount(1);
        fill.setDelay(Duration.seconds(delay));
        fill.setDuration(Duration.seconds(1));

        fill.setToValue(Color.valueOf("#DDEEDD"));
        txt.setFill(Color.DARKGREEN);
        fill.setShape(rect);
        fill.play();
    }

    /**
     * @param key
     * @param keyList
     * @param length
     * @return
     */
    public int binarySearchWithinInternalNode(double key, Key[] keyList, int length) {
        int st = 0;
        int end = length - 1;
        int mid;
        int index = -1;
        // Return first index if key is less than the first element
        if (key < keyList[st].getKey()) {
            return 0;
        }
        // Return array size + 1 as the new position of the key if greater than
        // last element
        if (key >= keyList[end].getKey()) {
            return length;
        }
        while (st <= end) {
            mid = (st + end) / 2;
            // Following condition ensures that we find a location s.t. key is
            // smaller than element at that index and is greater than or equal
            // to the element at the previous index. This location is where the
            // key would be inserted
            if (key < keyList[mid].getKey() && key >= keyList[mid - 1].getKey()) {
                index = mid;
                break;
            } // Following conditions follow normal Binary Search
            else if (key >= keyList[mid].getKey()) {
                st = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        return index;
    }

}