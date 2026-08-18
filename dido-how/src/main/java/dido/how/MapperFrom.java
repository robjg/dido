package dido.how;

import dido.data.DidoData;
import dido.data.schema.HasSchema;

import java.util.function.Function;

/**
 * Provides a mapping from something to DidoData providing a Schema.
 *
 * @param <T> The thing from.
 */
public interface MapperFrom<T> extends Function<T, DidoData>, HasSchema {
}
