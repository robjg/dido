package dido.data;

/**
 * Data that is accessed by String field names. Although these methods essentially duplicate
 * those provide in {@link DidoData}, they provide are more easily accessed when writing code.
 * These methods are for programmers, {@code DidoData} is for frameworks.
 */
public interface NamedData extends DidoData {

    Object get(String name);

    boolean has(String name);

    boolean getBoolean(String name);

    byte getByte(String name);

    char getChar(String name);

    short getShort(String name);

    int getInt(String name);

    long getLong(String name);

    float getFloat(String name);

    double getDouble(String name);

    String getString(String name);

}
