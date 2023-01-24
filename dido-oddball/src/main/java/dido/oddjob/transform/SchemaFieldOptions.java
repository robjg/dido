package dido.oddjob.transform;

public class SchemaFieldOptions<F> {
    
    private final int index;
    
    private final F field;
    
    private final Class<?> type;

    private SchemaFieldOptions(int index, F field, Class<?> type) {
        this.index = index;
        this.field = field;
        this.type = type;
    }

    public static <F> SchemaFieldOptions<F> of(int index, F field, Class<?> type) {
        return new SchemaFieldOptions<>(index, field, type);
    }

    public int getIndex() {
        return index;
    }

    public F getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }
}
