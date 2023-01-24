package dido.oddjob.transform;

import dido.data.DataSchema;


/**
 * Creates a {@link Transformer}
 *
 * @param <F> The field type of the incoming data.
 * @param <T> The field type of the outgoing data.
 */
public interface TransformerFactory<F, T> {

    /**
     * Creates the {@link Transformer}.
     *
     * @param position The position of this factory when in a list of transformers. The first will be 1.
     * @param fromSchema The incoming data schema.
     * @param schemaSetter The ability to set part of the outgoing data schema.
     *
     * @return The complete Transformer. Not expected to ever be null.
     */
    Transformer<F, T> create(int position,
                             DataSchema<F> fromSchema,
                             SchemaSetter<T> schemaSetter);


}
