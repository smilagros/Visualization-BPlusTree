import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * The BPlusTree Class.
 */

public class BPlusTree implements Serializable {

    /**
     * The degree order.
     */
    private int order;

    /**
     * The root of the B Plus Tree.
     */
    private Node root;


    /**
     * Instantiates a new b plus tree.
     */
    public BPlusTree() {

    }

    /**
     * Initializes the B Plus Tree. Sets the degree of the BPlus Tree as order
     *
     * @param order the degree of the B Plus Tree
     */
    public void initialize(int order) {
        // At initialization, order of the tree is set to order. Root is set to null
        this.order = order;
        this.root = null;

        // System.out.println("B+ Tree Initialized. Degree is " + order);
    }

    /////////////////////////////////////////////////////////////////////////////7
    private LinkedList<BPlusTree> stepTrees = new LinkedList<BPlusTree>();
    public final Node nullBTNode = new Node();

    /**
     * @param node , the node
     * @return the height of the node position
     */
    public int getHeight(Node node) {
        int height = 1;
        Node curr = node;
        curr = curr.getNext();
        while (null != curr) {
            printNode(curr);
            curr = curr.getNext();
            height++;
        }
        return height;


    }

    /**
     * @return true, if tree is empty
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


    public LinkedList<BPlusTree> getStepTrees() {
        return stepTrees;
    }

    public void setStepTrees(LinkedList<BPlusTree> stepTrees) {
        this.stepTrees = stepTrees;
    }

////////////////////////////////////////////////////////////////////////////////77


    /**
     * Insert a key to the B Plus Tree
     *
     * @param key the key to be inserted
     */
    public void insert(double key) {

        // Case 1: Inserting to an BPlus Tree Empty
        if (this.root == null) {
            System.out.println("Case 1");
            Node newNode = new Node();
            newNode.getKeys().add(key);
            this.root = newNode;
            // St parent to null
            this.root.setParent(null);
            this.stepTrees.add(CloneUtils.clone(this));
            return;
        }

        // Case 2: Only one node that is not full
        else if (this.root.getChildren().isEmpty() && this.root.getKeys().size() < (this.order - 1)) {
            System.out.println("Case 2");
            insertLeafNode(key, this.root);
            this.stepTrees.add(CloneUtils.clone(this));

        }

        // Case 3: Normal insert
        else {
            System.out.println("Case 3");
            Node curr = this.root;
            // Since we insert the element only at the leaf node, we
            // traverse to the last level
            // Ya que insertamos el elemento solo en el nodo hoja, atravesamos al último nivel
            System.out.println("curr.getChildren1 ->" + curr.getChildren());

            int i = 1;
            while (!curr.getChildren().isEmpty()) {//while no  sea hoja
                System.out.println("ingresando a while " + i);
                System.out.println("key->" + key);
                curr = curr.getChildren().get(binarySearchInternalNode(key, curr.getKeys()));//ubicar el nododonde insertar
                System.out.println("while curr ->" + curr.getChildren());
                i++;
            }


            insertLeafNode(key, curr);//insertar dentro del nodo hoja

            if (curr.getKeys().size() == this.order) {
                System.out.println("If the leaf node becomes full, we split it .Tree Order->" + order);
                splitLeafNode(curr, this.order);
            }


        }
        this.stepTrees.add(CloneUtils.clone(this));

    }

    /**
     * Insert the key to the leaf node.
     *
     * @param key  the key to be inserted
     * @param node the node where the key has to be inserted
     */
    private void insertLeafNode(double key, Node node) {
        // Se ejecuta una búsqueda binaria para encontrar el lugar correcto donde se insertará el nodo
        System.out.println("insertLeafNode->" + key + " - Node->" + node.getKeys());
        int indexKey = binarySearchInternalNode(key, node.getKeys());
        System.out.println("Index position->" + indexKey);
        if (indexKey != 0 && node.getKeys().get(indexKey - 1) == key) {
            System.out.println("Key already exists.");
        } else {
            // Key doesn't exist. Add key and value
            node.getKeys().add(indexKey, key);
            this.stepTrees.add(CloneUtils.clone(this));
        }
        System.out.println("insertLeafNode final ->" + node.getKeys());

    }

    /**
     * Split leaf node.
     *
     * @param curr  the over-full leaf node to be split
     * @param order the degree of the B Plus Tree
     */
    private void splitLeafNode(Node curr, int order) {

        System.out.println("Find the newParent index");
        int halfIndex = order / 2;
        System.out.println(halfIndex);
        //Create two new nodes
        Node newParent = new Node();
        Node right = new Node();

        System.out.println("Set the right part to have newParent element and the elements right to the newParent element");
        right.setKeys(curr.getKeys().subList(halfIndex, curr.getKeys().size()));
        right.setParent(newParent);
        // While making newParent as the internal node, we add only the key since
        // internal nodes of bplus tree do not contain values
        newParent.getKeys().add(curr.getKeys().get(halfIndex));
        newParent.getChildren().add(right);
        // Curr holds the left part, so update the split node to contain just
        // the left part
        curr.getKeys().subList(halfIndex, curr.getKeys().size()).clear();

        System.out.println("After Split -> keys" + curr.getKeys());
        boolean firstSplit = true;
        // propogate the newParent element up the tree and merge with parent of
        // previously overfull node
        splitInternalNode(curr.getParent(), curr, order, newParent, firstSplit);


    }

    /**
     * Split internal node.
     *
     * @param parentLeft the current Internal Node
     * @param left       the child of the current Internal Node (Previous internal
     *                   node)
     * @param order      the degree of the B Plus Tree
     * @param newParent  the part split to be inserted to this internal Node
     * @param firstSplit indicates if the split is happening at the first internal node
     *                   from the bottom
     */
    //        splitInternalNode  (curr.getParent(), curr,     order,    newParent,            firstSplit);
    private void splitInternalNode(Node parentLeft, Node left, int order, Node newParent, boolean firstSplit) {
        if (parentLeft == null) {
            // if we split the root before, then a new root has to be created
            this.root = newParent;
            // we find where the child has to be inserted by doing a binary
            // search on keys
            int indexLeft = binarySearchInternalNode(left.getKeys().get(0), newParent.getKeys());
            left.setParent(newParent);
            newParent.getChildren().add(indexLeft, left);
            if (firstSplit) {
                // update the linked list only for first split (for leaf node)
                if (indexLeft == 0) {
                    newParent.getChildren().get(0).setNext(newParent.getChildren().get(1));
                    newParent.getChildren().get(1).setPrev(newParent.getChildren().get(0));
                } else {
                    newParent.getChildren().get(indexLeft + 1)
                            .setPrev(newParent.getChildren().get(indexLeft));
                    newParent.getChildren().get(indexLeft - 1)
                            .setNext(newParent.getChildren().get(indexLeft));
                }
            }
            this.stepTrees.add(CloneUtils.clone(this));

        } else {
            // merge the internal node with the newParent + right of previous split
            mergeInternalNodes(parentLeft, newParent);
            if (parentLeft.getKeys().size() == order) {
                // do a split again if the internal node becomes full
                int halfIndex = (int) Math.ceil(order / 2.0) - 1;
                Node parent = new Node();
                Node right = new Node();

                // since internal nodes follow a split like the b tree, right
                // part contains elements right of the newParent element, and the
                // newParent becomes parent of right part
                right.setKeys(parentLeft.getKeys().subList(halfIndex + 1, parentLeft.getKeys().size()));
                right.setParent(parent);

                parent.getKeys().add(parentLeft.getKeys().get(halfIndex));
                parent.getChildren().add(right);

                List<Node> childrenCurr = parentLeft.getChildren();
                List<Node> childrenOfRight = new ArrayList<>();

                int lastChildLeft = childrenCurr.size() - 1;

                // update the children that have to be sent to the right part
                // from the split node
                for (int i = childrenCurr.size() - 1; i >= 0; i--) {
                    List<Double> currKeysList = childrenCurr.get(i).getKeys();
                    if (parent.getKeys().get(0) <= currKeysList.get(0)) {
                        childrenCurr.get(i).setParent(right);
                        childrenOfRight.add(0, childrenCurr.get(i));
                        lastChildLeft--;
                    } else {
                        break;
                    }
                }

                right.setChildren(childrenOfRight);

                // update the overfull node to contain just the left part and
                // update its children
                parentLeft.getChildren().subList(lastChildLeft + 1, childrenCurr.size()).clear();
                parentLeft.getKeys().subList(halfIndex, parentLeft.getKeys().size()).clear();

                // propogate split one level up
                splitInternalNode(parentLeft.getParent(), parentLeft, order, newParent, false);
            }
            this.stepTrees.add(CloneUtils.clone(this));

        }

    }

    /**
     * Merge internal nodes.
     *
     * @param mergeFrom to part from which we have to merge (newParent of the previous
     *                  split node)
     * @param mergeInto the internal node to be merged to
     */
    private void mergeInternalNodes(Node mergeInto, Node mergeFrom) {
        Double keyToBeInserted = mergeFrom.getKeys().get(0);
        Node childToBeInserted = mergeFrom.getChildren().get(0);
        // Find the index where the key has to be inserted to by doing a binary
        // search
        int indexToBeInsertedAt = binarySearchInternalNode(keyToBeInserted, mergeInto.getKeys());
        int childInsertPos = indexToBeInsertedAt;
        if (keyToBeInserted <= childToBeInserted.getKeys().get(0)) {
            childInsertPos = indexToBeInsertedAt + 1;
        }
        childToBeInserted.setParent(mergeInto);
        mergeInto.getChildren().add(childInsertPos, childToBeInserted);
        mergeInto.getKeys().add(indexToBeInsertedAt, keyToBeInserted);

        // Update Linked List ofleft nodes
        if (!mergeInto.getChildren().isEmpty() && mergeInto.getChildren().get(0).getChildren().isEmpty()) {

            // If merge is happening at the last element, then only pointer
            // between new node and previously last element
            // needs to be updated
            if (mergeInto.getChildren().size() - 1 != childInsertPos
                    && mergeInto.getChildren().get(childInsertPos + 1).getPrev() == null) {
                mergeInto.getChildren().get(childInsertPos + 1).setPrev(mergeInto.getChildren().get(childInsertPos));
                mergeInto.getChildren().get(childInsertPos).setNext(mergeInto.getChildren().get(childInsertPos + 1));
            }
            // If merge is happening at the last element, then only pointer
            // between new node and previously last element
            // needs to be updated
            else if (0 != childInsertPos && mergeInto.getChildren().get(childInsertPos - 1).getNext() == null) {
                mergeInto.getChildren().get(childInsertPos).setPrev(mergeInto.getChildren().get(childInsertPos - 1));
                mergeInto.getChildren().get(childInsertPos - 1).setNext(mergeInto.getChildren().get(childInsertPos));
            }
            // If merge is happening in between, then the next element and the
            // previous element's prev and next pointers have to be updated
            else {
                mergeInto.getChildren().get(childInsertPos)
                        .setNext(mergeInto.getChildren().get(childInsertPos - 1).getNext());
                mergeInto.getChildren().get(childInsertPos).getNext()
                        .setPrev(mergeInto.getChildren().get(childInsertPos));
                mergeInto.getChildren().get(childInsertPos - 1).setNext(mergeInto.getChildren().get(childInsertPos));
                mergeInto.getChildren().get(childInsertPos).setPrev(mergeInto.getChildren().get(childInsertPos - 1));
            }
        }

    }

    /**
     * Helper method - Prints the tree using a level order traversal
     */
    public void printTree() {
        Queue<Node> queue = new LinkedList<Node>();
        queue.add(this.root);
        queue.add(null);
        Node curr = null;
        int levelNumber = 2;
        System.out.println("Nivel 1");
        while (!queue.isEmpty()) {
            curr = queue.poll();
            if (curr == null) {
                queue.add(null);
                if (queue.peek() == null) {
                    break;
                }
                System.out.println("\n" + "Nivel " + levelNumber++);
                continue;
            }

            printNode(curr);

            if (curr.getChildren().isEmpty()) {
                break;
            }
            for (int i = 0; i < curr.getChildren().size(); i++) {
                queue.add(curr.getChildren().get(i));
            }
        }

        curr = curr.getNext();
        while (null != curr) {
            printNode(curr);
            curr = curr.getNext();
        }

    }

    /**
     * Helper method Prints a node of the tree.
     *
     * @param curr the node to be printed
     */
    private void printNode(Node curr) {
        for (int i = 0; i < curr.getKeys().size(); i++) {
            System.out.print(curr.getKeys().get(i) + " ");
        }
        System.out.print("||");
    }

    /**
     * Búsqueda binaria modificada dentro del nodo interno.
     *
     * @param key     la llave a buscar
     * @param keyList la lista de claves a buscar
     * @return the first index of the list at which the key which is greater
     * than the input key
     */
    public int binarySearchInternalNode(double key, List<Double> keyList) {
        int start = 0;
        int end = keyList.size() - 1;
        int position;
        int index = -1;
        //The key is at start
        if (key < keyList.get(start)) {
            return 0;
        }
        // The key is at end
        if (key >= keyList.get(end)) {
            return keyList.size();
        }
        while (start <= end) {
            position = (start + end) / 2;
            // La siguiente condición asegura que encontremos una ubicación
            // s.t. la clave es más pequeña que el elemento en ese índice
            // y es mayor o igual que el elemento en el índice anterior.
            // Esta ubicación es donde se insertaría la llave
            if (key < keyList.get(position) && key >= keyList.get(position - 1)) {
                index = position;
                break;
            } // Las siguientes condiciones siguen la búsqueda binaria normal
            else if (key >= keyList.get(position)) {
                start = position + 1;
            } else {
                end = position - 1;
            }
        }
        return index;
    }

    /**
     * Search values for a key
     *
     * @param key the key to be searched
     * @return the list of values for the key
     */
    public List<String> search(double key) {
        List<String> searchValues = null;

        Node curr = this.root;
        // Traverse to the corresponding leaf node that would 'should'
        // contain this key
        while (curr.getChildren().size() != 0) {
            curr = curr.getChildren().get(binarySearchInternalNode(key, curr.getKeys()));
        }
        List<Double> keyList = curr.getKeys();
        // Do a linear search in this node for the key. Set the parameter
        // 'searchValues' only if success
        for (int i = 0; i < keyList.size(); i++) {
            //if (key == keyList.get(i).getKey()) {
            //	searchValues = keyList.get(i).getValues();
            //}
            if (key < keyList.get(i)) {
                break;
            }
        }

        return searchValues;
    }
    // Get the mid point
    private int getMidpoint() {
        return (int) Math.ceil((this.order + 1) / 2.0) - 1;
    }

    private boolean isOverfull() {
        return this.order == order + 1;
    }
    /***************************Nueva Implementacion******************************/

    /**
     * @param key , use key to find node
     * @return the node which contains of the key
     */
    public Node getNode(Double key) {
        if (isEmpty()) {
            return nullBTNode;
        }
        int index = 0;
        Node currentNode = root;
        while (!currentNode.equals(nullBTNode)) {
            int i = 0;
            while (i < currentNode.getSize()) {
                if (currentNode.getKey(i).equals(key)) {
                    index = i;
                    return currentNode;
                } else if (currentNode.getKey(i).compareTo(key) > 0) {
                    currentNode = currentNode.getChildren().get(i);
                    i = 0;
                } else {
                    i++;
                }
            }
            if (!currentNode.isNull()) {
                currentNode = currentNode.getChildren().get(currentNode.getSize());
            }
        }
        return nullBTNode;
    }

    /**
     * @param key , the key to be deleted
     */

    /*
     * Case 1: If k is in the node x which is a leaf and x.size -1 >= halfNumber
     * Case 2: If k is in the node x which is a leaf and x.size -1 < halfNumber Case
     * 3: If k is in the node x and x is an internal node (not a leaf)
     */
    public void delete(double key) {
 /*       System.out.println("--------------------------------------\nDelete\n--------------------------------------");
//		stepMess.add("Cay ban dau");
        stepTrees.add(CloneUtils.clone(this));
        // Tim kiem node chua key
        Node node = getNode(key);
        Node deleteNode = null;
        if (node.equals(nullBTNode))
            return;

        // Neu la root, cay 1 node 1 key -> Xoa luon
        if (node.equals(root) && node.getSize() == 1 && node.isLastInternalNode()) {
            root = null;
            //treeSize--;

            System.out.println("Xoa goc");
            stepTrees.add(CloneUtils.clone(this));
        } else {
            boolean flag = true;
            boolean isReplaced = false;
            // TODO: case 3
            if (!node.isLastInternalNode()) {
                node = replaceNode(node);
                deleteNode = node;
                isReplaced = true;
            }

            // Neu xoa lam anh huong den do cao cay
            if (node.getSize() - 1 < halfNumber) {
//				System.out.println("Case 2:");
                // TODO: case 2
                node = balanceDeletedNode(node);
                if (isReplaced) {
                    for (int i = 0; i <= node.getSize(); i++) {
                        for (int j = 0; i < node.getChild(i).getSize(); j++) {
                            if (node.getChild(i).getKey(j).equals(key)) {
                                deleteNode = node.getChild(i);
                                break;
                            }
                        }
                    }
                }
            } else if (node.isLastInternalNode()) {
                // TODO: Case 1
                System.out.println("Case 1: Delete");
                node.removeChild(0);
            }

            while (!node.getChild(0).equals(root) && node.getSize() < halfNumber && flag) {
//				System.out.println("Debug3");
                System.out.println("This is case 3c: Recursively delete");
                if (node.equals(root)) {
                    for (int i = 0; i <= root.getSize(); i++) {
                        if (root.getChild(i).getSize() == 0) {
                            flag = true;
                            break;
                        } else {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    node = balanceDeletedNode(node);
                }
            }

            if (deleteNode == null) {
                // Ktra xem da xoa truoc do chua hay moi chi rebalance/ replace
                node = getNode(key);
            } else {
                node = deleteNode;
            }

            if (!node.equals(nullBTNode)) {
                // Sau khi replace xong thi xoa node di (khi do, node da tro thanh la)
                for (int i = 0; i < node.getSize(); i++) {
                    if (node.getKey(i) == key) {
                        node.removeKey(i);
                    }
                }
                treeSize--;

                System.out.println("Xoa " + key);
                stepTrees.add(CloneUtils.clone(this));
            }
        }*/
    }


}
