package dido.data;

import java.lang.reflect.Type;

/**
 * Provides the ability to create Dido Data.
 *
 * @param <D> The type of Dido Data.
 */
public interface DataFactory<D extends DidoData> {

    Type getDataType();

    ReadWriteSchema getSchema();

    @Deprecated
    default FieldSetter getFieldSetterAt(int index) {
        return getSchema().getFieldSetterAt(index);
    }

    @Deprecated
    default FieldSetter getFieldSetterNamed(String name) {
        return getSchema().getFieldSetterNamed(name);
    }

    /**
     * Provides Writable data that can be written to either directly using the {@code set} methods
     * or using a {@link FieldSetter} provided by a {@link WriteStrategy} appropriate for the data type.
     *
     * @return Writable Data. Never null.
     */
    WritableData getWritableData();


    D toData();

}
