package dido.oddjob.transform;

/**
 * Container for field options for new fields.
 *
 */
public class SchemaFieldOptions {
    
    private final int index;
    
    private final String field;
    
    private final Class<?> type;

    private SchemaFieldOptions(int index, String field, Class<?> type) {
        this.index = index;
        this.field = field;
        this.type = type;
    }

    public static SchemaFieldOptions of(int index, String field, Class<?> type) {
        return new SchemaFieldOptions(index, field, type);
    }

    public int getIndex() {
        return index;
    }

    public String getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }
}
