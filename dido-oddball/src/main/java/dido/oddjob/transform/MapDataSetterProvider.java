package dido.oddjob.transform;

import dido.data.DataBuilder;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapData;

public class MapDataSetterProvider implements SetterProvider<String> {

    @Override
    public DataFactory<String> provideSetter(DataSchema<String> schema) {
        DataBuilder builder = MapData.newBuilder(schema);

        DataSetter<String> setter = new DataSetter<>() {

            @Override
            public void set(String field, Object value) {
                builder.set(field, value);
            }

            @Override
            public void setBoolean(String field, boolean value) {
                builder.set(field, value);
            }

            @Override
            public void setByte(String field, byte value) {
                builder.set(field, value);
            }

            @Override
            public void setChar(String field, char value) {
                builder.set(field, value);
            }

            @Override
            public void setShort(String field, short value) {
                builder.set(field, value);
            }

            @Override
            public void setInt(String field, int value) {
                builder.set(field, value);
            }

            @Override
            public void setLong(String field, long value) {
                builder.set(field, value);
            }

            @Override
            public void setFloat(String field, float value) {
                builder.set(field, value);
            }

            @Override
            public void setDouble(String field, double value) {
                builder.set(field, value);
            }

            @Override
            public void setString(String field, String value) {
                builder.set(field, value);
            }

            @Override
            public void setAt(int index, Object value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setBooleanAt(int index, boolean value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setByteAt(int index, byte value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setShortAt(int index, short value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setIntAt(int index, int value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setLongAt(int index, long value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setFloatAt(int index, float value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setDoubleAt(int index, double value) {
                builder.set(schema.getFieldAt(index), value);
            }

            @Override
            public void setStringAt(int index, String value) {
                builder.set(schema.getFieldAt(index), value);
            }
        };

        return new DataFactory<>() {
            @Override
            public DataSetter<String> getSetter() {
                return setter;
            }

            @Override
            public GenericData<String> toData() {
                return builder.build();
            }
        };
    }
}
