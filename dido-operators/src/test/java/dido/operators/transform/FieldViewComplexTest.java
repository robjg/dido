package dido.operators.transform;

import dido.data.*;
import dido.data.useful.AbstractFieldGetter;

class FieldViewComplexTest {


    static DataSchema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.valuesWithSchema(schema)
            .of("Apple", 10, 23.5);

    /**
     * Demo of a more complicated field definition.
     */
    static class MarkupOperation implements FieldView {

        @Override
        public void define(ReadSchema incomingSchema, Definition definition) {

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

            definition.addField(markupField, markupGetter);
            definition.addField(amountField, amountGetter);
            definition.addField(totalField, totalGetter);
        }
    }

}
