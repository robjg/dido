package dido.oddjob.transform;

import dido.data.DataSchema;


/**
 * Creates a {@link Transformer}
 *
 */
public interface TransformerFactory {

    /**
     * Creates the {@link Transformer}.
     *
     * @param position The position of this factory when in a list of transformers. The first will be 1.
     * @param fromSchema The incoming data schema.
     * @param schemaSetter The ability to set part of the outgoing data schema.
     *
     * @return The complete Transformer. Not expected to ever be null.
     */
    Transformer create(int position,
                             DataSchema fromSchema,
                             SchemaSetter schemaSetter);


}
