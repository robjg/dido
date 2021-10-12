package dido.data;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class EnumMapDataTest {

    public interface HasType {

        Class<?> getType();
    }

    enum Fields implements HasType {
        Fruit(String.class),
        Qty(int.class),
        Price(double.class);

        private final Class<?> type;

        Fields(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    @Test
    void testSchema() {

        EnumSchema<Fields> schema = EnumSchema.schemaFor(Fields.class, Fields::getType);

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));

        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getType(Fields.Fruit), is(String.class));
        assertThat(schema.getTypeAt(2), is(int.class));
        assertThat(schema.getType(Fields.Qty), is(int.class));
        assertThat(schema.getTypeAt(3), is(double.class));
        assertThat(schema.getType(Fields.Price), is(double.class));

        assertThat(schema.getFieldAt(1), is(Fields.Fruit));
        assertThat(schema.getIndex(Fields.Fruit), is(1));
        assertThat(schema.getFieldAt(2), is(Fields.Qty));
        assertThat(schema.getIndex(Fields.Qty), is(2));
        assertThat(schema.getFieldAt(3), is(Fields.Price));
        assertThat(schema.getIndex(Fields.Price), is(3));

        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.getFields(), contains(Fields.Fruit, Fields.Qty, Fields.Price));
    }

    @Test
    void builderWithNoSchema() {

        DataBuilder<Fields> builderNoSchema = EnumMapData.builderForEnum(Fields.class);

        GenericData<Fields> data3 = builderNoSchema
                .setString(Fields.Fruit, "Apple")
                .setInt(Fields.Qty, 2)
                .setDouble(Fields.Price, 75.3)
                .build();

        assertThat(data3.getSchema().getType(Fields.Qty), is(int.class));
        assertThat(data3.getSchema().getTypeAt(2), is(int.class));

        assertThat(data3.getSchema().getType(Fields.Price), is(double.class));
        assertThat(data3.getSchema().getTypeAt(3), is(double.class));
    }

    @Test
    void testWithHasType() {

        EnumSchema<Fields> schema = EnumSchema.schemaFor(Fields.class, Fields::getType);

        DataBuilder<Fields> builder = EnumMapData.newBuilder(schema);

        GenericData<Fields> data1 = builder
                .set(Fields.Fruit, "Apple")
                .set(Fields.Qty, 2)
                .set(Fields.Price, 75.3)
                .build();

        GenericData<Fields> data2 = builder
                .set(Fields.Fruit, "Orange")
                .set(Fields.Qty, 3)
                .set(Fields.Price, 47.3)
                .build();

        assertThat(data1.hasIndex(1), is(true));
        assertThat(data1.hasField(Fields.Fruit), is(true));

        assertThat(data1.getString(Fields.Fruit), is("Apple"));
        assertThat(data1.getStringAt(1), is("Apple"));
        assertThat(data1.getInt(Fields.Qty), is(2));
        assertThat(data1.getIntAt(2), is(2));
        assertThat(data1.getDouble(Fields.Price), is(75.3));
        assertThat(data1.getDoubleAt(3), is(75.3));

        assertThat(data2.getString(Fields.Fruit), is("Orange"));
        assertThat(data2.getStringAt(1), is("Orange"));

        DataBuilder<Fields> builderNoSchema = EnumMapData.builderForEnum(Fields.class);

        GenericData<Fields> data3 = builderNoSchema
                .setString(Fields.Fruit, "Apple")
                .setInt(Fields.Qty, 2)
                .setDouble(Fields.Price, 75.3)
                .build();

        assertThat(data3.getSchema(), is(data1.getSchema()));

        assertThat(data3, is(data1));
        assertThat(data3.hashCode(), is(data1.hashCode()));

        assertThat(data1.toString(), is("{[Fruit]=Apple, [Qty]=2, [Price]=75.3}"));
    }

}