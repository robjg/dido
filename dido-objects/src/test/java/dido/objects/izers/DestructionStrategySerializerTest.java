package dido.objects.izers;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.objects.*;
import dido.objects.stratagy.BeanStrategies;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

class DestructionStrategySerializerTest {

    @Test
    void serialize() {

        SerializationCache serializationCache = mock(SerializationCache.class);

        DidoDataSerializer test = (DidoDataSerializer) DestructionStrategySerializer
                .from(BeanStrategies.from(Fruit.class))
                .create(Fruit.class, serializationCache);

        DidoData data = test.serialize(new Fruit("Apple", 5, 22.4));

        assertThat(data.getStringNamed("fruit"), is("Apple"));
        assertThat(data.getIntNamed("qty"), is(5));
        assertThat(data.getDoubleNamed("price"), is(22.4));

        DataSchema schema = data.getSchema();
        assertThat(schema.getTypeNamed("fruit"), is(String.class));
        assertThat(schema.getTypeNamed("qty"), is(int.class));
        assertThat(schema.getTypeNamed("price"), is(double.class));
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