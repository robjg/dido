package dido.data;

public interface DidoData extends GenericData<String> {

    static DidoData adapt(GenericData<String> data) {
        return new AbstractData() {
            @Override
            public Object get(String field) {
                return data.get(field);
            }

            @Override
            public <T> T getAs(String field, Class<T> type) {
                return data.getAs(field, type);
            }

            @Override
            public boolean hasField(String field) {
                return data.hasField(field);
            }

            @Override
            public boolean getBoolean(String field) {
                return data.getBoolean(field);
            }

            @Override
            public byte getByte(String field) {
                return data.getByte(field);
            }

            @Override
            public char getChar(String field) {
                return data.getChar(field);
            }

            @Override
            public short getShort(String field) {
                return data.getShort(field);
            }

            @Override
            public int getInt(String field) {
                return data.getInt(field);
            }

            @Override
            public long getLong(String field) {
                return data.getLong(field);
            }

            @Override
            public float getFloat(String field) {
                return data.getFloat(field);
            }

            @Override
            public double getDouble(String field) {
                return data.getDouble(field);
            }

            @Override
            public String getString(String field) {
                return data.getString(field);
            }

            @Override
            public DataSchema<String> getSchema() {
                return data.getSchema();
            }

            @Override
            public Object getAt(int index) {
                return data.getAt(index);
            }

            @Override
            public <T> T getAtAs(int index, Class<T> type) {
                return data.getAtAs(index, type);
            }

            @Override
            public boolean hasIndex(int index) {
                return data.hasIndex(index);
            }

            @Override
            public boolean getBooleanAt(int index) {
                return data.getBooleanAt(index);
            }

            @Override
            public byte getByteAt(int index) {
                return data.getByteAt(index);
            }

            @Override
            public short getShortAt(int index) {
                return data.getShortAt(index);
            }

            @Override
            public int getIntAt(int index) {
                return data.getIntAt(index);
            }

            @Override
            public long getLongAt(int index) {
                return data.getLongAt(index);
            }

            @Override
            public float getFloatAt(int index) {
                return data.getFloatAt(index);
            }

            @Override
            public double getDoubleAt(int index) {
                return data.getDoubleAt(index);
            }

            @Override
            public String getStringAt(int index) {
                return data.getStringAt(index);
            }
        };
    }
}
