package dido.data;

/**
 * Something capable of getting data. Getters are designed to be reused and so can be
 * optimised for the fastest possible access.
 */
public interface FieldGetter {

    boolean has(DidoData data);

    Object get(DidoData data);

    boolean getBoolean(DidoData data);

    char getChar(DidoData data);

    byte getByte(DidoData data);

    short getShort(DidoData data);

    int getInt(DidoData data);

    long getLong(DidoData data);

    float getFloat(DidoData data);

    double getDouble(DidoData data);

    String getString(DidoData data);

    static FieldGetter getterAt(int index) {

        return new FieldGetter() {

            @Override
            public Object get(DidoData data) {
                return data.getAt(index);
            }

            @Override
            public boolean has(DidoData data) {
                return data.hasIndex(index);
            }

            @Override
            public boolean getBoolean(DidoData data) {
                return data.getBooleanAt(index);
            }

            @Override
            public char getChar(DidoData data) {
                return data.getCharAt(index);
            }

            @Override
            public byte getByte(DidoData data) {
                return data.getByteAt(index);
            }

            @Override
            public short getShort(DidoData data) {
                return data.getShortAt(index);
            }

            @Override
            public int getInt(DidoData data) {
                return data.getIntAt(index);
            }

            @Override
            public long getLong(DidoData data) {
                return data.getLongAt(index);
            }

            @Override
            public float getFloat(DidoData data) {
                return data.getFloatAt(index);
            }

            @Override
            public double getDouble(DidoData data) {
                return data.getDoubleAt(index);
            }

            @Override
            public String getString(DidoData data) {
                return data.getStringAt(index);
            }
        };
    }

    static FieldGetter getterNamed(String name) {

        return new FieldGetter() {
            @Override
            public Object get(DidoData data) {
                return data.getNamed(name);
            }

            @Override
            public boolean has(DidoData data) {
                return data.hasNamed(name);
            }

            @Override
            public boolean getBoolean(DidoData data) {
                return data.getBooleanNamed(name);
            }

            @Override
            public char getChar(DidoData data) {
                return data.getCharNamed(name);
            }

            @Override
            public byte getByte(DidoData data) {
                return data.getByteNamed(name);
            }

            @Override
            public short getShort(DidoData data) {
                return data.getShortNamed(name);
            }

            @Override
            public int getInt(DidoData data) {
                return data.getIntNamed(name);
            }

            @Override
            public long getLong(DidoData data) {
                return data.getLongNamed(name);
            }

            @Override
            public float getFloat(DidoData data) {
                return data.getFloatNamed(name);
            }

            @Override
            public double getDouble(DidoData data) {
                return data.getDoubleNamed(name);
            }

            @Override
            public String getString(DidoData data) {
                return data.getStringNamed(name);
            }
        };
    }
}
