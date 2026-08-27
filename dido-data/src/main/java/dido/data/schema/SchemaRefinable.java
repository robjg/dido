package dido.data.schema;

import dido.data.DataSchema;

/**
 * Something that can be Refined by a Schema. Provides a way
 * of optimising a pipeline of operation on Dido Data in advance.
 *
 * @param <T>
 */
@FunctionalInterface
public interface SchemaRefinable<T> {

    T forSchema(DataSchema schema);
}
