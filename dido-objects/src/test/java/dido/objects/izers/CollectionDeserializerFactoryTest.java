package dido.objects.izers;

import dido.data.DidoData;
import dido.data.RepeatingData;
import dido.how.conversion.TypeToken;
import dido.objects.DeserializationCache;
import dido.objects.DidoDataDeserializer;
import dido.objects.RepeatingDeserializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CollectionDeserializerFactoryTest {

    @Test
    void deserializer() {

        CollectionDeserializerFactory test = new CollectionDeserializerFactory();

        DeserializationCache deserializationCache = mock(DeserializationCache.class);
        when(deserializationCache.deserializerFor(Fruit.class))
                .thenReturn((DidoDataDeserializer<Fruit>) data -> new Fruit(data.getStringAt(1)));
        when(deserializationCache.deserializerFor(Object.class))
                .thenReturn((DidoDataDeserializer<String>) data -> data.getStringAt(1));


        RepeatingData repeatingData = RepeatingData.of(
                DidoData.of("Apple"),
                DidoData.of("Orange")
        );

        RepeatingDeserializer<List<Fruit>> deserializer1 = (RepeatingDeserializer<List<Fruit>>) test.create(
                new TypeToken<List<Fruit>>() {
                }.getType(), deserializationCache);

        List<Fruit> fruits1 = deserializer1.deserialize(repeatingData);

        assertThat(fruits1, contains(new Fruit("Apple"), new Fruit("Orange")));

        RepeatingDeserializer<List<Object>> deserializer2 = (RepeatingDeserializer<List<Object>>) test.create(
                new TypeToken<List<Object>>() {
                }.getType(), deserializationCache);

        List<Object> fruits2 = deserializer2.deserialize(repeatingData);

        assertThat(fruits2, contains("Apple", "Orange"));

        RepeatingDeserializer<List> deserializer3 = (RepeatingDeserializer<List>) test.create(
                new TypeToken<List>() {
                }.getType(), deserializationCache);

        List<Fruit> fruits3 = deserializer3.deserialize(repeatingData);

        assertThat(fruits3, contains("Apple", "Orange"));

        RepeatingDeserializer<Set<Fruit>> deserializer4 = (RepeatingDeserializer<Set<Fruit>>) test.create(
                new TypeToken<Set<Fruit>>() {
                }.getType(), deserializationCache);

        Set<Fruit> fruits4 = deserializer4.deserialize(repeatingData);

        assertThat(fruits4, containsInAnyOrder(new Fruit("Apple"), new Fruit("Orange")));

    }

    static class FruitDeserializer implements DidoDataDeserializer<Fruit> {
        @Override
        public Fruit deserialize(DidoData data) {
            return new Fruit(data.getStringAt(1));
        }
    }

    public static class Fruit {

        private final String fruit;

        public Fruit(String fruit) {
            this.fruit = fruit;
        }

        public String getFruit() {
            return fruit;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Fruit fruit1 = (Fruit) o;
            return Objects.equals(fruit, fruit1.fruit);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(fruit);
        }
    }
}