package dido.data;

import java.util.*;

/**
 * Provides the ability to Concatenate {@link DidoData}. The data isn't copied but linked in a new master
 * {@code DidoData} object.
 *
 * Concatenation won't cope with repeated or nest fields yet.
 *
 */
public class Concatenator {


    static class Location {
        private final int dataIndex;
        private final int index;

        Location(int dataSet, int index) {
            this.dataIndex = dataSet;
            this.index = index;
        }
    }

    private final DataSchema schema;

    private final Location[] locations;

    private final Map<String, Location> fieldLocations;

    private Concatenator(DataSchema schema,
                         Location[] locations,
                         Map<String, Location> fieldLocations) {
        this.schema = schema;
        this.locations = locations;
        this.fieldLocations = fieldLocations;
    }

    public static class Settings {

        private final Set<String> excludeFields = new HashSet<>();

        private boolean skipDuplicates;

        public Settings excludeFields(String... exclusions) {
            excludeFields.addAll(List.of(exclusions));
            return this;
        }

        public Settings skipDuplicates(boolean skipDuplicates) {
            this.skipDuplicates = skipDuplicates;
            return this;
        }

        public Concatenator makeFromSchemas(DataSchema... schemas) {

            List<Location> locations = new LinkedList<>();
            Map<String, Location> fieldLocations = new HashMap<>();

            SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

            int locationIndex = 0;
            int dataIndex = 0;
            for (DataSchema schema : schemas) {
                for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
                    Location location = new Location(dataIndex, i);
                    String field = schema.getFieldNameAt(i);
                    if (field != null) {
                        if (excludeFields.contains(field)) {
                            continue;
                        }
                        if (fieldLocations.containsKey(field)) {
                            if (skipDuplicates) {
                                continue;
                            }
                            else {
                                throw new IllegalArgumentException("Fields must be unique: " + field);
                            }
                        }
                        fieldLocations.put(field, location);
                    }
                    locations.add(location);
                    schemaBuilder.addSchemaField(SchemaField.of(
                            ++locationIndex, field, schema.getTypeAt(i)));
                }
                ++dataIndex;
            }

            return new Concatenator(schemaBuilder.build(),
                    locations.toArray(new Location[0]),
                    fieldLocations);
        }

        public Factory factory() {
            return new Factory(this);
        }

        public DidoData of(IndexedData... data) {
            return factory().concat(data);
        }
    }

    /**
     * Create a new {@code Concatenator}.
     *
     * @param schemas The schemas of the records that will be concatenated.
     * @return A {@code Concatenator}.
     */
    public static Concatenator fromSchemas(DataSchema... schemas) {

        return new Settings().makeFromSchemas(schemas);
    }

    public static Settings withSettings() {
        return new Settings();
    }

    public static DidoData of(IndexedData... data) {
        return new Factory(new Settings()).concat(data);
    }

    public static Factory factory() {

        return new Factory(new Settings());
    }

    public DidoData concat(IndexedData... data) {

        return new ConcatenatedData(data);
    }

    public DataSchema getSchema() {
        return schema;
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     *
     */
    public static class Factory {

        private final Settings settings;

        private Concatenator last;

        private DataSchema[] previous;

        public Factory(Settings settings) {
            this.settings = settings;
        }

        public DidoData concat(IndexedData... data) {

            boolean recreate = false;
            if (last == null) {
                recreate = true;
                previous = new DataSchema[data.length];
                for (int i = 0; i < data.length; ++i) {
                    previous[i] = data[i].getSchema();
                }
            } else {
                for (int i = 0; i < data.length; ++i) {
                    if (previous[i] != data[i].getSchema()) {
                        recreate = true;
                        previous[i] = data[i].getSchema();
                    }
                }
            }

            if (recreate) {
                last = settings.makeFromSchemas(previous);
            }

            return last.concat(data);
        }
    }

    /**
     * The Data
     */
    class ConcatenatedData extends AbstractData implements DidoData {

        private final IndexedData[] data;

        ConcatenatedData(IndexedData[] data) {
            this.data = data;
        }

        @Override
        public DataSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getAt(loc.index);
        }

        @Override
        public <T> T getAtAs(int index, Class<T> type) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getAtAs(loc.index, type);
        }

        @Override
        public boolean hasIndex(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].hasIndex(loc.index);
        }

        @Override
        public String getStringAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getStringAt(loc.index);
        }

        @Override
        public boolean getBooleanAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getBooleanAt(loc.index);
        }

        @Override
        public byte getByteAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getByteAt(loc.index);
        }

        @Override
        public char getCharAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getCharAt(loc.index);
        }

        @Override
        public short getShortAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getShortAt(loc.index);
        }

        @Override
        public int getIntAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getIntAt(loc.index);
        }

        @Override
        public long getLongAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getLongAt(loc.index);
        }

        @Override
        public float getFloatAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getFloatAt(loc.index);
        }

        @Override
        public double getDoubleAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getDoubleAt(loc.index);
        }

        @Override
        public Object get(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getAt(loc.index);
        }

        @Override
        public <T> T getAs(String field, Class<T> type) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getAtAs(loc.index, type);
        }

        @Override
        public boolean hasField(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].hasIndex(loc.index);
        }

        @Override
        public boolean getBoolean(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getBooleanAt(loc.index);
        }

        @Override
        public byte getByte(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getByteAt(loc.index);
        }

        @Override
        public char getChar(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getCharAt(loc.index);
        }

        @Override
        public short getShort(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getShortAt(loc.index);
        }

        @Override
        public int getInt(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getIntAt(loc.index);
        }

        @Override
        public long getLong(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getLongAt(loc.index);
        }

        @Override
        public float getFloat(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getFloatAt(loc.index);
        }

        @Override
        public double getDouble(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getDoubleAt(loc.index);
        }

        @Override
        public String getString(String field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getStringAt(loc.index);
        }
    }



    /**
     * Almost a Schema with offset.
     */
    static class OffsetSchema {

        private final DataSchema originalSchema;

        private final SchemaField[] schemaFields;

        private final int offset;

        OffsetSchema(DataSchema originalSchema, int offset) {
            this.originalSchema = originalSchema;
            this.schemaFields = new SchemaField[originalSchema.lastIndex()];
            for (int i = originalSchema.firstIndex(); i > 0; i = originalSchema.nextIndex(i)) {
                this.schemaFields[i - 1] = originalSchema.getSchemaFieldAt(i).mapToIndex(i + offset);
            }
            this.offset = offset;
        }

        SchemaField getSchemaFieldAt(int index) {
            return schemaFields[index - offset - 1];
        }

        public String getFieldNameAt(int index) {
            return originalSchema.getFieldNameAt(index - offset);
        }

        Class<?> getTypeAt(int index) {
            return originalSchema.getTypeAt(index - offset);
        }

        DataSchema getSchemaAt(int index) {
            return originalSchema.getSchemaAt(index - offset);
        }

        int getIndexNamed(String field) {
            return originalSchema.getIndexNamed(field) + offset;
        }
    }

    /**
     * The Schema.
     *
     */
    static class CompositeSchema extends AbstractDataSchema implements DataSchema {

        private final OffsetSchema[] schemaByIndex;

        private final Map<String, OffsetSchema> schemaByField;

        private final int firstIndex;

        private final int lastIndex;

        private final int[] nextIndex;

        CompositeSchema(OffsetSchema[] schemaByIndex,
                        Map<String, OffsetSchema> schemaByField,
                        int firstIndex,
                        int lastIndex,
                        int[] nextIndex) {
            this.schemaByIndex = schemaByIndex;
            this.schemaByField = schemaByField;
            this.firstIndex = firstIndex;
            this.lastIndex = lastIndex;
            this.nextIndex = nextIndex;
        }

        @Override
        public SchemaField getSchemaFieldAt(int index) {
            return schemaByIndex[index - 1].getSchemaFieldAt(index);
        }

        @Override
        public String getFieldNameAt(int index) {
            return schemaByIndex[index - 1].getFieldNameAt(index);
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return schemaByIndex[index - 1].getTypeAt(index);
        }

        @Override
        public DataSchema getSchemaAt(int index) {
            return schemaByIndex[index - 1].getSchemaAt(index);
        }

        @Override
        public int getIndexNamed(String fieldName) {
            return schemaByField.get(fieldName).getIndexNamed(fieldName);
        }

        @Override
        public int firstIndex() {
            return firstIndex;
        }

        @Override
        public int nextIndex(int index) {

            return nextIndex[index - 1];
        }

        @Override
        public int lastIndex() {
            return lastIndex;
        }

        @Override
        public Collection<String> getFieldNames() {
            return schemaByField.keySet();
        }
    }
}
