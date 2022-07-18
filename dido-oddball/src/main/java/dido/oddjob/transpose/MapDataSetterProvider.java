package dido.oddjob.transpose;

import dido.data.GenericDataBuilder;
import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapData;

public class MapDataSetterProvider<F> implements SetterProvider<F> {

    @Override
    public DataFactory<F> provideSetter(DataSchema<F> schema) {
        GenericDataBuilder<F> builder = MapData.newBuilder(schema);

        DataSetter<F> setter = new DataSetter<>() {

            @Override
            public void set(F field, Object value) {
                builder.set(field, value);
            }

            @Override
            public void setBoolean(F field, boolean value) {
                builder.set(field, value);
            }

            @Override
            public void setByte(F field, byte value) {
                builder.set(field, value);
            }

            @Override
            public void setChar(F field, char value) {
                builder.set(field, value);
            }

            @Override
            public void setShort(F field, short value) {
                builder.set(field, value);
            }

            @Override
            public void setInt(F field, int value) {
                builder.set(field, value);
            }

            @Override
            public void setLong(F field, long value) {
                builder.set(field, value);
            }

            @Override
            public void setFloat(F field, float value) {
                builder.set(field, value);
            }

            @Override
            public void setDouble(F field, double value) {
                builder.set(field, value);
            }

            @Override
            public void setString(F field, String value) {
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
            public DataSetter<F> getSetter() {
                return setter;
            }

            @Override
            public GenericData<F> toData() {
                return builder.build();
            }
        };
    }
}
