package tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Class BPlus Tree
 */
public class BPlusTree {

    /**
     * The degree order.
     */
    public static int order;
    /**
     * The minimum numbers of Keys in the node
     */

    public static int minKeys;
    /**
     * The maximum numbers of Keys in the node
     */

    public int maxKeys;
    /**
     * Split Position
     */
    public int splitIndex;
    /**
     * The root of the B Plus node.
     */
    private Node root;

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
        this.maxKeys = order - 1;
        this.minKeys = (int) Math.ceil((order + 1) / 2) - 1;
        this.splitIndex = (int) Math.ceil((order) / 2);

    }

    /**
     * Get root
     *
     * @return root node
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * Set Root Node
     *
     * @param root
     */
    public void setRoot(Node root) {
        this.root = root;
    }


    /**
     * Insert a value and value pair to the B Plus Tree
     *
     * @param key the key to be inserted
     */
    public void insertElement(double key) {
        if (this.root == null) {
            this.root = new Node();
            this.root.keys[0] = new Key(key, generateString());
            this.root.numKeys++;

        } else {
            this.insert(this.root, key);
        }
    }


    /**
     * Insert value
     *
     * @param node
     * @param key
     */
    public void insert(Node node, double key) {

        if (node.isLeaf) {
            this.insertInto(node, key);
            this.insertValidate(node);
        } else {
            int findIndex = 0;
            while (findIndex < node.numKeys && node.keys[findIndex].getKey() < key) {
                findIndex++;
            }
            this.insert(node.children[findIndex], key);
        }
    }


    /**
     * Insert value into the node
     *
     * @param node
     * @param key
     */
    public void insertInto(Node node, double key) {
        int index = node.numKeys;
        while (index > 0 && node.keys[index - 1].getKey() > key) {
            node.keys[index] = node.keys[index - 1];
            index--;
        }

        node.keys[index] = new Key(key, generateString());//value;
        node.numKeys++;
    }


    /**
     * Validate node
     *
     * @param node
     */
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


    /**
     * Split node
     *
     * @param node
     * @return
     */
    public Node split(Node node) {
        //Create new Nodes
        Node rightNode = new Node();
        Node leftNode = new Node();
        //Calculate midle value
        double midleValue = node.keys[this.splitIndex].getKey();

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
            currentParent.keys[index] = new Key(midleValue, "");//midleValue;
            currentParent.children[index + 1] = rightNode;
            currentParent.children[index] = leftNode;
            rightNode.parent = currentParent;
            leftNode.parent = currentParent;

            return node.parent;

        } else {

            this.root = new Node();
            this.root.keys[0] = new Key(midleValue, "");//midleValue;
            this.root.numKeys++;
            this.root.children[0] = leftNode;
            this.root.children[1] = rightNode;
            leftNode.parent = this.root;
            rightNode.parent = this.root;
            this.root.isLeaf = false;

            return this.root;
        }
    }

    /**
     * Delete key from node
     *
     * @param deletedValue
     */
    public void deleteElement(double deletedValue) {
        this.doDelete(this.root, deletedValue);

    }

    /**
     * Delete
     *
     * @param node
     * @param key  the key to be deleted
     */
    public void doDelete(Node node, double key) {
        //The tree isn't empty
        if (node != null) {
            //Find node
            int i = 0;
            while (i < node.numKeys && node.keys[i].getKey() < key) {
                i++;
            }
            //Call recursive to doDelete when the key is mayor a todos los valores del arreglo
            if (i == node.numKeys) {
                if (!node.isLeaf) {
                    this.doDelete(node.children[node.numKeys], key);
                }
            }
            //Find key in internal node when
            else if (!node.isLeaf && node.keys[i].getKey() == key) {
                //Delete key in the right children
                this.doDelete(node.children[i + 1], key);
                //Delete in internal node
            } else if (!node.isLeaf) {
                this.doDelete(node.children[i], key);
                //Node is leaf when
            } else if (node.isLeaf && node.keys[i].getKey() == key) {
                //Delete key
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
                        if (!(index == parentNode.numKeys)) {
                            nextSmallest = parentNode.children[index + 1].keys[0].getKey();
                        }
                    } else {
                        nextSmallest = node.keys[0].getKey();
                    }
                    while (parentNode != null) {
                        if (index > 0 && parentNode.keys[index - 1].getKey() == key) {
                            parentNode.keys[index - 1] = new Key(nextSmallest, "");
                        }
                        Node grandParent = parentNode.parent;

                        parentNode = grandParent;
                    }

                }
                //Validate node :quantity minimal of keys
                this.validateAfterDelete(node);
            }

        }
    }


    /**
     * Validate node after delete
     *
     * @param node
     */
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
                int index = 0;
                while (parentNode.children[index] != node) {
                    index++;
                }
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
        }
    }


    /**
     * @param node
     * @param index
     * @return
     */
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


    /**
     * @param node
     * @param index
     * @return
     */
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
            node.children[0].parent = node;
        }
        leftSib.numKeys--;

        return node;
    }


    /**
     * Merge node
     *
     * @param node
     * @return
     */
    public Node mergeRight(Node node) {

        Node parentNode = node.parent;
        int index = 0;
        while (parentNode.children[index] != node) {
            index++;
        }

        Node rightSib = parentNode.children[index + 1];

        if (!node.isLeaf) {
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


    /**
     * Print in console
     *
     * @param node  Start Node
     * @param level
     */
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

    /**
     * Print Console
     */
    public void print() {
        System.out.println("****************************");
        printTree(this.root, 0);
    }

    /**
     * Search between two keys
     *
     * @param key1
     * @param key2
     * @return
     */
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
                Double key = currNode.getKey(i);
                if (key >= key1 && key <= key2)
                    searchKeys.add(currNode.getKeys()[i]);
                if (currNode.getKey(i) > key2) {
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
    public int binarySearchWithinInternalNode(double key, Key[] keyList, int length) {
        int st = 0;
        int end = length - 1;
        int mid;
        int index = -1;
        // Return first index if key is less than the first element
        if (key < keyList[st].getKey()) {
            return 0;
        }
        // Return array size + 1 as the new positin of the key if greater than
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


    /**
     * @param key the key to be searched
     * @return Node asociated to key
     */
    public Node getNode(double key) {
        Node currentNode = root;
        while (currentNode != null) {
            int i = 0;
            while (i < currentNode.numKeys) {
                if (currentNode.getKey(i).equals(key)) {
                    return currentNode;
                } else if (currentNode.getKey(i).compareTo(key) > 0) {
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

    public String generateString() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int sizeString = 2;
        StringBuilder sb = new StringBuilder(sizeString);
        Random random = new Random();
        for (int i = 0; i < sizeString; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        //System.out.println(output);
        return output;
    }

}

