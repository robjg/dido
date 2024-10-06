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
        WriteSchema schema = dataFactory.getSchema();
        setters = new FieldSetter[schema.lastIndex()];
        for (int index = schema.firstIndex(); index > 0; index = schema.nextIndex(index)) {
            setters[index - 1] = schema.getFieldSetterAt(index);
        }
    }

    public static <D extends DidoData> Values<D> withDataFactory(DataFactory<D> dataFactory) {
        return new Values<>(dataFactory);
    }

    public ReadWriteSchema getSchema() {
        return dataFactory.getSchema();
    }

    public D of(Object... values) {
        WritableData writableData = dataFactory.getSetter();
        for (int i = 0; i < values.length; ++i) {
            Object value = values[i];
            if (value != null) {
                setters[i].set(writableData, value);
            }
        }
        return dataFactory.toData();
    }

    public D ofList(List<?> values) {
        WritableData writableData = dataFactory.getSetter();
        int i = 0;
        for (Object value : values) {
            if (value != null) {
                setters[i++].set(writableData, value);
            }
        }
        return dataFactory.toData();
    }

}
