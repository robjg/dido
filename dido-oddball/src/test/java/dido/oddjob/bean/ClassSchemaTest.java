package dido.oddjob.bean;

import dido.data.DataSchema;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ClassSchemaTest {

    @Test
    void classToSchema() {

        PropertyAccessor propertyAccessor = new BeanUtilsPropertyAccessor();

        DataSchema<String> schema = new ClassSchema(propertyAccessor).schemaForClass(Fruit.class);

        assertThat(schema.getFields(), Matchers.containsInAnyOrder("type", "quantity", "price"));
        assertThat(schema.getType("type"), is(String.class));
        assertThat(schema.getType("quantity"), is(int.class));
        assertThat(schema.getType("price"), is(double.class));
    }

}