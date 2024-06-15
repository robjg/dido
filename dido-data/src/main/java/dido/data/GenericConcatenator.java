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
public class GenericConcatenator<F> {


    static class Location {
        private final int dataIndex;
        private final int index;

        Location(int dataSet, int index) {
            this.dataIndex = dataSet;
            this.index = index;
        }
    }

    private final GenericDataSchema<F> schema;

    private final Location[] locations;

    private final Map<F, Location> fieldLocations;

    private GenericConcatenator(GenericDataSchema<F> schema,
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

        public GenericConcatenator<F> makeFromSchemas(GenericDataSchema<F>... schemas) {

            List<Location> locations = new LinkedList<>();
            Map<F, Location> fieldLocations = new HashMap<>();

            SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();

            int locationIndex = 0;
            int dataIndex = 0;
            for (GenericDataSchema<F> schema : schemas) {
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
                    schemaBuilder.addGenericSchemaField(GenericSchemaField.of(
                            ++locationIndex, field, schema.getTypeAt(i)));
                }
                ++dataIndex;
            }

            return new GenericConcatenator<>(schemaBuilder.build(),
                    locations.toArray(new Location[0]),
                    fieldLocations);
        }

        public Factory<F> factory() {
            return new Factory<>(this);
        }

        public GenericData<F> of(GenericData<F>... data) {
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
    public static <F> GenericConcatenator<F> fromSchemas(GenericDataSchema<F>... schemas) {

        return new Settings<F>().makeFromSchemas(schemas);
    }

    public static <F> Settings<F> withSettings() {
        return new Settings<>();
    }

    public static <F> GenericData<F> of(GenericData<F>... data) {
        return new Factory<F>(new Settings<>()).concat(data);
    }

    public static <F> Factory<F> factory() {

        return new Factory<>(new Settings<>());
    }

    public GenericData<F> concat(GenericData<F>... data) {

        return new ConcatenatedData(data);
    }

    public GenericDataSchema<F> getSchema() {
        return schema;
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     *
     * @param <F> Field Type.
     */
    public static class Factory<F> {

        private final Settings<F> settings;

        private GenericConcatenator<F> last;

        private GenericDataSchema<F>[] previous;

        public Factory(Settings<F> settings) {
            this.settings = settings;
        }

        public GenericData<F> concat(GenericData<F>... data) {

            boolean recreate = false;
            if (last == null) {
                recreate = true;
                //noinspection unchecked
                previous = new GenericDataSchema[data.length];
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

        private final GenericData<F>[] data;

        ConcatenatedData(GenericData<F>[] data) {
            this.data = data;
        }

        @Override
        public GenericDataSchema<F> getSchema() {
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

        @Override
        public Object getOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getAt(loc.index);
        }

        @Override
        public <T> T getOfAs(F field, Class<T> type) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getAtAs(loc.index, type);
        }

        @Override
        public boolean hasFieldOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].hasIndex(loc.index);
        }

        @Override
        public boolean getBooleanOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getBooleanAt(loc.index);
        }

        @Override
        public byte getByteOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getByteAt(loc.index);
        }

        @Override
        public char getCharOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getCharAt(loc.index);
        }

        @Override
        public short getShortOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getShortAt(loc.index);
        }

        @Override
        public int getIntOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getIntAt(loc.index);
        }

        @Override
        public long getLongOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getLongAt(loc.index);
        }

        @Override
        public float getFloatOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getFloatAt(loc.index);
        }

        @Override
        public double getDoubleOf(F field) {
            Location loc = fieldLocations.get(field);
            return data[loc.dataIndex].getDoubleAt(loc.index);
        }

        @Override
        public String getStringOf(F field) {
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

        private final GenericDataSchema<F> originalSchema;

        private final GenericSchemaField<F>[] schemaFields;

        private final int offset;

        OffsetSchema(GenericDataSchema<F> originalSchema, int offset) {
            this.originalSchema = originalSchema;
            //noinspection unchecked
            this.schemaFields = new GenericSchemaField[originalSchema.lastIndex()];
            for (int i = originalSchema.firstIndex(); i > 0; i = originalSchema.nextIndex(i)) {
                this.schemaFields[i - 1] = originalSchema.getSchemaFieldAt(i).mapToIndex(i + offset);
            }
            this.offset = offset;
        }

        GenericSchemaField<F> getSchemaFieldAt(int index) {
            return schemaFields[index - offset - 1];
        }

        public F getFieldAt(int index) {
            return originalSchema.getFieldAt(index - offset);
        }

        Class<?> getTypeAt(int index) {
            return originalSchema.getTypeAt(index - offset);
        }

        DataSchema getSchemaAt(int index) {
            return originalSchema.getSchemaAt(index - offset);
        }

        int getIndex(F field) {
            return originalSchema.getIndex(field) + offset;
        }
    }

}
