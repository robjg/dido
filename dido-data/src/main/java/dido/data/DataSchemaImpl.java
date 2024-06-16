package dido.data;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataSchemaImpl extends AbstractDataSchema {

    private final Map<String, SchemaField> nameToSchemaField;

    private final int[] nextIndex;

    private final SchemaField[] indexToSchemaField;

    private final int firstIndex;

    private final int lastIndex;

    private volatile int hashCode = -1;

    private DataSchemaImpl(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {

        this.nameToSchemaField = new LinkedHashMap<>();

        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;

        int offset = firstIndex - 1;
        Object[] indexToField =  new Object[lastIndex - offset];
        int[] nextIndex = new int[lastIndex - offset];
        SchemaField[] indexToSchemaField = new SchemaField[lastIndex - offset];

        int last = 0;
        for (SchemaField meta : schemaFields) {
            int index = meta.getIndex();

            indexToSchemaField[index - firstIndex] = meta;

            String name = meta.getName();
            if (name != null) {
                nameToSchemaField.put(name, meta);
            }

            if (last != 0) {
                nextIndex[last - firstIndex] = index;
            }
            last = index;
        }

        this.nextIndex = nextIndex;
        this.indexToSchemaField = indexToSchemaField;
    }

    public static  DataSchema fromFields(SchemaField... schemaFields) {

        return new DataSchemaImpl(() -> Arrays.stream(schemaFields).iterator(),
                schemaFields[0].getIndex(), schemaFields[schemaFields.length - 1].getIndex());
    }


    public static  DataSchema fromFields(Iterable<SchemaField> schemaFields, int firstIndex, int lastIndex) {

        return new DataSchemaImpl(schemaFields, firstIndex, lastIndex);
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
        return indexToSchemaField[index - firstIndex];
    }

    @Override
    public Class<?> getTypeAt(int index) {
        return indexToSchemaField[index - firstIndex].getType();
    }

    @Override
    public DataSchema getSchemaAt(int index) {
        SchemaField schemaField = indexToSchemaField[index - firstIndex];
        return schemaField == null ? null : schemaField.getNestedSchema();
    }

    @Override
    public SchemaField getSchemaFieldNamed(String fieldName) {
        return nameToSchemaField.get(fieldName);
    }

    @Override
    public int getIndexNamed(String fieldName) {
        SchemaField schemaField = nameToSchemaField.get(fieldName);
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
