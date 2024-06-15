package dido.oddjob.bean;

import dido.data.GenericDataSchema;
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

        GenericDataSchema<String> schema = new ClassSchema(propertyAccessor).schemaForClass(Fruit.class);

        assertThat(schema.getFields(), Matchers.containsInAnyOrder("type", "quantity", "price"));
        assertThat(schema.getTypeOf("type"), is(String.class));
        assertThat(schema.getTypeOf("quantity"), is(int.class));
        assertThat(schema.getTypeOf("price"), is(double.class));
    }

}