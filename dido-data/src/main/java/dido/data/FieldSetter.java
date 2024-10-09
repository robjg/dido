package dido.data;

/**
 * Provides the ability to set data. Field Setters can be retrieved from an {@link WriteStrategy}
 * at the beginning of a large scale write operation and reused with each new item of
 * {@link WritableData} they are fed. Field setters will be optimised for a certain type of writable
 * data, and will throw {@link ClassCastException}s if fed the wrong type of data.
 *
 * @see DataFactory
 */
public interface FieldSetter {

    void clear(WritableData data);

    void set(WritableData data, Object value);

    void setBoolean(WritableData data, boolean value);

    void setByte(WritableData data, byte value);

    void setChar(WritableData data, char value);

    void setShort(WritableData data, short value);

    void setInt(WritableData data, int value);

    void setLong(WritableData data, long value);

    void setFloat(WritableData data, float value);

    void setDouble(WritableData data, double value);

    void setString(WritableData data, String value);

    static FieldSetter at(int index) {

        return new FieldSetter() {

            @Override
            public void clear(WritableData data) {
                data.clearAt(index);
            }

            @Override
            public void set(WritableData data, Object value) {
                data.setAt(index, value);
            }

            @Override
            public void setBoolean(WritableData data, boolean value) {
                data.setBooleanAt(index, value);
            }

            @Override
            public void setByte(WritableData data, byte value) {
                data.setByteAt(index, value);
            }

            @Override
            public void setChar(WritableData data, char value) {
                data.setCharAt(index, value);
            }

            @Override
            public void setShort(WritableData data, short value) {
                data.setShortAt(index, value);
            }

            @Override
            public void setInt(WritableData data, int value) {
                data.setIntAt(index, value);
            }

            @Override
            public void setLong(WritableData data, long value) {
                data.setLongAt(index, value);
            }

            @Override
            public void setFloat(WritableData data, float value) {
                data.setFloatAt(index, value);
            }

            @Override
            public void setDouble(WritableData data, double value) {
                data.setDoubleAt(index, value);
            }

            @Override
            public void setString(WritableData data, String value) {
                data.setStringAt(index, value);
            }
        };
    }

    static FieldSetter named(String name) {

        return new FieldSetter() {
            @Override
            public void clear(WritableData data) {
                data.clearNamed(name);
            }

            @Override
            public void set(WritableData data, Object value) {
                data.setNamed(name, value);
            }

            @Override
            public void setBoolean(WritableData data, boolean value) {
                data.setBooleanNamed(name, value);
            }

            @Override
            public void setByte(WritableData data, byte value) {
                data.setByteNamed(name, value);
            }

            @Override
            public void setChar(WritableData data, char value) {
                data.setCharNamed(name, value);
            }

            @Override
            public void setShort(WritableData data, short value) {
                data.setShortNamed(name, value);
            }

            @Override
            public void setInt(WritableData data, int value) {
                data.setIntNamed(name, value);
            }

            @Override
            public void setLong(WritableData data, long value) {
                data.setLongNamed(name, value);
            }

            @Override
            public void setFloat(WritableData data, float value) {
                data.setFloatNamed(name, value);
            }

            @Override
            public void setDouble(WritableData data, double value) {
                data.setDoubleNamed(name, value);
            }

            @Override
            public void setString(WritableData data, String value) {
                data.setStringNamed(name, value);
            }
        };
    }
}
