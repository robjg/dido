package dido.how;

import dido.data.schema.SchemaRefinable;

import java.util.function.Function;

/**
 * A Function that can be optimised with a Schema.
 *
 * @param <F> The from type of the function.
 * @param <T> The to type of the function.
 */
public interface RefinableFunction<F, T>
        extends Function<F, T>, SchemaRefinable<Function<F, T>> {
}
