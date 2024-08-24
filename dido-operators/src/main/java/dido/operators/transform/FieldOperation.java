package dido.operators.transform;

import dido.data.DidoData;

import java.util.function.Consumer;

/**
 * Apply an operation to a field possibly using incoming data.
 *
 * @see FieldOperationFactory
 * @see FieldOperationDefinition
 */
public interface FieldOperation extends Consumer<DidoData> {



}
