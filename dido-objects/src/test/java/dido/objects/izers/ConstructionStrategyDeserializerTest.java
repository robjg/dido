package dido.objects.izers;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.objects.DeserializationCache;
import dido.objects.DidoDataDeserializer;
import dido.objects.DidoDeserializerFactory;
import dido.objects.stratagy.BeanStrategies;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

class ConstructionStrategyDeserializerTest {

    @Test
    void deserialize() {

        DataSchema schema = DataSchema.builder()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .addNamed("price", double.class)
                .build();

        DidoDeserializerFactory test = ConstructionStrategyDeserializer
                .from(BeanStrategies.from(Fruit.class), schema);

        DidoData data = DidoData.valuesWithSchema(schema).of("Apple", 5, 35.7);

        DidoDataDeserializer<Fruit> deserializer = (DidoDataDeserializer<Fruit>)
                test.create(Fruit.class, mock(DeserializationCache.class));

        Fruit fruit = deserializer.deserialize(data);

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