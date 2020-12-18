package tree;

/**
 * The Class tree.Node.
 */
public class Node {

    public int numKeys;
    /**
     * The list of key values in the node.
     */
    public Key[] keys;

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
        this.keys = new Key[BPlusTree.order + 1];
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
     * Get key in the index
     *
     * @param index the index to get
     * @return the key
     */
    public Double getKey(int index) {
        return this.keys[index].getKey();
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
    public Key[] getKeys() {
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

    @Override
    public String toString() {
        return "Keys =" + keys.toString();
    }

}

