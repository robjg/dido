package dido.data;

import dido.data.util.RepeatingDataWrappers;

import java.util.List;
import java.util.stream.Stream;

/**
 * Represents nested multiple items of {@link DidoData}. Provides an abstraction that hides
 * if the implementation uses an array or collection of something else for containing the multiple
 * data items and thus makes serialisation simpler.
 */
public interface RepeatingData extends Iterable<DidoData> {

    /**
     * The number of data items.
     *
     * @return The number of data items.
     */
    int size();

    /**
     * Get the data item at the 0 based given row. If row &lt; 0 or &gt; size behaviour is
     * undefined but an {@link  java.util.NoSuchElementException} is likely.
     *
     * @param row The row.
     *
     * @return The Data at the row.
     */
    DidoData get(int row);

    /**
     * Provide the data as a stream.
     *
     * @return A stream.
     */
    Stream<DidoData> stream();

    /**
     * Create repeating data from a list of data.
     *
     * @param list A list of data.
     * @return Repeating data.
     */
    static RepeatingData of(List<DidoData> list) {
        return RepeatingDataWrappers.of(list);
    }

    /**
     * Create repeating data from the given data items.
     *
     * @param data The data items.
     * @return Repeating data.
     */
    static RepeatingData of(DidoData... data) {
        return RepeatingDataWrappers.of(data);
    }

}
