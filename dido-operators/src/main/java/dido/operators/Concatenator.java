package dido.operators;

import dido.data.NoSuchFieldException;
import dido.data.*;
import dido.data.useful.AbstractData;
import dido.data.useful.DataSchemaImpl;
import dido.data.useful.SchemaFactoryImpl;

import java.util.*;

/**
 * Provides the ability to Concatenate {@link DidoData}. The data isn't copied but linked in a new master
 * {@code DidoData} object.
 * <p>
 * Concatenation won't cope with repeated or nest fields yet.
 */
public class Concatenator {


    static class Location {
        private final int dataIndex;
        private final int index;
        private final FieldGetter getter;

        Location(int dataSet, int index, FieldGetter getter) {
            this.dataIndex = dataSet;
            this.index = index;
            this.getter = getter;
        }
    }

    private final ReadSchema schema;

    private final Location[] locations;

    private final Map<String, Location> fieldLocations;

    private Concatenator(ReadSchema schema,
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

            class Schema extends DataSchemaImpl implements ReadSchema {

                Schema(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
                    super(fields, firstIndex, lastIndex);
                }

                @Override
                public FieldGetter getFieldGetterAt(int index) {
                    if (!hasIndex(index)) {
                        throw new dido.data.NoSuchFieldException(index, this);
                    }
                    return getFieldGetterNamed(getFieldNameAt(index));
                }

                @Override
                public FieldGetter getFieldGetterNamed(String name) {
                    Location location = fieldLocations.get(name);
                    if (location == null) {
                        throw new NoSuchFieldException(name, this);
                    }
                    ;
                    return location.getter;
                }
            }

            SchemaFactoryImpl<ReadSchema> schemaFactory = new SchemaFactoryImpl<>() {
                @Override
                protected ReadSchema create(Collection<SchemaField> fields, int firstIndex, int lastIndex) {
                    return new Schema(fields, firstIndex, lastIndex);
                }
            };

            int locationIndex = 0;
            int dataIndex = 0;
            for (DataSchema schema : schemas) {
                ReadStrategy readStrategy = ReadStrategy.fromSchema(schema);
                for (int i = schema.firstIndex(); i > 0; i = schema.nextIndex(i)) {
                    Location location = new Location(dataIndex, i, readStrategy.getFieldGetterAt(i));
                    SchemaField schemaField = schema.getSchemaFieldAt(i);
                    String name = schemaField.getName();
                    if (excludeFields.contains(name)) {
                        continue;
                    }
                    if (fieldLocations.containsKey(name)) {
                        if (skipDuplicates) {
                            continue;
                        } else {
                            throw new IllegalArgumentException("Fields must be unique: " + name);
                        }
                    }
                    fieldLocations.put(name, location);
                    locations.add(location);
                    schemaFactory.addSchemaField(schemaField.mapToIndex(++locationIndex));
                }
                ++dataIndex;
            }

            return new Concatenator(schemaFactory.toSchema(),
                    locations.toArray(new Location[0]),
                    fieldLocations);
        }

        public Factory factory() {
            return new Factory(this);
        }

        public DidoData of(DidoData... data) {
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

    public static DidoData of(DidoData... data) {
        return new Factory(new Settings()).concat(data);
    }

    public static Factory factory() {

        return new Factory(new Settings());
    }

    public DidoData concat(IndexedData... data) {

        return new ConcatenatedData(data);
    }

    public ReadSchema getSchema() {
        return schema;
    }

    /**
     * Compares previous schemas so we can maybe shortcut.
     */
    public static class Factory {

        private final Settings settings;

        private Concatenator last;

        private ReadSchema[] previous;

        public Factory(Settings settings) {
            this.settings = settings;
        }

        public DidoData concat(DidoData... data) {

            boolean recreate = false;
            if (last == null) {
                recreate = true;
                previous = new ReadSchema[data.length];
                for (int i = 0; i < data.length; ++i) {
                    previous[i] = ReadSchema.from(data[i].getSchema());
                }
            } else {
                for (int i = 0; i < data.length; ++i) {
                    if (previous[i] != data[i].getSchema()) {
                        recreate = true;
                        previous[i] = ReadSchema.from(data[i].getSchema());
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
        public ReadSchema getSchema() {
            return schema;
        }

        @Override
        public Object getAt(int index) {
            Location loc = locations[index - 1];
            return data[loc.dataIndex].getAt(loc.index);
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
        public Object getNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getAt(loc.index);
        }

        @Override
        public boolean hasNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].hasIndex(loc.index);
        }

        @Override
        public boolean getBooleanNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getBooleanAt(loc.index);
        }

        @Override
        public byte getByteNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getByteAt(loc.index);
        }

        @Override
        public char getCharNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getCharAt(loc.index);
        }

        @Override
        public short getShortNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getShortAt(loc.index);
        }

        @Override
        public int getIntNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getIntAt(loc.index);
        }

        @Override
        public long getLongNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getLongAt(loc.index);
        }

        @Override
        public float getFloatNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getFloatAt(loc.index);
        }

        @Override
        public double getDoubleNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getDoubleAt(loc.index);
        }

        @Override
        public String getStringNamed(String name) {
            Location loc = fieldLocations.get(name);
            return data[loc.dataIndex].getStringAt(loc.index);
        }
    }
}
