package dido.data;

/**
 * For A fluent way of providing data for a known schema
 */
public class Values<D extends DidoData> {

    private final DataFactory<D> dataFactory;

    private final Setter[] setters;

    private Values(WritableSchema<D> schema) {
        this.dataFactory = schema.newDataFactory();
        setters = new Setter[schema.lastIndex()];
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            setters[index - 1] = dataFactory.getSetterAt(index);
        }
    }

    public static <D extends DidoData> Values<D> valuesFor(WritableSchema<D> writableSchema) {
        return new Values<>(writableSchema);
    }

    public D of(Object... values) {
        for (int i = 0; i < values.length; ++i) {
            Object value = values[i];
            if (value != null) {
                setters[i].set(value);
            }
        }
        return dataFactory.toData();
    }
}
