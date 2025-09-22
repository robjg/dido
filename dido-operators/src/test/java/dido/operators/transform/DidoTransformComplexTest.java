package dido.operators.transform;

import dido.data.*;
import dido.data.immutable.ArrayData;
import dido.data.immutable.ArrayDataDataFactoryProvider;
import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DidoTransformComplexTest {

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
    static class MarkupOperation implements FieldWrite {

        @Override
        public Prepare prepare(ReadSchema incomingSchema, SchemaSetter schemaSetter) {

            FieldGetter priceGetter = incomingSchema.getFieldGetterNamed("Price");

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

        DidoTransform didoTransform = WriteTransformBuilder.with()
                .reIndex(true)
                .existingFields(true)
                .dataFactoryProvider(new ArrayDataDataFactoryProvider())
                .forSchema(schema)
                .addFieldView(FieldViews.removeNamed("Qty"))
                .addFieldWrite(new MarkupOperation())
                .build();

        DidoData result = didoTransform.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .addNamed("Markup", double.class)
                .addNamed("MarkupAmount", double.class)
                .addNamed("FinalPrice", double.class)
                .build();

        assertThat(didoTransform.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 23.5, 0.5, 11.75, 35.25);

        assertThat(result, is(expectedData));
    }

    /**
     * Is it thread safe.
     */
    static class MarkupTransformationFactory {

        public DidoTransform define(DataSchema incomingSchema, DataFactoryProvider factoryProvider) {

            SchemaFactory schemaFactory = factoryProvider.getSchemaFactory();

            DataSchema outSchema = SchemaBuilder.builderFor(schemaFactory)
                    .addSchemaField(incomingSchema.getSchemaFieldNamed("Fruit").mapToIndex(1))
                    .addSchemaField(incomingSchema.getSchemaFieldNamed("Price").mapToIndex(2))
                    .addSchemaField(SchemaField.of(3, "Markup", double.class))
                    .addSchemaField(SchemaField.of(4, "MarkupAmount", double.class))
                    .addSchemaField(SchemaField.of(5, "FinalPrice", double.class))
                    .build();

            ReadStrategy readStrategy = ReadStrategy.fromSchema(incomingSchema);

            FieldGetter fruitGetter = readStrategy.getFieldGetterNamed("Fruit");
            FieldGetter priceGetter = readStrategy.getFieldGetterNamed("Price");

            DataFactory dataFactory = factoryProvider.factoryFor(outSchema);

            WriteStrategy writeStrategy = WriteStrategy.fromSchema(outSchema);

            FieldSetter fruitSetter = writeStrategy.getFieldSetterNamed("Fruit");
            FieldSetter priceSetter = writeStrategy.getFieldSetterNamed("Price");
            FieldSetter markupSetter = writeStrategy.getFieldSetterNamed("Markup");
            FieldSetter amountSetter = writeStrategy.getFieldSetterNamed("MarkupAmount");
            FieldSetter finalSetter = writeStrategy.getFieldSetterNamed("FinalPrice");

            return new DidoTransform() {

                @Override
                public DataSchema getResultantSchema() {
                    return outSchema;
                }

                @Override
                public DidoData apply(DidoData data) {

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

        DidoTransform didoTransform = new MarkupTransformationFactory()
                .define(schema, new ArrayDataDataFactoryProvider());

        DidoData result = didoTransform.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .addNamed("Fruit", String.class)
                .addNamed("Price", double.class)
                .addNamed("Markup", double.class)
                .addNamed("MarkupAmount", double.class)
                .addNamed("FinalPrice", double.class)
                .build();

        assertThat(didoTransform.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.withSchema(expectedSchema)
                .of("Apple", 23.5, 0.5, 11.75, 35.25);

        assertThat(result, is(expectedData));
    }
}
