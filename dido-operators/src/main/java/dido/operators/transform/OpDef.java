package dido.operators.transform;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.WritableData;
import dido.data.WriteSchema;

import java.util.function.BiConsumer;

/**
 * Defines an Operation that will set data and can become part of a {@link DidoTransform}.
 * The implementation is a two-step process. A {@link dido.data.DataFactory} is required for last step, but
 * it can't be created until the final schema is known. This is why this creates
 * a {@link Prepare} as the intermediate step.
 */
@FunctionalInterface
public interface OpDef {

    /**
     * Creates the {@link Prepare}.
     *
     * @param incomingSchema The incoming data schema.
     * @param schemaSetter The ability to set part of the outgoing data schema.
     *
     * @return The complete Transformer. Not expected to ever be null.
     */
    Prepare prepare(DataSchema incomingSchema,
                    SchemaSetter schemaSetter);

    /**
     * Intermediate step created by {@link OpDef}
     */
    @FunctionalInterface
    interface Prepare {

        BiConsumer<DidoData, WritableData> create(WriteSchema writableData);
    }
}
