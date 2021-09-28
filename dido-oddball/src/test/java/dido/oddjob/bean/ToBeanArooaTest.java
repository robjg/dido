package dido.oddjob.bean;


import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapRecord;
import dido.data.SchemaBuilder;
import org.apache.commons.beanutils.DynaBean;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ToBeanArooaTest {

    @Test
    void testToBean() {

        GenericData<String> data = MapRecord.newBuilderNoSchema()
                .setString("type", "Apple")
                .setInt("quantity", 5)
                .setDouble("price", 27.3)
                .build();

        Function<GenericData<String>, Fruit> test = new ToBeanArooa(new BeanUtilsPropertyAccessor())
                .ofClass(Fruit.class);

        Fruit fruit = test.apply(data);

        assertThat(fruit.getType(), is("Apple"));
        assertThat(fruit.getQuantity(), is(5));
        assertThat(fruit.getPrice(), is(27.3));
    }

    @Test
    public void testOfSchema() {

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("boolean", boolean.class)
                .addField("byte", byte.class)
                .addField("char", char.class)
                .addField("short", short.class)
                .addField("int", int.class)
                .addField("long", long.class)
                .addField("float", float.class)
                .addField("double", double.class)
                .build();

        Function<GenericData<String>, Object> test = new ToBeanArooa(new BeanUtilsPropertyAccessor())
                .ofSchema(schema);

        GenericData<String> data = MapRecord.newBuilder(schema)
                .setBoolean("boolean", true)
                .setByte("byte", (byte) 1)
                .setChar("char", 'A')
                .setShort("short", (short) 2)
                .setInt("int", 3)
                .setLong("long", 4L)
                .setFloat("float", 1.1F)
                .setDouble("double", 2.2)
                .build();

        DynaBean bean = (DynaBean) test.apply(data);

        assertThat(bean.get("boolean"), is(true));
        assertThat(bean.get("byte"), is((byte) 1));
        assertThat(bean.get("char"), is('A'));
        assertThat(bean.get("short"), is((short) 2));
        assertThat(bean.get("int"), is(3));
        assertThat(bean.get("long"), is(4L));
        assertThat(bean.get("float"), is(1.1F));
        assertThat(bean.get("double"), is(2.2));
    }
}