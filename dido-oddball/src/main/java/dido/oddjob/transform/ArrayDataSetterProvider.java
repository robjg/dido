package dido.oddjob.transform;

import dido.data.ArrayData;
import dido.data.DataSchema;
import dido.data.DidoData;

public class ArrayDataSetterProvider implements SetterProvider {

    @Override
    public DataFactory provideSetter(DataSchema schema) {

        ArrayData.Builder builder = ArrayData.builderForSchema(schema);

        DataSetter setter = new DataSetter() {

            @Override
            public void set(String field, Object value) {
                builder.setAt(schema.getIndexNamed(field), value);
            }

            @Override
            public void setBoolean(String field, boolean value) {
                set(field, value);
            }

            @Override
            public void setByte(String field, byte value) {
                set(field, value);
            }

            @Override
            public void setChar(String field, char value) {
                set(field, value);
            }

            @Override
            public void setShort(String field, short value) {
                set(field, value);
            }

            @Override
            public void setInt(String field, int value) {
                set(field, value);
            }

            @Override
            public void setLong(String field, long value) {
                set(field, value);
            }

            @Override
            public void setFloat(String field, float value) {
                set(field, value);
            }

            @Override
            public void setDouble(String field, double value) {
                set(field, value);
            }

            @Override
            public void setString(String field, String value) {
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

        return new DataFactory() {
            @Override
            public DataSetter getSetter() {
                return setter;
            }

            @Override
            public DidoData toData() {
                return builder.build();
            }
        };
    }
}
