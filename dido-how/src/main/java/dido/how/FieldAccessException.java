package dido.how;

import dido.data.DataSchema;

/**
 * An Exception thrown if the field in the data couldn't be accessed for some reason.
 */
public class FieldAccessException extends DataException {

    public FieldAccessException(int index, DataSchema schema, Throwable cause) {
        super("Failed accessing field at index [" + index + "], data schema is " + schema, cause);
    }

    public FieldAccessException(String name, DataSchema schema, Throwable cause) {
        super("Failed accessing field named [" + name + "], data schema is " + schema, cause);
    }

}
