package dido.data;

/**
 * Provide indexed access to data. Indexes are always 1 based.
 *
 */
public interface IndexedData {

    /**
     * Get the Data Schema associated with this data.
     *
     * @return A Data Schema, never null.
     */
    IndexedSchema getSchema();

    /**
     * Get the data at the given index. If the index is in the schema then this
     * method will either return the data or null if there is none. If the index is not
     * int the schema then behaviour is undefined.
     *
     * @param index The index.
     * @return Either some data or null.
     */
    Object getAt(int index);

    /**
     * Is there data at the given index. If the index is in the schema then this
     * method will either return true or false. If the index is not
     * in the schema then behaviour is undefined.
     *
     * @param index The index.
     * @return true if there is data, false if there isn't.
     */
    boolean hasAt(int index);

    /**
     * Get the boolean value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    boolean getBooleanAt(int index);

    /**
     * Get the char value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    char getCharAt(int index);

    /**
     * Get the byte value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    byte getByteAt(int index);

    /**
     * Get the short value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    short getShortAt(int index);

    /**
     * Get the int value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    int getIntAt(int index);

    /**
     * Get the long value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    long getLongAt(int index);

    /**
     * Get the float value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    float getFloatAt(int index);

    /**
     * Get the double value at the given index. If there is no data at the index or the index is not
     * in the schema then behaviour is undefined. Unless documented, it should be assumed that no conversion
     * of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    double getDoubleAt(int index);

    /**
     * Get the String value at the given index. If there is no data at the index then null will be returned.
     * If the index is not in the schema then behaviour is undefined. Unless documented, it should be assumed
     * that no conversion of data at the index will be done.
     *
     * @param index The index.
     * @return A boolean value.
     */
    String getStringAt(int index);


}
