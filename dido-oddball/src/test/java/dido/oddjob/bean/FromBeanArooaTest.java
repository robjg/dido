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

        Function<Object, DidoData> transform = new FromBeanArooa(new BeanUtilsPropertyAccessor())
                .ofUnknownClass();

        DidoData data1 = transform.apply(fruit1);

        assertThat(data1.getString("type"), is("Apple"));
        assertThat(data1.getInt("quantity"), is(5));

        Fruit fruit2 = new Fruit();
        fruit2.setType("Orange");
        fruit2.setQuantity(2);

        DidoData data2 = transform.apply(fruit2);

        assertThat(data2.getString("type"), is("Orange"));
        assertThat(data2.getInt("quantity"), is(2));

        assertThat(data1.getSchema(), sameInstance(data2.getSchema()));
    }

    @Test
    void whenNestedBeanPartialSchemaThenNestedData() {

        FromBeanArooa fromBeanArooa = FromBeanArooa.usingAccessor(new BeanUtilsPropertyAccessor());

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", Integer.class)
                .build();

        DataSchema partialIn = SchemaBuilder.newInstance()
                .addRepeatingField("orderLines", nestedSchema)
                .build();

        Function<Order, DidoData> fromBean =
                fromBeanArooa.with()
                        .schema(partialIn).partial(true)
                        .ofClass(Order.class);

        Order.OrderLine orderLine1 = new Order.OrderLine();
        orderLine1.setFruit("Apple");
                orderLine1.setQty(5);

        Order.OrderLine orderLine2 = new Order.OrderLine();
        orderLine2.setFruit("Pear");
        orderLine2.setQty(4);

        Order order = new Order();
        order.setOrderId("A123");
        order.setOrderLines(List.of(orderLine1, orderLine2));

        DidoData result = fromBean.apply(order);

        RepeatingData repeatingData =
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

        DataSchema nestedSchema = SchemaBuilder.newInstance()
                .addField("fruit", String.class)
                .addField("qty", Integer.class)
                .build();

        DataSchema schema = SchemaBuilder.newInstance()
                .addField("orderId", String.class)
                .addRepeatingField("orderLines", nestedSchema)
                .build();

        Function<Order, DidoData> fromBean =
                fromBeanArooa.with()
                        .schema(schema)
                        .ofUnknownClass();

        Order.OrderLine orderLine1 = new Order.OrderLine();
        orderLine1.setFruit("Apple");
        orderLine1.setQty(5);

        Order.OrderLine orderLine2 = new Order.OrderLine();
        orderLine2.setFruit("Pear");
        orderLine2.setQty(4);

        Order order = new Order();
        order.setOrderId("A123");
        order.setOrderLines(List.of(orderLine1, orderLine2));

        DidoData result = fromBean.apply(order);

        assertThat(result.getSchema(), is(schema));

        DidoData expectedData = ArrayData.valuesFor(schema)
                .of("A123",
                        RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                        .of("Apple", 5),
                                ArrayData.valuesFor(nestedSchema)
                                        .of("Pear", 4)));

        assertThat(result, is(expectedData));

    }
}