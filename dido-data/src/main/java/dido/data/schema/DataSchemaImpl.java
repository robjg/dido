package dido.data.schema;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.data.useful.AbstractDataSchema;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An implementation of {@link DataSchema}
 */
public class DataSchemaImpl extends AbstractDataSchema {

    private final Map<String, SchemaField> nameToSchemaField;

    private final int[] nextIndex;

    private final SchemaField[] indexToSchemaField;

    private final int firstIndex;

    private final int lastIndex;

    private volatile int hashCode = -1;

    protected DataSchemaImpl(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {

        this.nameToSchemaField = new LinkedHashMap<>();

        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;

        int offset = firstIndex - 1;
        int[] nextIndex = new int[lastIndex - offset];
        SchemaField[] indexToSchemaField = new SchemaField[lastIndex - offset];

        int last = 0;
        for (SchemaField schemaField : schemaFields) {
            int index = schemaField.getIndex();

            indexToSchemaField[index - firstIndex] = schemaField;

            String name = schemaField.getName();
            if (name != null) {
                nameToSchemaField.put(name, schemaField);
            }

            if (last != 0) {
                nextIndex[last - firstIndex] = index;
            }
            last = index;
        }

        this.nextIndex = nextIndex;
        this.indexToSchemaField = indexToSchemaField;
    }

    protected DataSchemaImpl(DataSchemaImpl other) {
        this.nameToSchemaField = other.nameToSchemaField;
        this.nextIndex = other.nextIndex;
        this.indexToSchemaField = other.indexToSchemaField;
        this.firstIndex = other.firstIndex;
        this.lastIndex = other.lastIndex;
        this.hashCode = other.hashCode;
    }

    /**
     * Create a schema from {@link SchemaField}s. The fields are expected
     * to have a name, a none 0 index, and be in index order.
     *
     * @param schemaFields Schema Fields.
     * @return A Data Schema.
     */
    public static DataSchema fromFields(SchemaField... schemaFields) {

        return new DataSchemaImpl(() -> Arrays.stream(schemaFields).iterator(),
                schemaFields[0].getIndex(), schemaFields[schemaFields.length - 1].getIndex());
    }


    public static  DataSchema fromFields(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {

        return new DataSchemaImpl(schemaFields, firstIndex, lastIndex);
    }

    @Override
    public boolean hasIndex(int index) {
        return index >= this.firstIndex
                && index <= this.lastIndex
                && this.indexToSchemaField[index - this.firstIndex] != null;
    }

    @Override
    public int firstIndex() {
        return firstIndex;
    }

    @Override
    public int lastIndex() {
        return lastIndex;
    }

    @Override
    public int nextIndex(int index) {
        return this.nextIndex[index - firstIndex];
    }

    @Override
    public SchemaField getSchemaFieldAt(int index) {
        if (index < firstIndex || index > lastIndex) {
            return null;
        }
        else {
            return indexToSchemaField[index - firstIndex];
        }
    }

    @Override
    public Type getTypeAt(int index) {
        SchemaField schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getType();
    }

    @Override
    public int getSize() {
        return nameToSchemaField.size();
    }

    @Override
    public DataSchema getSchemaAt(int index) {
        SchemaField schemaField = getSchemaFieldAt(index);
        return schemaField == null ? null : schemaField.getNestedSchema();
    }

    @Override
    public boolean hasNamed(String name) {
        return nameToSchemaField.containsKey(name);
    }

    @Override
    public SchemaField getSchemaFieldNamed(String name) {
        return nameToSchemaField.get(name);
    }

    @Override
    public int getIndexNamed(String name) {
        SchemaField schemaField = nameToSchemaField.get(name);
        return schemaField == null ? 0 : schemaField.getIndex();
    }

    @Override
    public int hashCode() {
        if  (hashCode == -1) {
            hashCode = DataSchema.hashCode(this);
        }
        return hashCode;
    }

}
