import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
     * @param order the degree of the B Plus Tree
     */
    public void initialize(int order) {
        // At initialization, order of the tree is set to m. Root is set to null
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
    /**
     * @return order
     */
    public int getOrder() {
        return this.order;
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

        Node currNode = this.root;
        // Traverse to the corresponding external node that would 'should'
        // contain starting key (key1)

        while (currNode.getChildren()[0] != null) {
            currNode = currNode.getChildren()[binarySearchWithinInternalNode(currNode.getKeys()[0], currNode.getKeys(), currNode.numKeys)];
            height++;

        }

        return height;


        // Start

    }

    /**
     * Insert a value and value pair to the B Plus Tree
     *
     * @param value the key to be inserted
     */
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

        if (!node.isLeaf) {
            rightSplit = rightSplit + 1;
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
            for (int j = 0; j < leftSplit + 1; j++) {
                leftNode.children[j] = node.children[j];
                leftNode.isLeaf = false;
                //node.children[j].parent = leftNode;
                node.children[j] = null;
            }
        }

        for (int i = 0; i < leftSplit; i++) {
            leftNode.keys[i] = node.keys[i];
            leftNode.numKeys++;
        }

        if (node.isLeaf) {
            rightNode.next = node.next;
            node.next = rightNode;
        }

        node.keys = leftNode.keys;
        node.children = leftNode.children;
        node.numKeys = leftNode.numKeys;

        leftNode = node;

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
        /*if (this.root.numKeys == 0) {
            this.root = this.root.children[0];
            this.root.parent = null;

        }*/
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
                this.stepsTree.clear();
                this.stepsTree.add(CloneUtils.clone(this));
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

    public void findElement(double value) {

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

    public List search(double key1, double key2) {
        System.out.println("Searching between keys " + key1 + ", " + key2);
        List searchKeys = new ArrayList<>();
        Node currNode = this.root;
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
                if (key >= key1 && key <= key2)
                    searchKeys.add(currNode.getKeys()[i]);
                if (currNode.getKeys()[i] > key2) {
                    endSearch = true;
                }
            }
            currNode = currNode.getNext();
        }

        return searchKeys;
    }


    /**
     * Modified Binary search within internal node.
     *
     * @param key     the key to be searched
     * @param keyList the list of keys to be searched
     * @return the first index of the list at which the key which is greater
     * than the input key
     */
    public int binarySearchWithinInternalNode(double key, Double[] keyList, int length) {
        int st = 0;
        int end = length - 1;
        int mid;
        int index = -1;
        // Return first index if key is less than the first element
        if (key < keyList[st]) {
            return 0;
        }
        // Return array size + 1 as the new positin of the key if greater than
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


    public Node getNode(double key) {
        Node currentNode = root;
        while (currentNode!=null) {
            int i = 0;
            while (i < currentNode.numKeys) {
                if (currentNode.keys[i].equals(key)) {
                    return currentNode;
                } else if (currentNode.keys[i].compareTo(key) > 0) {
                    currentNode = currentNode.children[i];
                    i = 0;
                } else {
                    i++;
                }
            }
            if (!currentNode.isNull()) {
                currentNode = currentNode.children[currentNode.numKeys];
            }
        }
        return null;
    }

}
