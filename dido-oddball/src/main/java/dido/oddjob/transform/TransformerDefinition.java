package dido.oddjob.transform;

import dido.data.DataSchema;


/**
 * Creates a {@link TransformerFactory}
 *
 */
public interface TransformerDefinition {

    /**
     * Creates the {@link TransformerFactory}.
     *
     * @param fromSchema The incoming data schema.
     * @param schemaSetter The ability to set part of the outgoing data schema.
     *
     * @return The complete Transformer. Not expected to ever be null.
     */
    TransformerFactory define(DataSchema fromSchema,
                              SchemaSetter schemaSetter);


}
