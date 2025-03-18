package dido.objects;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.RepeatingData;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataOutObjectsTest {

    @Test
    void fromDidoData() {

        DataSchema schema = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DidoData data = DidoData.valuesWithSchema(schema).of("Apple", 5, 22.4);

        Fruit fruit = DataOutObjects.beanOf(Fruit.class)
                .fromDidoData(data);

        assertThat(fruit.getFruit(), is("Apple"));
        assertThat(fruit.getQty(), is(5));
        assertThat(fruit.getPrice(), is(22.4));
    }

    @Test
    void fromDidoDataRepeating() {

        DataSchema fruitSchema = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DataSchema basketSchema = DataSchema.builder()
                .addNamed("id", String.class)
                .addRepeatingNamed("fruits", fruitSchema)
                .build();

        DidoData data = DidoData.valuesWithSchema(basketSchema)
                .of("X", RepeatingData.of(
                        DidoData.valuesWithSchema(fruitSchema)
                                .many()
                                .of("Apple", 5, 22.4)
                                .of("Orange", 3, 37.4)
                                .toList()));

        Basket basket = DataOutObjects.beanOf(Fruit.class)
                .and()
                .beanOf( Basket.class)
                .fromDidoData(data);

        assertThat(basket.getId(), is("X"));

        Fruit[] fruits = basket.getFruits();

        assertThat(fruits[0].getFruit(), is("Apple"));
        assertThat(fruits[0].getQty(), is(5));
        assertThat(fruits[0].getPrice(), is(22.4));

        assertThat(fruits[1].getFruit(), is("Orange"));
        assertThat(fruits[1].getQty(), is(3));
        assertThat(fruits[1].getPrice(), is(37.4));
    }


    public static class Fruit {

        private String fruit;

        private int qty;

        private double price;

        public String getFruit() {
            return fruit;
        }

        public void setFruit(String fruit) {
            this.fruit = fruit;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    public static class Basket {

        private String id;

        private Fruit[] fruits;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Fruit[] getFruits() {
            return fruits;
        }

        public void setFruits(Fruit[] fruits) {
            this.fruits = fruits;
        }
    }
}
