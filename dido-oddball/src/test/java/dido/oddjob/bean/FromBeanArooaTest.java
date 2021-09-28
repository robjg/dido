package dido.oddjob.bean;

import dido.data.GenericData;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;

import java.util.function.Function;

import static org.hamcrest.Matchers.sameInstance;

class FromBeanArooaTest {

    public static class Fruit {

        private String type;

        private int quantity;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    @Test
    void testFromBean() {

        Fruit fruit1 = new Fruit();
        fruit1.setType("Apple");
        fruit1.setQuantity(5);

        Function<Object, GenericData<String>> transform = new FromBeanArooa(new BeanUtilsPropertyAccessor())
                .ofUnknown();

        GenericData<String> data1 = transform.apply(fruit1);

        MatcherAssert.assertThat(data1.getString("type"), Matchers.is("Apple"));
        MatcherAssert.assertThat(data1.getInt("quantity"), Matchers.is(5));

        Fruit fruit2 = new Fruit();
        fruit2.setType("Orange");
        fruit2.setQuantity(2);

        GenericData<String> data2 = transform.apply(fruit2);

        MatcherAssert.assertThat(data2.getString("type"), Matchers.is("Orange"));
        MatcherAssert.assertThat(data2.getInt("quantity"), Matchers.is(2));

        MatcherAssert.assertThat(data1.getSchema(), sameInstance(data2.getSchema()));
    }
}