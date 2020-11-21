package tree;

import java.io.Serializable;

/**
 * The Class tree.Node.
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

    /**
     * The node is leaf or external node
     */
    public boolean isLeaf;

    /**
     * Position x
     */
    public double x;
    /**
     * Position y
     */
    public double y;

    public double[] widths;

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


    /**
     * Get keys size
     *
     * @return
     */
    public int getSize() {
        return this.numKeys;
    }

    /**
     * Node is leaf or external node
     *
     * @return true o false
     */
    public boolean isLeaf() {
        if (this.isLeaf == true) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set node flag to left
     *
     * @param value
     */
    public void setLeaf(boolean value) {
        this.isLeaf = value;
    }


    /**
     * Get numKeys
     *
     * @return
     */
    public int getNumKeys() {
        return this.numKeys;
    }

    /**
     * Set NumKeys
     *
     * @param numKeys
     */
    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }

    /**
     * Get key in the index
     *
     * @param index the index to get
     * @return the key
     */
    public Double getKey(int index) {
        return this.keys[index];
    }


    /**
     * Keys is null
     *
     * @return
     */
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

    /**
     * Node is overflowed
     *
     * @return
     */
    public boolean isOverflowed() {
        return keys.length == BPlusTree.order - 1;
    }

    /**
     * Node is underflowed
     *
     * @return
     */
    public boolean isUnderflowed() {
        return keys.length < BPlusTree.minKeys;
    }

}
