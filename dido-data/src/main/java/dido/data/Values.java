package dido.data;

import java.util.List;

/**
 * For A fluent way of providing data for a known schema
 */
public class Values<D extends DidoData> {

    private final DataFactory<D> dataFactory;

    private final FieldSetter[] setters;

    private Values(DataFactory<D> dataFactory) {
        this.dataFactory = dataFactory;
        DataSchema schema = dataFactory.getSchema();
        WriteStrategy writeStrategy = WriteStrategy.fromSchema(schema);
        setters = new FieldSetter[schema.lastIndex()];
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            setters[index - 1] = writeStrategy.getFieldSetterAt(index);
        }
    }

    public static <D extends DidoData> Values<D> withDataFactory(DataFactory<D> dataFactory) {
        return new Values<>(dataFactory);
    }

    public DataSchema getSchema() {
        return dataFactory.getSchema();
    }

    public D of(Object... values) {
        WritableData writableData = dataFactory.getWritableData();
        for (int i = 0; i < values.length; ++i) {
            Object value = values[i];
            if (value != null) {
                setters[i].set(writableData, value);
            }
        }
        return dataFactory.toData();
    }

    public D ofList(List<?> values) {
        WritableData writableData = dataFactory.getWritableData();
        int i = 0;
        for (Object value : values) {
            if (value != null) {
                setters[i++].set(writableData, value);
            }
        }
        return dataFactory.toData();
    }

}
