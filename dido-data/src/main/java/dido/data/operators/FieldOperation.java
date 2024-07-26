package dido.data.operators;

import dido.data.DidoData;

import java.util.function.Consumer;

/**
 * Apply an operation to a field possibly using incoming data.
 */
public interface FieldOperation extends Consumer<DidoData> {


}
