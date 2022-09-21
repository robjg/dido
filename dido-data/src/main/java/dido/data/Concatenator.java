package dido.data;

import java.util.*;

/**
 * Provides the ability to Concatenate {@link GenericData}. The data isn't copied but linked in a new master
 * {@code GenericData} object.
 *
 * Concatenation won't cope with repeated or nest fields yet.
 *
 * @param <F> The type of field.
 */
public class Concatenator<F> {


    static class Location {
        private final int dataIndex;
        private final int index;

        Location(int dataSet, int index) {
            this.dataIndex = dataSet;
            this.index = index;
        }
    }

    private final DataSchema<F> schema;

    private final Location[] locations;

    private final Map<F, Location> fieldLocations;

    private Concatenator(DataSchema<F> schema,
                         Location[] locations,
                         Map<F, Location> fieldLocations) {
        this.schema = schema;
        this.locations = locations;
        this.fieldLocations = fieldLocations;
    }

    public static class Settings<F> {

        private final Set<F> excludeFields = new HashSet<>();

        private boolean skipDuplicates;

        public Settings<F> excludeFields(F... exclusions) {
            excludeFields.addAll(List.of(exclusions));
            return this;
        }

        public Settings<F> skipDuplicates(boolean skipDuplicates) {
            this.skipDuplicates = skipDuplicates;
            return this;
        }

        public Concatenator<F> makeFromSchemas(DataSchema<F>... schemas) {

            List<Location> locations = new LinkedList<>();
            Map<F, Location> fieldLocations = new HashMap<>();

            SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();

            int locationIndex = 0;
            int dataIndex = 0;
            for (DataSchema<F> schema : schemas) {
                for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
                    Location location = new Location(dataIndex, i);
                    F field = schema.getFieldAt(i);
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

            return new Concatenator<>(schemaBuilder.build(),
                    locations.toArray(new Location[0]),
                    fieldLocations);
        }

        public Factory<F> factory() {
            return new Factory<>(this);
        }

        public GenericData<F> of(IndexedData<F>... data) {
            return factory().concat(data);
        }
    }

    /**
     * Create a new {@code Concatenator}.
     *
     * @param schemas The schemas of the records that will be concatenated.
     * @param <F>     The type of the field.
     * @return A {@code Concatenator}.
     */
    public static <F> Concatenator<F> fromSchemas(DataSchema<F>... schemas) {

        return new Settings<F>().makeFromSchemas(schemas);
    }

    public static <F> Settings<F> withSettings() {
        return new Settings<>();
    }

    public static <F> GenericData<F> of(IndexedData<F>... data) {
        return new Factory<F>(new Settings<>()).concat(data);
    }

    public static <F> Factory<F> factory() {

        return new Factory<>(new Settings<>());
    }

    public GenericData<F> concat(IndexedData<F>... data) {

        return new ConcatenatedData(data);
    }

    public DataSchema<F> getSchema() {
        return schema;
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     *
     * @param <F> Field Type.
     */
    public static class Factory<F> {

        private final Settings<F> settings;

        private Concatenator<F> last;

        private DataSchema<F>[] previous;

        public Factory(Settings<F> settings) {
            this.settings = settings;
        }

        public GenericData<F> concat(IndexedData<F>... data) {

            boolean recreate = false;
            if (last == null) {
                recreate = true;
                //noinspection unchecked
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
    class ConcatenatedData extends AbstractGenericData<F> implements GenericData<F> {

        private final IndexedData<F>[] data;

        ConcatenatedData(IndexedData<F>[] data) {
            this.data = data;
        }

        @Override
        public DataSchema<F> getSchema() {
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
        public Object get(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getAt(loc.index);
        }

        @Override
        public <T> T getAs(F field, Class<T> type) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getAtAs(loc.index, type);
        }

        @Override
        public boolean hasField(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].hasIndex(loc.index);
        }

        @Override
        public boolean getBoolean(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getBooleanAt(loc.index);
        }

        @Override
        public byte getByte(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getByteAt(loc.index);
        }

        @Override
        public char getChar(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getCharAt(loc.index);
        }

        @Override
        public short getShort(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getShortAt(loc.index);
        }

        @Override
        public int getInt(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getIntAt(loc.index);
        }

        @Override
        public long getLong(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getLongAt(loc.index);
        }

        @Override
        public float getFloat(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getFloatAt(loc.index);
        }

        @Override
        public double getDouble(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getDoubleAt(loc.index);
        }

        @Override
        public String getString(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getStringAt(loc.index);
        }
    }



    /**
     * Almost a Schema with offset.
     *
     * @param <F> Field Type.
     */
    static class OffsetSchema<F> {

        private final DataSchema<F> originalSchema;

        private final SchemaField<F>[] schemaFields;

        private final int offset;

        OffsetSchema(DataSchema<F> originalSchema, int offset) {
            this.originalSchema = originalSchema;
            //noinspection unchecked
            this.schemaFields = new SchemaField[originalSchema.lastIndex()];
            for (int i = originalSchema.firstIndex(); i > 0; i = originalSchema.nextIndex(i)) {
                this.schemaFields[i - 1] = originalSchema.getSchemaFieldAt(i).mapToIndex(i + offset);
            }
            this.offset = offset;
        }

        SchemaField<F> getSchemaFieldAt(int index) {
            return schemaFields[index - offset - 1];
        }

        public F getFieldAt(int index) {
            return originalSchema.getFieldAt(index - offset);
        }

        Class<?> getTypeAt(int index) {
            return originalSchema.getTypeAt(index - offset);
        }

        <N> DataSchema<N> getSchemaAt(int index) {
            return originalSchema.getSchemaAt(index - offset);
        }

        int getIndex(F field) {
            return originalSchema.getIndex(field) + offset;
        }
    }

    /**
     * The Schema.
     *
     * @param <F> Field Type.
     */
    static class CompositeSchema<F> extends AbstractDataSchema<F> {

        private final OffsetSchema<F>[] schemaByIndex;

        private final Map<F, OffsetSchema<F>> schemaByField;

        private final int firstIndex;

        private final int lastIndex;

        private final int[] nextIndex;

        CompositeSchema(OffsetSchema<F>[] schemaByIndex,
                        Map<F, OffsetSchema<F>> schemaByField,
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
        public SchemaField<F> getSchemaFieldAt(int index) {
            return schemaByIndex[index - 1].getSchemaFieldAt(index);
        }

        @Override
        public F getFieldAt(int index) {
            return schemaByIndex[index - 1].getFieldAt(index);
        }

        @Override
        public Class<?> getTypeAt(int index) {
            return schemaByIndex[index - 1].getTypeAt(index);
        }

        @Override
        public <N> DataSchema<N> getSchemaAt(int index) {
            return schemaByIndex[index - 1].getSchemaAt(index);
        }

        @Override
        public int getIndex(F field) {
            return schemaByField.get(field).getIndex(field);
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
        public Collection<F> getFields() {
            return schemaByField.keySet();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DataSchema) {
                return DataSchema.equals(this, (DataSchema<?>) obj);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return DataSchema.hashCode(this);
        }

        @Override
        public String toString() {
            return DataSchema.toString(this);
        }
    }
}
