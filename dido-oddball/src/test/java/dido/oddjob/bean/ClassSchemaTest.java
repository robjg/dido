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

        DataSchema schema = new ClassSchema(propertyAccessor).schemaForClass(Fruit.class);

        assertThat(schema.getFieldNames(), Matchers.containsInAnyOrder("type", "quantity", "price"));
        assertThat(schema.getTypeNamed("type"), is(String.class));
        assertThat(schema.getTypeNamed("quantity"), is(int.class));
        assertThat(schema.getTypeNamed("price"), is(double.class));
    }

}