package dido.data.operators;

import dido.data.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class FieldOperationsTest {

    static ArrayDataSchema schema = ArrayDataSchema.newBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.valuesFor(schema)
            .of("Apple", 10, 23.5);

    @Test
    void copy() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forTransformableSchema(schema)
                .addFieldOperation(FieldOperations.copyNamed("Fruit", "Type"))
                .addFieldOperation(FieldOperations.copyNamed("Price", "Price"))
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Type", String.class)
                .addNamed("Price", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesFor(expectedSchema)
                .of("Apple", 23.5);

        assertThat(result, is(expectedData));
    }

    /**
     * Demo of some a bad approach that works but isn't thread safe.
     */
    static class MarkupOperation implements FieldOperationDefinition {

        double markup;
        double markupAmount;

        @Override
        public void define(DataSchema incomingSchema, FieldOperationManager operationManager) {

            Getter priceGetter = schema.getDataGetterNamed("Price");

            SchemaField markupField = SchemaField.of(0, "Markup", double.class);
            SchemaField amountField = SchemaField.of(0, "MarkupAmount", double.class);
            SchemaField totalField = SchemaField.of(0, "FinalPrice", double.class);

            operationManager.addOperation(markupField, dataFactory -> {

                Setter setter = dataFactory.getSetterNamed("Markup");

                return data -> {
                    double price = priceGetter.getDouble(data);
                    if (price > 100.0) {
                        markup = 0.3;
                    } else {
                        markup = 0.5;
                    }
                    setter.setDouble(markup);
                };
            });

            operationManager.addOperation(amountField, dataFactory -> {

                Setter setter = dataFactory.getSetterNamed("MarkupAmount");

                return data -> {
                    double price = priceGetter.getDouble(data);
                    markupAmount = price * markup;
                    setter.setDouble(markupAmount);
                };
            });

            operationManager.addOperation(totalField, dataFactory -> {

                Setter setter = dataFactory.getSetterNamed("FinalPrice");

                return data -> {
                    double price = priceGetter.getDouble(data);
                    setter.setDouble(price + markupAmount);
                };
            });
        }
    }

    @Test
    void complicated() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder.forSchemaWithCopy(schema)
                .addFieldOperation(FieldOperations.removeNamed("Qty"))
                .addFieldOperation(new MarkupOperation())
                .build();

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .addNamed("Markup", double.class)
                .addNamed("MarkupAmount", double.class)
                .addNamed("FinalPrice", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesFor(expectedSchema)
                .of("Apple", 23.5, 0.5, 11.75, 35.25);

        assertThat(result, is(expectedData));
    }

    /**
     * Is it thread safe.
     */
    static class MarkupTransformationFactory<D extends DidoData> {

        public Transformation<D> define(DataSchema incomingSchema, WritableSchemaFactory<D> schemaFactory) {

            WritableSchema<D> outSchema = SchemaBuilder.builderFor(schemaFactory)
                    .addSchemaField(incomingSchema.getSchemaFieldNamed("Fruit").mapToIndex(1))
                    .addSchemaField(incomingSchema.getSchemaFieldNamed("Price").mapToIndex(2))
                    .addSchemaField(SchemaField.of(3, "Markup", double.class))
                    .addSchemaField(SchemaField.of(4, "MarkupAmount", double.class))
                    .addSchemaField(SchemaField.of(5, "FinalPrice", double.class))
                    .build();

            Getter fruitGetter = incomingSchema.getDataGetterNamed("Fruit");
            Getter priceGetter = incomingSchema.getDataGetterNamed("Price");

            DataFactory<D> dataFactory = outSchema.newDataFactory();

            Setter fruitSetter = dataFactory.getSetterNamed("Fruit");
            Setter priceSetter = dataFactory.getSetterNamed("Price");
            Setter markupSetter = dataFactory.getSetterNamed("Markup");
            Setter amountSetter = dataFactory.getSetterNamed("MarkupAmount");
            Setter finalSetter = dataFactory.getSetterNamed("FinalPrice");

            return new Transformation<>() {

                @Override
                public WritableSchema<D> getResultantSchema() {
                    return outSchema;
                }

                @Override
                public D apply(DidoData data) {

                    double markup;
                    double markupAmount;

                    double price = priceGetter.getDouble(data);
                    if (price > 100.0) {
                        markup = 0.3;
                    } else {
                        markup = 0.5;
                    }

                    markupAmount = price * markup;

                    fruitSetter.setString(fruitGetter.getString(data));
                    priceSetter.setDouble(price);
                    markupSetter.setDouble(markup);
                    amountSetter.setDouble(markupAmount);
                    finalSetter.setDouble(price + markupAmount);

                    return dataFactory.toData();
                }
            };
        }
    }

    @Test
    void complicatedWithTransformation() {

        Transformation<ArrayData> transformation = new MarkupTransformationFactory<ArrayData>()
                .define(schema, ArrayData.schemaFactory());

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .addNamed("Markup", double.class)
                .addNamed("MarkupAmount", double.class)
                .addNamed("FinalPrice", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesFor(expectedSchema)
                .of("Apple", 23.5, 0.5, 11.75, 35.25);

        assertThat(result, is(expectedData));
    }
}