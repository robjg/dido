package dido.oddjob.transpose;

import dido.data.DataSchema;


/**
 * Creates a {@link Transposer}
 *
 * @param <F> The field type of the incoming data.
 * @param <T> The field type of the outgoing data.
 */
public interface TransposerFactory<F, T> {

    /**
     * Creates the {@link Transposer}.
     *
     * @param position The position of this factory when in a list of transposers. The first will be 1.
     * @param fromSchema The incoming data schema.
     * @param schemaSetter The ability to set part of the outgoing data schema.
     *
     * @return The complete Transposer. Not expected to ever be null.
     */
    Transposer<F, T> create(int position,
                            DataSchema<F> fromSchema,
                            SchemaSetter<T> schemaSetter);


}
