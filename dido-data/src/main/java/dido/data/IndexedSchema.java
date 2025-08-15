package dido.data;

import java.lang.reflect.Type;

/**
 * Schema for {@link IndexedData}. The most basic form of Schema.
 */
public interface IndexedSchema {

    boolean hasIndex(int index);

    /**
     * Get the first index in an {@link IndexedData} structure
     * at which there may be data. Calling {@link IndexedData#hasAt(int)} for values
     * below this has undefined behaviour.
     *
     * @return The first index. Or a value of 0 if the schema is empty.
     */
    int firstIndex();

    /**
     * Get the next index {@link IndexedData} structure
     * at which there may be data. Calling {@link IndexedData#hasAt(int)} for values
     * between the index argument and the returned value this has undefined behaviour.
     * Calling this method with a value that has not been returned by {@link #firstIndex()}
     * or a previous call to this method has undefined behaviour. Calling this method
     * with the result of {@link #lastIndex()} will always return 0.
     *
     * @param index A valid index.
     * @return The next index or 0.
     */
    int nextIndex(int index);

    /**
     * Get the first index in an {@link IndexedData} structure
     * at which there may be data. Calling {@link IndexedData#hasAt(int)} for values
     * below this has undefined behaviour.
     *
     * @return The first index. Or a value of 0 if the schema is empty.
     */
    int lastIndex();

    /**
     * Get the type that a value is at a given index. If the
     * index does not exist behaviour is undefined.
     *
     * @param index The index.
     * @return The type.
     */
    Type getTypeAt(int index);

    /**
     * The number of fields in the schema.
     *
     * @return The size.
     */
    int getSize();

    /**
     * Get the indices in the schema. Implementations must return an array
     * such that there internal structure can not be manipulated.
     *
     * @return An array of the indices. Never null.
     */
    default int[] getIndices() {
        int[] indices = new int[getSize()];
        int i = 0;
        for (int index = firstIndex(); index > 0; index = nextIndex(index)) {
            indices[i++] = index;
        }
        return indices;
    }
}
