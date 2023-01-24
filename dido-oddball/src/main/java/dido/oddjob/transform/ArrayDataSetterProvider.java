package dido.oddjob.transform;

import dido.data.*;

public class ArrayDataSetterProvider<F> implements SetterProvider<F> {

    @Override
    public DataFactory<F> provideSetter(DataSchema<F> schema) {

        ArrayData.Builder<F> builder = ArrayData.builderForSchema(schema);

        DataSetter<F> setter = new DataSetter<F>() {

            @Override
            public void set(F field, Object value) {
                builder.setAt(schema.getIndex(field), value);
            }

            @Override
            public void setBoolean(F field, boolean value) {
                set(field, value);
            }

            @Override
            public void setByte(F field, byte value) {
                set(field, value);
            }

            @Override
            public void setChar(F field, char value) {
                set(field, value);
            }

            @Override
            public void setShort(F field, short value) {
                set(field, value);
            }

            @Override
            public void setInt(F field, int value) {
                set(field, value);
            }

            @Override
            public void setLong(F field, long value) {
                set(field, value);
            }

            @Override
            public void setFloat(F field, float value) {
                set(field, value);
            }

            @Override
            public void setDouble(F field, double value) {
                set(field, value);
            }

            @Override
            public void setString(F field, String value) {
                set(field, value);
            }

            @Override
            public void setAt(int index, Object value) {
                builder.setAt(index, value);
            }

            @Override
            public void setBooleanAt(int index, boolean value) {
                setAt(index, value);
            }

            @Override
            public void setByteAt(int index, byte value) {
                setAt(index, value);
            }

            @Override
            public void setShortAt(int index, short value) {
                setAt(index, value);
            }

            @Override
            public void setIntAt(int index, int value) {
                setAt(index, value);
            }

            @Override
            public void setLongAt(int index, long value) {
                setAt(index, value);
            }

            @Override
            public void setFloatAt(int index, float value) {
                setAt(index, value);
            }

            @Override
            public void setDoubleAt(int index, double value) {
                setAt(index, value);
            }

            @Override
            public void setStringAt(int index, String value) {
                setAt(index, value);
            }

        };

        return new DataFactory<F>() {
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
