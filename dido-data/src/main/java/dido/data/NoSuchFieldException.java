package dido.data;

/**
 * An Exception thrown if there is No Field defined by a Schema.
 */
public class NoSuchFieldException extends RuntimeException {

    public NoSuchFieldException(int index, IndexedSchema schema) {
        super("No such field at index [" + index + "], schema is " + schema);
    }

    public NoSuchFieldException(String name, IndexedSchema schema) {
        super("No such field named [" + name + "], schema is " + schema);
    }

}
