package dido.data;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Provide a reference to a schema that is yet to be defined. Required for nested tree like
 * schemas. The reference may be named for identification.
 * </p>
 * A reference may only be set once. It should be set as soon as possible. A schema containing
 * a reference can be considered effectively immutable once the reference is set.
 * </p>
 * Equality
 */
public class SchemaReference implements Supplier<DataSchema> {

    private final AtomicReference<DataSchema> schemaRef = new AtomicReference<>();

    private final String name;

    private SchemaReference() {
        this(null);
    }

    private SchemaReference(String name) {
        this.name = name;
    }


    public static SchemaReference blank() {
        return new SchemaReference();
    }

    public static <F> SchemaReference named(String name) {
        return new SchemaReference(name);
    }

    public void set(DataSchema schema) {
        if (schemaRef.get() != null) {
            throw new IllegalStateException(
                    "Attempting to set a schema in a schema reference that has already been set. New: " +
                    schema + ", in: " + this);
        }
        schemaRef.set(Objects.requireNonNull(schema, "Schema is null setting: " + this));
    }

    @Override
    public DataSchema get() {
        DataSchema schema = schemaRef.get();
        if (schema == null) {
            throw new IllegalStateException("Attempt to access a schema in a reference before it has been set: " +
                    this);
        }
        return schema;
    }

    @Override
    public boolean equals(Object o) {
        if (schemaRef.get() == null) {
            return false;
        }
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaReference that = (SchemaReference) o;
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
