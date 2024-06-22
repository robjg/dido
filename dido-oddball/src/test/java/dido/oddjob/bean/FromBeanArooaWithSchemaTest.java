package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.NamedData;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FromBeanArooaWithSchemaTest {

    @Test
    void testToBean() {

        Fruit fruit = new Fruit();
        fruit.setType("Apple");
        fruit.setQuantity(5);
        fruit.setPrice(27.3);

        PropertyAccessor propertyAccessor = new BeanUtilsPropertyAccessor();

        DataSchema schema = new ClassSchema(propertyAccessor).schemaForClass(Fruit.class);

        NamedData data = new FromBeanArooaWithSchema<>(schema, propertyAccessor).apply(fruit);

        assertThat(data.getString("type"), is("Apple"));
        assertThat(data.getInt("quantity"), is(5));
        assertThat(data.getDouble("price"), is(27.3));
    }

}