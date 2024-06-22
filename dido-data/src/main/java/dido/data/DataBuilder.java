package dido.data;

/**
 * Base for things that build {@link DidoData}. The main implementations
 * are {@link NamedDataBuilder} and {@link IndexedDataBuilder}. Note that the former doesn't
 * inherit from the latter like their counterparts for {@link DidoData} because the schema might not be fully
 * known during the building process so an index for a field might not be known and vice versa.
 *
 * @param <D> The Type of DidoData being built.
 */
public interface DataBuilder<D extends DidoData> {


    D build();
}
