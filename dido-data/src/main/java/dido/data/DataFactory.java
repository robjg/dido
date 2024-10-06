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

    WritableData getSetter();

    D toData();

}
