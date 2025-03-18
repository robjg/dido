package dido.objects.stratagy;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class BeanStrategiesTest {

    @Test
    void gettersWork() {

        DestructionStrategy test =  BeanStrategies.from(Fruit.class);

        Fruit fruit = new Fruit();
        fruit.setFruit("Apple");
        fruit.setPrice(35.7);
        fruit.setQty(5);

        assertThat(test.getGetter("fruit").getValue(fruit), is("Apple"));
        assertThat(test.getGetter("qty").getValue(fruit), is(5));
        assertThat(test.getGetter("price").getValue(fruit), is(35.7));
    }

    @Test
    void settersWork() {

        ConstructionStrategy<Fruit> test = BeanStrategies.from(Fruit.class);

        ObjectConstructor<Fruit> constructor = test.getConstructorSupplier().get();
        test.getSetter("fruit").setValue(constructor, "Apple");
        test.getSetter("qty").setValue(constructor, 5);
        test.getSetter("price").setValue(constructor, 35.7);

        Fruit fruit = constructor.actualize();

        assertThat(fruit.getFruit(), is("Apple"));
        assertThat(fruit.getQty(), is(5));
        assertThat(fruit.getPrice(), is(35.7));
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
}