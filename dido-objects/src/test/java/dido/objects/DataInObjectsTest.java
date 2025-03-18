package dido.objects;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.RepeatingData;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataInObjectsTest {

    @Test
    void toDidoData() {

        DidoData data = DataInObjects.beanOf(Fruit.class)
                .toDidoData(new Fruit("Apple", 5, 22.4));

        assertThat(data.getStringNamed("fruit"), is("Apple"));
        assertThat(data.getIntNamed("qty"), is(5));
        assertThat(data.getDoubleNamed("price"), is(22.4));
    }

    @Test
    void withMapper() {

        Function<Object, DidoData> mapper = DataInObjects.beanOf(Fruit.class)
                .mapper();

        Fruit fruit = new Fruit("Apple", 5,22.4);

        DidoData data = mapper.apply(fruit);

        assertThat(data.getStringNamed("fruit"), is("Apple"));
        assertThat(data.getIntNamed("qty"), is(5));
        assertThat(data.getDoubleNamed("price"), is(22.4));
    }

    @Test
    void toDidoDataRepeating() {

        DataSchema fruitSchema = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DataSchema basketSchema = DataSchema.builder()
                .addNamed("id", String.class)
                .addRepeatingNamed("fruits", fruitSchema)
                .build();

        Function<Basket, DidoData> mapper = DataInObjects.beanOf(Fruit.class)
                .and()
                .beanOf(Basket.class)
                .schema(basketSchema)
                .mapper();

        Fruit fruit1 = new Fruit("Apple", 5, 22.4);

        Fruit fruit2 = new Fruit("Orange",3,37.4);

        Basket basket = new Basket("X", new Fruit[]{ fruit1, fruit2 });

        DidoData data = mapper.apply(basket);

        RepeatingData repeating = (RepeatingData) data.getNamed("fruits");

        DidoData row1 = repeating.get(0);

        assertThat(row1.getStringNamed("fruit"), is("Apple"));
        assertThat(row1.getIntNamed("qty"), is(5));
        assertThat(row1.getDoubleNamed("price"), is(22.4));

        DidoData row2 = repeating.get(1);

        assertThat(row2.getStringNamed("fruit"), is("Orange"));
        assertThat(row2.getIntNamed("qty"), is(3));
        assertThat(row2.getDoubleNamed("price"), is(37.4));
    }

    public static class Fruit {

        private final String fruit;

        private final int qty;

        private final double price;

        public Fruit(String fruit, int qty, double price) {
            this.fruit = fruit;
            this.qty = qty;
            this.price = price;
        }

        public String getFruit() {
            return fruit;
        }

        public int getQty() {
            return qty;
        }

        public double getPrice() {
            return price;
        }
    }

    public static class Basket {

        private final String id;

        private final Fruit[] fruits;

        public Basket(String id, Fruit[] fruits) {
            this.id = id;
            this.fruits = fruits;
        }

        public String getId() {
            return id;
        }

        public Fruit[] getFruits() {
            return fruits;
        }

    }

}
