package dido.data;

/**
 * Something capable of getting data. Getters are designed to be reused and so can be
 * optimised for the fastest possible access.
 */
public interface Getter {

    Object get(DidoData data);

    <T> T getAs(Class<T> type, DidoData data);

    boolean has(DidoData data);

    boolean getBoolean(DidoData data);

    char getChar(DidoData data);

    byte getByte(DidoData data);

    short getShort(DidoData data);

    int getInt(DidoData data);

    long getLong(DidoData data);

    float getFloat(DidoData data);

    double getDouble(DidoData data);

    String getString(DidoData data);

}
