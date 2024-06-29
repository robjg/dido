package dido.oddjob.bean;


import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
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

        DidoData data = MapData.newBuilderNoSchema()
                .withString("type", "Apple")
                .withInt("quantity", 5)
                .withDouble("price", 27.3)
                .build();

        Function<DidoData, Fruit> test = new ToBeanArooa(new BeanUtilsPropertyAccessor())
                .ofClass(Fruit.class);

        Fruit fruit = test.apply(data);

        assertThat(fruit.getType(), is("Apple"));
        assertThat(fruit.getQuantity(), is(5));
        assertThat(fruit.getPrice(), is(27.3));
    }

    @Test
    public void testOfSchema() {

        DataSchema schema = SchemaBuilder.newInstance()
                .addNamed("boolean", boolean.class)
                .addNamed("byte", byte.class)
                .addNamed("char", char.class)
                .addNamed("short", short.class)
                .addNamed("int", int.class)
                .addNamed("long", long.class)
                .addNamed("float", float.class)
                .addNamed("double", double.class)
                .build();

        Function<DidoData, Object> test = new ToBeanArooa(new BeanUtilsPropertyAccessor())
                .ofSchema(schema);

        DidoData data = MapData.newBuilder(schema)
                .withBoolean("boolean", true)
                .withByte("byte", (byte) 1)
                .withChar("char", 'A')
                .withShort("short", (short) 2)
                .withInt("int", 3)
                .withLong("long", 4L)
                .withFloat("float", 1.1F)
                .withDouble("double", 2.2)
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