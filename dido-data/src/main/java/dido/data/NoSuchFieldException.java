package dido.data;

/**
 * An Exception thrown if there is No Field defined by a Schema.
 */
public class NoSuchFieldException extends RuntimeException {

    public NoSuchFieldException(int index, DataSchema schema) {
        super("No such field at index [" + index + "], data schema is " + schema);
    }

    public NoSuchFieldException(String name, DataSchema schema) {
        super("No such field named [" + name + "], data schema is " + schema);
    }

}
