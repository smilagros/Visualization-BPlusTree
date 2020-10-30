import java.io.Serializable;
import java.util.LinkedList;

public class BPlusTree implements Serializable {

    /**
     * The degree order.
     */
    public static int order;

    /**
     * The root of the B Plus node.
     */
    private Node root;
    /**
     * The root of the B Plus node.
     */

    public int maxKeys;
    /**
     * The root of the B Plus node.
     */
    public static int minKeys = 1;
    /**
     * The root of the B Plus node.
     */
    public int splitIndex;
    /**
     * The root of the B Plus node.
     */
    public int maxDegree;


    private LinkedList<BPlusTree> stepsTree = new LinkedList<BPlusTree>();

    /**
     * Instantiates a new b plus node.
     */
    public BPlusTree() {

    }

    /**
     * Initializes the B Plus Tree. Sets the degree of the BPlus Tree as m
     *
     * @param order
     *            the degree of the B Plus Tree
     */
    public void initialize(int order) {
        // At initialization, order of the tree is set to m. Root is set to null
        // At initialization, order of the node is set to order. Root is set to null
        this.order = order;
        this.root = null;
        this.maxDegree = order;
        this.maxKeys = order - 1;
        this.minKeys = (int) Math.ceil((order + 1) / 2) - 1;
        this.splitIndex = (int) Math.ceil((order) / 2);

    }
    /**
     * @return true, if node is empty
     */
    public boolean isEmpty() {
        return this.root == null;
    }


    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * @return root node
     */
    public Node getRoot() {
        return this.root;
    }

    public int getOrder() {
        return order;
    }


    public LinkedList<BPlusTree> getStepsTree() {
        return stepsTree;
    }

    public void setStepsTree(LinkedList<BPlusTree> stepsTree) {
        this.stepsTree = stepsTree;
    }

    /**
     * @param node , the node
     * @return the height of the node position
     */
    public int getHeight(Node node) {
        int height = 1;
        Node currentNode = node;
        currentNode = currentNode.getNext();
        while (null != currentNode) {
            //printNode(currentNode);
            currentNode = currentNode.getNext();
            height++;
        }
        return height;


    }

    /*Insert Element*/
    public void insertElement(double value) {
        if (this.root == null) {
            this.root = new Node();
            this.root.keys[0] = value;
            this.root.numKeys++;
            this.stepsTree.add(CloneUtils.clone(this));

        } else {
            this.insert(this.root, value);
            this.stepsTree.clear();
             this.stepsTree.add(CloneUtils.clone(this));
        }
    }


    public void insert(Node node, double value) {

        if (node.isLeaf) {
            this.insertInto(node, value);
            this.insertValidate(node);
        } else {
            int findIndex = 0;
            while (findIndex < node.numKeys && node.keys[findIndex] < value) {
                findIndex++;
            }
            this.insert(node.children[findIndex], value);
        }
    }


    public void insertInto(Node node, double value) {
        int index = node.numKeys;
        while (index > 0 && node.keys[index - 1] > value) {
            node.keys[index] = node.keys[index - 1];
            index--;
        }
        node.keys[index] = value;
        node.numKeys++;

        //this.stepsTree.add(CloneUtils.clone(this));

    }


    public void insertValidate(Node node) {
        if (node.numKeys <= this.maxKeys) {
            return;
        } else if (node.parent == null) {
            this.root = this.split(node);
            return;
        } else {
            Node split = this.split(node);
            this.insertValidate(split);
        }
    }


    public Node split(Node node) {
        Node rightNode = new Node();
        Node leftNode = new Node();
        double midleValue = node.keys[this.splitIndex];

        int rightSplit = this.splitIndex;
        int leftSplit = this.splitIndex;

        if (node.isLeaf) {
            rightNode.next = node.next;
            node.next = rightNode;
        } else {
            rightSplit = rightSplit + 1;
            //leftSplit = leftSplit;
        }

        if (!node.isLeaf) {
            for (int i = rightSplit; i < node.numKeys + 1; i++) {
                rightNode.children[i - rightSplit] = node.children[i];
                rightNode.isLeaf = false;
                node.children[i].parent = rightNode;
                node.children[i] = null;
            }

        }
        for (int i = rightSplit; i < node.numKeys; i++) {
            rightNode.keys[i - rightSplit] = node.keys[i];
            rightNode.numKeys++;
        }

        if (!node.isLeaf) {
            for (int j = 0; j < leftSplit+1 ; j++) {
                leftNode.children[j] = node.children[j];
                leftNode.isLeaf = false;
                node.children[j].parent = leftNode;
                node.children[j] = null;
            }
        }

        for (int i = 0; i < leftSplit; i++) {
            leftNode.keys[i] = node.keys[i];
            leftNode.numKeys++;
        }

        if (node.parent != null) {
            Node currentParent = node.parent;
            int index = 0;
            while (index < currentParent.numKeys + 1 && currentParent.children[index] != node) {
                index++;
            }

            for (int i = currentParent.numKeys; i > index; i--) {
                currentParent.children[i + 1] = currentParent.children[i];
                currentParent.keys[i] = currentParent.keys[i - 1];
            }
            currentParent.numKeys++;
            currentParent.keys[index] = midleValue;
            currentParent.children[index + 1] = rightNode;
            currentParent.children[index] = leftNode;
            rightNode.parent = currentParent;
            leftNode.parent = currentParent;
            //this.stepsTree.add(CloneUtils.clone(this));

            return node.parent;

        } else {
            this.root = new Node();
            this.root.keys[0] = midleValue;
            this.root.numKeys++;
            this.root.children[0] = leftNode;
            this.root.children[1] = rightNode;
            leftNode.parent = this.root;
            rightNode.parent = this.root;
            this.root.isLeaf = false;
           // this.stepsTree.add(CloneUtils.clone(this));
            return this.root;
        }
    }

    /*Delete */
    public void deleteElement(double deletedValue) {
        this.doDelete(this.root, deletedValue);
        if (this.root.numKeys == 0) {
            this.root = this.root.children[0];
            this.root.parent = null;
        }
    }

    public void doDelete(Node node, double val) {
        if (node != null) {
            int i;
            for (i = 0; i < node.numKeys && node.keys[i] < val; i++) ;
            if (i == node.numKeys) {
                if (!node.isLeaf) {
                    this.doDelete(node.children[node.numKeys], val);
                }

            } else if (!node.isLeaf && node.keys[i] == val) {

                this.doDelete(node.children[i + 1], val);
            } else if (!node.isLeaf) {
                this.doDelete(node.children[i], val);
            } else if (node.isLeaf && node.keys[i] == val) {
                for (int j = i; j < node.numKeys - 1; j++) {
                    node.keys[j] = node.keys[j + 1];
                }
                node.numKeys--;

                if (i == 0 && node.parent != null) {
                    double nextSmallest = 0;
                    Node parentNode = node.parent;
                    int index;
                    for (index = 0; parentNode.children[index] != node; index++) ;
                    if (node.numKeys == 0) {
                        if (index == parentNode.numKeys) {
                        } else {
                            nextSmallest = parentNode.children[index + 1].keys[0];
                        }
                    } else {
                        nextSmallest = node.keys[0];
                    }
                    while (parentNode != null) {
                        if (index > 0 && parentNode.keys[index - 1] == val) {
                            parentNode.keys[index - 1] = nextSmallest;
                        }
                        Node grandParent = parentNode.parent;
                        for (index = 0; grandParent != null && grandParent.children[index] != parentNode; index++)
                            ;
                        parentNode = grandParent;

                    }

                }
                this.validateAfterDelete(node);

            }

        }
    }


    public void validateAfterDelete(Node node) {
        if (node.numKeys < this.minKeys) {
            if (node.parent == null) {
                if (node.numKeys == 0) {
                    this.root = node.children[0];
                    if (this.root != null)
                        this.root.parent = null;
                }
            } else {
                Node parentNode = node.parent;
                int index;
                for (index = 0; parentNode.children[index] != node; index++) ;

                if (index > 0 && parentNode.children[index - 1].numKeys > this.minKeys) {
                    this.stealFromLeft(node, index);

                } else if (index < parentNode.numKeys && parentNode.children[index + 1].numKeys > this.minKeys) {
                    this.stealFromRight(node, index);

                } else if (index == 0) {
                    // Merge with right sibling
                    Node nextNode = this.mergeRight(node);
                    this.validateAfterDelete(nextNode.parent);
                } else {
                    // Merge with left sibling
                    Node nextNode = this.mergeRight(parentNode.children[index - 1]);
                    this.validateAfterDelete(nextNode.parent);
                }
            }
        } else if (node.parent != null) {

        }
    }


    public Node stealFromRight(Node node, int index) {
        // Steal from right sibling
        Node parentNode = node.parent;
        Node rightSib = parentNode.children[index + 1];
        node.numKeys++;

        if (node.isLeaf) {
            node.keys[node.numKeys - 1] = rightSib.keys[0];
            parentNode.keys[index] = rightSib.keys[1];

        } else {
            node.keys[node.numKeys - 1] = parentNode.keys[index];
            parentNode.keys[index] = rightSib.keys[0];
        }

        if (!node.isLeaf) {
            node.children[node.numKeys] = rightSib.children[0];
            node.children[node.numKeys].parent = node;


            for (int i = 1; i < rightSib.numKeys + 1; i++) {
                rightSib.children[i - 1] = rightSib.children[i];
            }

        }
        for (int i = 1; i < rightSib.numKeys; i++) {
            rightSib.keys[i - 1] = rightSib.keys[i];
        }
        rightSib.numKeys--;

        return node;

    }


    public Node stealFromLeft(Node node, int index) {
        Node parentNode = node.parent;
        node.numKeys++;

        for (int i = node.numKeys - 1; i > 0; i--) {
            node.keys[i] = node.keys[i - 1];
        }
        Node leftSib = parentNode.children[index - 1];

        if (node.isLeaf) {
            node.keys[0] = leftSib.keys[leftSib.numKeys - 1];
            parentNode.keys[index - 1] = leftSib.keys[leftSib.numKeys - 1];
        } else {
            node.keys[0] = parentNode.keys[index - 1];
            parentNode.keys[index - 1] = leftSib.keys[leftSib.numKeys - 1];
        }

        if (!node.isLeaf) {
            for (int i = node.numKeys; i > 0; i--) {
                node.children[i] = node.children[i - 1];
            }
            node.children[0] = leftSib.children[leftSib.numKeys];
            leftSib.children[leftSib.numKeys] = null;
            node.children[0].parent = node;//aqui error
        }

        leftSib.numKeys--;

        return node;
    }


    public Node mergeRight(Node node) {

        Node parentNode = node.parent;
        int index;
        for (index = 0; parentNode.children[index] != node; index++) ;
        Node rightSib = parentNode.children[index + 1];

        if (node.isLeaf) {
        } else {
            node.keys[node.numKeys] = parentNode.keys[index];
        }

        for (int i = 0; i < rightSib.numKeys; i++) {
            int index1 = node.numKeys + 1 + i;
            if (node.isLeaf) {
                index1 -= 1;
            }
            node.keys[index1] = rightSib.keys[i];

        }
        if (!node.isLeaf) {
            for (int i = 0; i <= rightSib.numKeys; i++) {
                node.children[node.numKeys + 1 + i] = rightSib.children[i];
                node.children[node.numKeys + 1 + i].parent = node;

            }
            node.numKeys = node.numKeys + rightSib.numKeys + 1;

        } else {
            node.numKeys = node.numKeys + rightSib.numKeys;
            node.next = rightSib.next;

        }
        for (int i = index + 1; i < parentNode.numKeys; i++) {
            parentNode.children[i] = parentNode.children[i + 1];
            parentNode.keys[i - 1] = parentNode.keys[i];
        }
        parentNode.numKeys--;

        return node;
    }


    /*find*/

    public void findElement( double value)
    {

        this.findInTree(this.root, value);

    }
    public void findInTree(Node node, double val) {
        if (node != null) {
            int i;
            for (i = 0; i < node.numKeys && node.keys[i] < val; i++) ;
            if (i == node.numKeys) {
                if (!node.isLeaf) {
                    this.findInTree(node.children[node.numKeys], val);
                } else {
                }
            } else if (node.keys[i] > val) {
                if (!node.isLeaf) {
                    System.out.println("Step");
                    System.out.println(node.children[i]);
                    this.findInTree(node.children[i], val);
                } else {
                    System.out.println("Element " + val + " is not in the tree");
                }
            } else {
                if (node.isLeaf) {
                    System.out.println("Element " + val + " found");
                    System.out.println("Step");

                } else {
                    System.out.println("Step");
                    System.out.println(node.children[i + 1]);
                    this.findInTree(node.children[i + 1], val);
                }
            }
        } else {
            System.out.println("Element " + val + " is not in the tree");
        }
    }



    /*Print Console */

    public void printTree(Node node, int level) {
        if (node != null) {
            int i = node.numKeys - 1;
            while (i >= 0) {
                printTree(node.children[i + 1], level + 1);
                String mensaje = "";
                for (int j = 0; j < level; j++) {
                    mensaje += "\t";
                }
                System.out.println(mensaje + "|" + node.keys[i]);
                i -= 1;
            }
            printTree(node.children[0], level + 1);
        }
    }

    public void print() {
        System.out.println("****************************");
        printTree(this.root, 0);
    }

    /*public void  printTree()
    {

        this.commands = new Array();
        this.cmd("SetText", this.messageID, "Printing tree");
        var firstLabel = this.nextIndex;

        if (this.treeRoot != null)
        {
            this.xPosOfNextLabel = FIRST_PRINT_POS_X;
            this.yPosOfNextLabel = this.first_print_pos_y;

            var tmp = this.treeRoot;

            this.cmd("SetHighlight", tmp.graphicID, 1);
            this.cmd("Step");
            while (!tmp.isLeaf)
            {
                this.cmd("SetEdgeHighlight", tmp.graphicID, tmp.children[0].graphicID, 1);
                this.cmd("Step");
                this.cmd("SetHighlight", tmp.graphicID, 0);
                this.cmd("SetHighlight", tmp.children[0].graphicID, 1);
                this.cmd("SetEdgeHighlight", tmp.graphicID, tmp.children[0].graphicID, 0);
                this.cmd("Step");
                tmp = tmp.children[0];
            }

            while (tmp!= null)
            {
                this.cmd("SetHighlight", tmp.graphicID, 1);
                for (i = 0; i < tmp.numKeys; i++)
                {
                    var nextLabelID = this.nextIndex++;
                    this.cmd("CreateLabel", nextLabelID, tmp.keys[i], this.getLabelX(tmp, i), tmp.y);
                    this.cmd("SetForegroundColor", nextLabelID, PRINT_COLOR);
                    this.cmd("Move", nextLabelID, this.xPosOfNextLabel, this.yPosOfNextLabel);
                    this.cmd("Step");
                    this.xPosOfNextLabel +=  PRINT_HORIZONTAL_GAP;
                    if (this.xPosOfNextLabel > PRINT_MAX)
                    {
                        this.xPosOfNextLabel = FIRST_PRINT_POS_X;
                        this.yPosOfNextLabel += PRINT_VERTICAL_GAP;
                    }
                }
                if (tmp.next != null)
                {
                    this.cmd("SetEdgeHighlight", tmp.graphicID, tmp.next.graphicID, 1);
                    this.cmd("Step");
                    this.cmd("SetEdgeHighlight", tmp.graphicID, tmp.next.graphicID, 0);
                }
                this.cmd("SetHighlight", tmp.graphicID, 0);
                tmp = tmp.next;
            }
            this.cmd("Step");
            for (var i = firstLabel; i < this.nextIndex; i++)
            {
                this.cmd("Delete", i);
            }
            this.nextIndex = firstLabel;
        }
        this.cmd("SetText", this.messageID, "");
        return this.commands;
    }*/



}
