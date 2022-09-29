package dido.oddjob.bean;

import dido.data.*;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;

import java.util.List;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
                .ofUnknownClass();

        GenericData<String> data1 = transform.apply(fruit1);

        assertThat(data1.getString("type"), is("Apple"));
        assertThat(data1.getInt("quantity"), is(5));

        Fruit fruit2 = new Fruit();
        fruit2.setType("Orange");
        fruit2.setQuantity(2);

        GenericData<String> data2 = transform.apply(fruit2);

        assertThat(data2.getString("type"), is("Orange"));
        assertThat(data2.getInt("quantity"), is(2));

        assertThat(data1.getSchema(), sameInstance(data2.getSchema()));
    }

    public static class OrderLine {

        private final String fruit;

        private final int qty;

        public OrderLine(String fruit, int qty) {
            this.fruit = fruit;
            this.qty = qty;
        }

        public String getFruit() {
            return fruit;
        }

        public int getQty() {
            return qty;
        }
    }

    public static class Order {

        private final String orderId;

        private final List<OrderLine> orderLines;

        public Order(String orderId, List<OrderLine> orderLines) {
            this.orderId = orderId;
            this.orderLines = orderLines;
        }

        public String getOrderId() {
            return orderId;
        }

        public List<OrderLine> getOrderLines() {
            return orderLines;
        }
    }

    @Test
    void whenNestedBeanPartialSchemaThenNestedData() {

        FromBeanArooa fromBeanArooa = FromBeanArooa.usingAccessor(new BeanUtilsPropertyAccessor());

        DataSchema<String> nestedSchema = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", Integer.class)
                .build();

        DataSchema<String> partialIn = SchemaBuilder.forStringFields()
                .addRepeatingField("orderLines", nestedSchema)
                .build();

        Function<Order, GenericData<String>> fromBean =
                fromBeanArooa.with()
                        .schema(partialIn).partial(true)
                        .ofClass(Order.class);

        GenericData<String> result = fromBean.apply(new Order("A123",
                List.of(new OrderLine("Apple", 5), new OrderLine("Pear", 4))));


        RepeatingData<String> repeatingData =
                RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 5),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 4));

        assertThat(result.getString("orderId"), is("A123"));
        assertThat(result.get("orderLines"), is(repeatingData));

    }

    @Test
    void whenNestedBeanFullSchemaThenNestedData() {

        FromBeanArooa fromBeanArooa = FromBeanArooa.usingAccessor(new BeanUtilsPropertyAccessor());

        DataSchema<String> nestedSchema = SchemaBuilder.forStringFields()
                .addField("fruit", String.class)
                .addField("qty", Integer.class)
                .build();

        DataSchema<String> schema = SchemaBuilder.forStringFields()
                .addField("orderId", String.class)
                .addRepeatingField("orderLines", nestedSchema)
                .build();


        Function<Order, GenericData<String>> fromBean =
                fromBeanArooa.with()
                        .schema(schema)
                        .ofUnknownClass();

        GenericData<String> result = fromBean.apply(new Order("A123",
                List.of(new OrderLine("Apple", 5), new OrderLine("Pear", 4))));

        assertThat(result.getSchema(), is(schema));

        IndexedData<String> expectedData = ArrayData.valuesFor(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 5),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 4)));

        assertThat(result, is(expectedData));

    }
}