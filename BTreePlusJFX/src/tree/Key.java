package tree;

import java.util.ArrayList;
import java.util.List;


/**
 * The Class Key.
 */
public class Key {

    /**
     * The key.
     */
    double key;

    /**
     * The list of values for the key. Set only for external nodes
     */
    List<String> values;

    /**
     * Instantiates a new key and its value.
     *
     * @param key   the key
     * @param value the value
     */
    public Key(double key, String value) {
        this.key = key;
        if (null == this.values) {
            values = new ArrayList<>();
        }
        this.values.add(value);
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public double getKey() {
        return key;
    }

    public String toString() {
        return "Key [key=" + key + ", values=" + values + "]";
    }


}
