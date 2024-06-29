package dido.oddjob.bean;

import dido.data.*;
import org.junit.jupiter.api.Test;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ToBeanTransformerTest {

    @Test
    void testInOddjob() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataToBeanExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<Fruit> results = lookup.lookup("capture.beans", List.class);

        assertThat(results.get(0).getType(), is("Apple"));
        assertThat(results.get(1).getType(), is("Orange"));
        assertThat(results.get(2).getType(), is("Pear"));
    }

    public static class SomeNestedData implements ValueFactory<List<DidoData>> {


        @Override
        public List<DidoData> toValue() {

            DataSchema nestedSchema = SchemaBuilder.newInstance()
                    .addNamed("fruit", String.class)
                    .addNamed("qty", int.class)
                    .build();

            DataSchema schema = SchemaBuilder.newInstance()
                    .addNamed("orderId", String.class)
                    .addRepeatingNamed("orderLines", nestedSchema)
                    .build();

            DidoData data = ArrayData.valuesFor(schema)
                    .of("A123",
                            RepeatingData.of(ArrayData.valuesFor(nestedSchema)
                                            .of("Apple", 5),
                                    ArrayData.valuesFor(nestedSchema)
                                            .of("Pear", 4)));

            return List.of(data);
        }
    }

    @Test
    void testNestedDataInOddjob() throws ArooaConversionException {

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(new File(Objects.requireNonNull(
                getClass().getResource("DataToNestedBeanExample.xml")).getFile()));

        oddjob.run();

        assertThat(oddjob.lastStateEvent().getState().isComplete(), is(true));

        OddjobLookup lookup = new OddjobLookup(oddjob);

        @SuppressWarnings("unchecked")
        List<Order> results = lookup.lookup("capture.beans", List.class);

        Order order = results.get(0);

        Order.OrderLine orderLine1 = new Order.OrderLine();
        orderLine1.setFruit("Apple");
        orderLine1.setQty(5);

        Order.OrderLine orderLine2 = new Order.OrderLine();
        orderLine2.setFruit("Pear");
        orderLine2.setQty(4);

        Order expected = new Order();
        expected.setOrderId("A123");
        expected.setOrderLines(List.of(orderLine1, orderLine2));

        assertThat(order, is(expected));

    }

}
