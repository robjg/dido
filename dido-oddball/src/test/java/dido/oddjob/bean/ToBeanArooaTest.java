package dido.oddjob.bean;


import dido.data.GenericData;
import dido.data.MapRecord;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ToBeanArooaTest {

    @Test
    void testToBean() {

        ArooaClass arooaClass = new SimpleArooaClass(Fruit.class);

        GenericData<String> data = MapRecord.newBuilderNoSchema()
                .setString("type", "Apple")
                .setInt("quantity", 5)
                .setDouble("price", 27.3)
                .build();

        Fruit fruit = (Fruit) new ToBeanArooa(arooaClass, new BeanUtilsPropertyAccessor()).apply(data);

        assertThat(fruit.getType(), is("Apple"));
        assertThat(fruit.getQuantity(), is(5));
        assertThat(fruit.getPrice(), is(27.3));
    }

}