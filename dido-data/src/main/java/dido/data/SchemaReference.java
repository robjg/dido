package dido.data;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SchemaReference<F> implements Supplier<DataSchema<F>> {

    private final AtomicReference<DataSchema<F>> schemaRef = new AtomicReference<>();

    private final String name;

    private SchemaReference() {
        this(null);
    }

    private SchemaReference(String name) {
        this.name = name;
    }


    public static <F> SchemaReference<F> blank() {
        return new SchemaReference<>();
    }

    public static <F> SchemaReference<F> named(String name) {
        return new SchemaReference<>(name);
    }

    public void set(DataSchema<F> schema) {
        schemaRef.set(schema);
    }

    @Override
    public DataSchema<F> get() {
        return schemaRef.get();
    }

    @Override
    public boolean equals(Object o) {
        if (schemaRef.get() == null) {
            return false;
        }
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaReference<?> that = (SchemaReference<?>) o;
        return Objects.equals(name, that.name) && schemaRef.get().equals(that.schemaRef.get());
    }

    @Override
    public int hashCode() {
        if (schemaRef.get() == null) {
            return 0;
        }
        else {
            return Objects.hash(schemaRef.get(), name);
        }
    }

    @Override
    public String toString() {
        String maybeSet = schemaRef.get() == null ? " (unset)" : "";
        if (name == null) {
            return "SchemaReference" + maybeSet;
        }
        else {
            return "SchemaReference{'" + name + '\'' + '}' + maybeSet;
        }
    }
}
