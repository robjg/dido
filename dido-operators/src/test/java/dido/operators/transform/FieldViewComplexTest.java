package dido.operators.transform;

import dido.data.*;
import dido.data.immutable.ArrayData;
import dido.data.immutable.NonBoxedDataFactoryProvider;
import dido.data.useful.AbstractFieldGetter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FieldViewComplexTest {


    static DataSchema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.withSchema(schema)
            .of("Apple", 10, 23.5);

    /**
     * Demo of a more complicated field definition.
     */
    static class MarkupOperation implements FieldView {

        @Override
        public void define(ReadSchema incomingSchema, Definition viewDefinition) {

            FieldGetter priceGetter = incomingSchema.getFieldGetterNamed("Price");

            SchemaField markupField = SchemaField.of(0, "Markup", double.class);
            SchemaField amountField = SchemaField.of(0, "MarkupAmount", double.class);
            SchemaField totalField = SchemaField.of(0, "FinalPrice", double.class);

            DataFactoryProvider dataFactoryProvider = new NonBoxedDataFactoryProvider();

            SchemaFactory schemaFactory = dataFactoryProvider.getSchemaFactory();

            schemaFactory.addSchemaField(markupField);
            schemaFactory.addSchemaField(amountField);
            schemaFactory.addSchemaField(totalField);


            FieldGetter markupGetter = new AbstractFieldGetter.ForDouble() {
                @Override
                public double getDouble(DidoData data) {
                    double price = priceGetter.getDouble(data);

                    if (price > 100.0) {
                        return 0.3;
                    } else {
                        return 0.5;
                    }
                }
            };

            FieldGetter amountGetter = new AbstractFieldGetter.ForDouble() {
                @Override
                public double getDouble(DidoData data) {
                    double price = priceGetter.getDouble(data);
                    double markup = markupGetter.getDouble(data);
                    return price * markup;
                }
            };

            FieldGetter totalGetter = new AbstractFieldGetter.ForDouble() {
                @Override
                public double getDouble(DidoData data) {
                    double amount = amountGetter.getDouble(data);
                    double price = priceGetter.getDouble(data);
                    return price + amount;
                }
            };

            viewDefinition.addField(markupField, markupGetter);
            viewDefinition.addField(amountField, amountGetter);
            viewDefinition.addField(totalField, totalGetter);
        }
    }

    @Test
    void complexOp() {

        List<SchemaField> schemaFields = new ArrayList<>();
        List<FieldGetter> fieldGetters = new ArrayList<>();

        new MarkupOperation().define(ReadSchema.from(data.getSchema()), new FieldView.Definition() {
            @Override
            public void addField(SchemaField schemaField, FieldGetter fieldGetter) {
                schemaFields.add(schemaField);
                fieldGetters.add(fieldGetter);
            }

            @Override
            public void removeField(SchemaField schemaField) {
                throw new UnsupportedOperationException();
            }
        });

        assertThat(schemaFields, contains(
                SchemaField.of(0, "Markup", double.class),
                SchemaField.of(0, "MarkupAmount", double.class),
                SchemaField.of(0, "FinalPrice", double.class)));

        assertThat(fieldGetters.size(), is(3));

        assertThat(fieldGetters.get(0).getDouble(data), closeTo(0.5, 0.01));
        assertThat(fieldGetters.get(1).getDouble(data), closeTo(11.75, 0.01));
        assertThat(fieldGetters.get(2).getDouble(data), closeTo(35.25, 0.01));
    }

}
