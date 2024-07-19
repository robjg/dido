package dido.data;

import java.util.Objects;

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

    <T> T getAtAs(int index, Class<T> type);

    /**
     * Is there data at the given index. If the index is in the schema then this
     * method will either return true or false. If the index is not
     * int the schema then behaviour is undefined.
     *
     * @param index The index.
     * @return true if there is data, false if there isn't.
     */
    boolean hasIndex(int index);

    boolean getBooleanAt(int index);

    char getCharAt(int index);

    byte getByteAt(int index);

    short getShortAt(int index);

    int getIntAt(int index);

    long getLongAt(int index);

    float getFloatAt(int index);

    double getDoubleAt(int index);

    String getStringAt(int index);

    static boolean equalsIgnoringSchema(IndexedData data1, IndexedData data2) {
        if (data1 == data2) {
            return true;
        }
        if (data1 == null || data2 == null) {
            return false;
        }

        IndexedSchema schema1 = data1.getSchema();
        IndexedSchema schema2 = data2.getSchema();
        for (int index1 = schema1.firstIndex(), index2 = schema2.firstIndex();
             index1 > 0 || index2 > 0;
             index1 = schema1.nextIndex(index1), index2 = schema2.nextIndex(index2)) {
            if (index1 != index2) {
                return false;
            }

            if (!Objects.equals(data1.getAt(index1), data2.getAt(index2))) {
                return false;
            }
        }
        return true;
    }

}
