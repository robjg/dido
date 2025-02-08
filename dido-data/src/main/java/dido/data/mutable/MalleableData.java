package dido.data.mutable;

import dido.data.SchemaField;

import java.lang.reflect.Type;

/**
 * {@link MutableData} that can also have its schema changed.
 */
public interface MalleableData extends MutableData {

    void removeAt(int index);

    void removeNamed(String name);

    void setNamedAt(int index, String name, Object value);

    void setBooleanNamedAt(int index, String name, boolean value);

    void setByteNamedAt(int index, String name, byte value);

    void setCharNamedAt(int index, String name, char value);

    void setShortNamedAt(int index, String name, short value);

    void setIntNamedAt(int index, String name, int value);

    void setLongNamedAt(int index, String name, long value);

    void setFloatNamedAt(int index, String name, float value);

    void setDoubleNamedAt(int index, String name, double value);

    void setStringNamedAt(int index, String name, String value);

    void setAt(int index, Object value, Type type);

    void setNamed(String name, Object value, Type type);

    void setNamedAt(int index, String name, Object value, Type type);

    void setField(SchemaField schemaField, Object value);
}
