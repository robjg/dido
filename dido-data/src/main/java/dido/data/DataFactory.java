package dido.data;

/**
 * Provides the ability to create Dido Data.
 *
 */
public interface DataFactory {

    DataSchema getSchema();

    /**
     * Provides Writable data that can be written to either directly using the {@code set} methods
     * or using a {@link FieldSetter} provided by a {@link WriteStrategy} appropriate for the data type.
     *
     * @return Writable Data. Never null.
     */
    WritableData getWritableData();

    DidoData toData();

}
