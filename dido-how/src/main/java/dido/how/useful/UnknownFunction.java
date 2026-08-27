package dido.how.useful;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.schema.SchemaRefinable;
import dido.how.RefinableFunction;

import java.util.function.Function;

/**
 * Helper to provide a function that will delegate when a Schema is available
 * from the incoming Data.
 *
 * @param <T> The output type.
 */
public class UnknownFunction<T>
        implements RefinableFunction<DidoData, T> {

    private final SchemaRefinable<Function<DidoData, T>> how;

    private Function<DidoData, T> known;

    private UnknownFunction(SchemaRefinable<Function<DidoData, T>> how) {
        this.how = how;
    }

    public static <T> UnknownFunction<T> of(SchemaRefinable<Function<DidoData, T>> how) {
        return new UnknownFunction<>(how);
    }

    @Override
    public Function<DidoData, T> forSchema(DataSchema schema) {
        return how.forSchema(schema);
    }

    @Override
    public T apply(DidoData f) {
        if (known == null) {
            known = how.forSchema(f.getSchema());
        }
        return known.apply(f);
    }

    @Override
    public String toString() {
        return "Unknown of " + how.toString();
    }

}
