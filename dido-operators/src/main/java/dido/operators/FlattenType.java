package dido.operators;

import dido.data.DataSchema;
import dido.data.DidoData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @oddjob.description Provides a mapping function that either flattens fields that are
 * collections or arrays or a nested repeating schema. The result is a List of {@code DidoData}
 *
 * @oddjob.example Flatten a nested schema.
 * {@oddjob.xml.resource dido/operators/FlattenExample.xml}
 *
 * @oddjob.example Flatten columns that are arrays.
 * {@oddjob.xml.resource dido/operators/FlattenColumnsExample.xml}
 */
public class FlattenType implements Supplier<Function<DidoData, List<DidoData>>> {

    /**
     * @oddjob.description The comma separated list of fields to flatten. Only one nested
     * Schema field is supported (if columns is false)
     * @oddjob.required yes.
     */
    private String[] fields;

    /**
     * @oddjob.description  Flatten the fields as columns.
     * @oddjob.required No, defaults to false.
     */
    private boolean columns;

    /**
     * @oddjob.description The nested schema to flatten, if known.
     * @oddjob.required No, it will be discovered.
     */
    private DataSchema schema;

    @Override
    public Function<DidoData, List<DidoData>> get() {

        String[] fields = Objects.requireNonNull(this.fields, "Field must be provided");

        if (columns) {

            return Flatten.fields(fields);
        }
        else {
            if (fields.length != 1) {
                throw new IllegalArgumentException("Only one field is supported when flattening a nested schema.");
            }
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

    public DataSchema getSchema() {
        return schema;
    }

    public void setSchema(DataSchema schema) {
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
