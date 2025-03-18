package dido.objects.stratagy;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FactoryConstructorStrategyTest {

    @Test
    void staticConstruction() {

        ConstructionStrategy<Fruit> constructionStrategy = FactoryConstructorStrategy.from(
                FruitFactory.class, "createFruit");

        ObjectConstructor<Fruit> constructor = constructionStrategy.getConstructorSupplier().get();

        List<String> props = constructionStrategy.getSetters()
                .stream().map(ValueSetter::getName).collect(Collectors.toList());

        assertThat(props, containsInAnyOrder("fruit", "qty", "price"));

        constructionStrategy.getSetter("fruit")
                .setValue(constructor, "Apple");
        constructionStrategy.getSetter("qty")
                .setValue(constructor, 5);
        constructionStrategy.getSetter("price")
                .setValue(constructor, 35.7);

        Fruit fruit = constructor.actualize();

        assertThat(fruit.getFruit(), is("Apple"));
        assertThat(fruit.getQty(), is(5));
        assertThat(fruit.getPrice(), is(35.7));
    }

    public static class FruitFactory {

        public static Fruit createFruit(String fruit, int qty, double price) {

            return new Fruit(fruit, qty, price);
        }
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
}