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
     * @param fromSchema The incoming data schema.
     * @param schemaSetter The ability to set part of the outgoing data schema.
     *
     * @return The complete Transformer. Not expected to ever be null.
     */
    Transformer create(DataSchema fromSchema,
                       SchemaSetter schemaSetter);


}
