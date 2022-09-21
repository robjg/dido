package dido.operators;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class FlattenType implements Supplier<Function<IndexedData<String>, List<GenericData<String>>>> {

    private String field;

    private DataSchema<String> schema;

    @Override
    public Function<IndexedData<String>, List<GenericData<String>>> get() {

        String field = Objects.requireNonNull(this.field, "Field must be provided");

        return Optional.ofNullable(schema)
                .map(s -> Flatten.fieldOfSchema(field, s))
                .orElseGet(() -> Flatten.field(field));
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public DataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(DataSchema<String> schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "Flatten{" +
                "field='" + field + '\'' +
                '}';
    }
}
