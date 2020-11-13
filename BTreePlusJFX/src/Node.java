import java.io.Serializable;

/**
 * The Class Node.
 */
public class Node implements Serializable {

    public int numKeys;
    /**
     * The list of key values in the node.
     */
    public Double[] keys;

    /**
     * The children of this node. Set only for internal Nodes.
     */
    public Node[] children;


    /**
     * The next element in the linked list. Set only for left Nodes.
     */
    public Node next;

    /**
     * The parent of this node. NULL for root
     */
    public Node parent;


    public boolean isLeaf;


    public double x;

    public double y;

    public double [] widths;

    public double width;

    /**
     * Instantiates a new node.
     */
    public Node() {
        this.keys = new Double[BPlusTree.order + 1];
        this.children = new Node[BPlusTree.order + 2];
        this.numKeys = 0;
        this.widths = new double[BPlusTree.order + 1];
        this.isLeaf = true;
        this.parent = null;
        this.next = null;
    }


    public int getSize() {
        return this.numKeys;
    }

    public boolean isLeaf() {
        if (this.isLeaf == true) {
            return true;
        } else {

            return false;
        }
    }
    public void setLeaf(boolean value) {
        this.isLeaf = value;
    }


    public int getNumKeys() {
        return this.numKeys;
    }

    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }

    /**
     * @param index the index to get
     * @return the key
     */
    public Double getKey(int index) {
        return this.keys[index];
    }


    public boolean isNull() {
        if (keys.length == 0) {
            return true;

        } else {
            return false;
        }
    }


    /**
     * Gets the keys.
     *
     * @return the keys
     */
    public Double[] getKeys() {
        return keys;
    }

    /**
     * Sets the keys.
     *
     * @param keys the new keys
     */
    public void setKeys(Double[] keys) {
     /*   Iterator<Double> iter = keys.iterator();
        while (iter.hasNext()) {
            this.keys.add(iter.next());
        }*/
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public Node[] getChildren() {
        return children;
    }


    /**
     * Gets the next.
     *
     * @return the next
     */
    public Node getNext() {
        return next;
    }

    /**
     * Sets the next.
     *
     * @param next the new next
     */
    public void setNext(Node next) {
        this.next = next;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Keys =" + keys.toString();
    }

    public boolean isOverflowed() {
        return keys.length == BPlusTree.order;
    }

    public boolean isUnderflowed() {
        return keys.length < BPlusTree.minKeys;
    }

}
