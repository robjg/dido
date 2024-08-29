package dido.operators.transform;

import dido.data.DataSchema;

/**
 * Defines an Operation that can be applied to a field. Creating an {@link Transformation} is a
 * two-step process. A {@link dido.data.DataFactory} is required for last step, but
 * it can't be created until the final schema is known. This is why this creates
 * an {@link TransformerFactory} as the intermediate step.
 */
public interface TransformerDefinition {

    TransformerFactory define(DataSchema incomingSchema,
                              SchemaSetter schemaSetter);
}
