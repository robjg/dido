package dido.operators;

import dido.data.DidoData;
import dido.data.GenericDataSchema;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class FlattenType implements Supplier<Function<DidoData, List<DidoData>>> {

    private String[] fields;

    private boolean columns;

    private GenericDataSchema<String> schema;

    @Override
    public Function<DidoData, List<DidoData>> get() {

        String[] fields = Objects.requireNonNull(this.fields, "Field must be provided");

        if (columns) {

            return Flatten.fields(fields);
        }
        else {
            String field = fields[0];

            return Optional.ofNullable(schema)
                    .map(s -> Flatten.fieldOfSchema(field, s))
                    .orElseGet(() -> Flatten.field(field));
        }

    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] field) {
        this.fields = field;
    }

    public GenericDataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(GenericDataSchema<String> schema) {
        this.schema = schema;
    }

    public boolean isColumns() {
        return columns;
    }

    public void setColumns(boolean columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "Flatten{" +
                "fields='" + Arrays.toString(fields) + '\'' +
                '}';
    }
}
