import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Class Node.
 */
public class Node implements Serializable {

    /**
     * The list of key values in the node.
     */
    private List<Double> keys;

    /**
     * The children of this node. Set only for internal Nodes.
     */
    private List<Node> children;

    /**
     * The previous element in the linked list. Set only for left Nodes.
     * */
    private Node prev;

    /**
     * The next element in the linked list. Set only for left Nodes.
     */
    private Node next;

    /**
     * The parent of this node. NULL for root
     */
    private Node parent;

    /**
     * Instantiates a new node.
     */
    public Node() {
        this.keys = new ArrayList<Double>();
        this.children = new ArrayList<>();
        this.prev = null;
        this.next = null;
    }


    public int getSize() {
        return keys.size();
    }

    /**
     * @param index the index to get
     * @return the key
     */
    public Double getKey(int index) {
        return keys.get(index);
    }

    /**
     * @return true, if node is a leaf
     */
    public boolean isLastInternalNode() {
        if (this.keys.size() == 0)
            return false;
        for (Node node : this.children)
            if (node.keys.size() != 0)
                return false;
        return true;
    }

    /**
     * @return true, if keys is empty
     */
    public boolean isNull() {
        return keys.isEmpty();
    }


    /**
     * Gets the keys.
     *
     * @return the keys
     */
    public List<Double> getKeys() {
        return keys;
    }

    /**
     * Sets the keys.
     *
     * @param keys the new keys
     */
    public void setKeys(List<Double> keys) {
        Iterator<Double> iter = keys.iterator();
        while (iter.hasNext()) {
            this.keys.add(iter.next());
        }
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Sets the children.
     *
     * @param children the new children
     */
    public void setChildren(List<Node> children) {
        this.children = children;
    }

    /**
     * Gets the prev.
     *
     *
     * @return the prev
     */
    public Node getPrev() {
        return prev;
    }

    /**
     * Sets the prev.
     *
     * @param prev the new prev
     */
    public void setPrev(Node prev) {
        this.prev = prev;
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

}
