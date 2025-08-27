package dido.data.util;

import dido.data.DataSchema;
import dido.data.ReadSchema;
import dido.data.SchemaField;
import dido.data.useful.AbstractDataSchema;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Create a Schema that is a subset of the fields of another schema.
 */
public class SubSchema extends AbstractDataSchema {

    private final DataSchema original;

    private final NavigableMap<Integer, Integer> next;

    protected SubSchema(ReadSchema original, int[] indices) {
        this.original = original;
        this.next = new TreeMap<>();
        for (int i = 0; i < indices.length; ++i) {
            int index = indices[i];
            if (index < 1) {
                throw new IllegalArgumentException("Invalid index " + index);
            }
            next.put(index, i == indices.length - 1 ? 0 : indices[i + 1]);
        }
    }

    public static FieldSelectionFactory<SubSchema> from(DataSchema original) {
        return new FieldSelectionFactory<>(original,
                indices -> new SubSchema(ReadSchema.from(original), indices));
    }

    @Override
    public int firstIndex() {
        return next.firstKey();
    }

    @Override
    public int nextIndex(int index) {
        return next.get(index);
    }

    @Override
    public int lastIndex() {
        return next.lastKey();
    }

    @Override
    public SchemaField getSchemaFieldAt(int index) {
        if (next.containsKey(index)) {
            return original.getSchemaFieldAt(index);
        }
        else {
            return null;
        }
    }

    @Override
    public SchemaField getSchemaFieldNamed(String name) {
        int index = original.getIndexNamed(name);
        return getSchemaFieldAt(index);
    }

    @Override
    public int getSize() {
        return next.size();
    }

}
