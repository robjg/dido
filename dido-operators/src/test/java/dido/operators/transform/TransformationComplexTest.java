package dido.operators.transform;

import dido.data.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TransformationComplexTest {

    static ArrayData.Schema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.valuesForSchema(schema)
            .of("Apple", 10, 23.5);

    /**
     * Demo of a more complicated field definition.
     */
    static class MarkupOperation implements TransformerDefinition {

        @Override
        public TransformerFactory define(ReadSchema incomingSchema, SchemaSetter schemaSetter) {

            FieldGetter priceGetter = schema.getFieldGetterNamed("Price");

            SchemaField markupField = SchemaField.of(0, "Markup", double.class);
            SchemaField amountField = SchemaField.of(0, "MarkupAmount", double.class);
            SchemaField totalField = SchemaField.of(0, "FinalPrice", double.class);

            schemaSetter.addField(markupField);
            schemaSetter.addField(amountField);
            schemaSetter.addField(totalField);

            return writableSchema -> {

                FieldSetter markupSetter = writableSchema.getFieldSetterNamed("Markup");
                FieldSetter amountSetter = writableSchema.getFieldSetterNamed("MarkupAmount");
                FieldSetter finalSetter = writableSchema.getFieldSetterNamed("FinalPrice");

                return (data, out) -> {
                    double price = priceGetter.getDouble(data);

                    double markup;
                    if (price > 100.0) {
                        markup = 0.3;
                    } else {
                        markup = 0.5;
                    }
                    markupSetter.setDouble(out, markup);

                    double markupAmount = price * markup;
                    amountSetter.setDouble(out, markupAmount);

                    finalSetter.setDouble(out, price + markupAmount);
                };
            };
        }
    }

    @Test
    void complicated() {

        Transformation<ArrayData> transformation = FieldTransformationBuilder
                .withFactory(new ArrayDataDataFactoryProvider())
                .forSchemaWithCopy(schema)
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

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of("Apple", 23.5, 0.5, 11.75, 35.25);

        assertThat(result, is(expectedData));
    }

    /**
     * Is it thread safe.
     */
    static class MarkupTransformationFactory<D extends DidoData> {

        public Transformation<D> define(ReadSchema incomingSchema, DataFactoryProvider<D> factoryProvider) {

            SchemaFactory schemaFactory = factoryProvider.getSchemaFactory();

            WriteSchema outSchema = SchemaBuilder.builderFor(schemaFactory)
                    .addSchemaField(incomingSchema.getSchemaFieldNamed("Fruit").mapToIndex(1))
                    .addSchemaField(incomingSchema.getSchemaFieldNamed("Price").mapToIndex(2))
                    .addSchemaField(SchemaField.of(3, "Markup", double.class))
                    .addSchemaField(SchemaField.of(4, "MarkupAmount", double.class))
                    .addSchemaField(SchemaField.of(5, "FinalPrice", double.class))
                    .build();

            FieldGetter fruitGetter = incomingSchema.getFieldGetterNamed("Fruit");
            FieldGetter priceGetter = incomingSchema.getFieldGetterNamed("Price");

            DataFactory<D> dataFactory = factoryProvider.provideFactory(outSchema);

            FieldSetter fruitSetter = outSchema.getFieldSetterNamed("Fruit");
            FieldSetter priceSetter = outSchema.getFieldSetterNamed("Price");
            FieldSetter markupSetter = outSchema.getFieldSetterNamed("Markup");
            FieldSetter amountSetter = outSchema.getFieldSetterNamed("MarkupAmount");
            FieldSetter finalSetter = outSchema.getFieldSetterNamed("FinalPrice");

            return new Transformation<>() {

                @Override
                public WriteSchema getResultantSchema() {
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

                    WritableData out = dataFactory.getWritableData();

                    fruitSetter.setString(out, fruitGetter.getString(data));
                    priceSetter.setDouble(out, price);
                    markupSetter.setDouble(out, markup);
                    amountSetter.setDouble(out, markupAmount);
                    finalSetter.setDouble(out, price + markupAmount);

                    return dataFactory.toData();
                }
            };
        }
    }

    @Test
    void complicatedWithTransformation() {

        Transformation<ArrayData> transformation = new MarkupTransformationFactory<ArrayData>()
                .define(schema, new ArrayDataDataFactoryProvider());

        ArrayData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.newBuilder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .addNamed("Markup", double.class)
                .addNamed("MarkupAmount", double.class)
                .addNamed("FinalPrice", double.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesForSchema(expectedSchema)
                .of("Apple", 23.5, 0.5, 11.75, 35.25);

        assertThat(result, is(expectedData));
    }
}
