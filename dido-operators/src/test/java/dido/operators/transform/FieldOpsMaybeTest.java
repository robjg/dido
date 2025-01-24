package dido.operators.transform;

import dido.data.*;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Should these be added to {@link FieldOps} ?
 */
public class FieldOpsMaybeTest {

    static DataSchema schema = ArrayData.schemaBuilder()
            .addNamed("Fruit", String.class)
            .addNamed("Qty", int.class)
            .addNamed("Price", double.class)
            .build();

    DidoData data = ArrayData.valuesWithSchema(schema)
            .of("Apple", 10, 23.5);


    public static <T> OpDef computeNamedGetter(String to,
                                               Function<? super ReadSchema,
                                                       Function<? super DidoData, ? extends T>> func,
                                               Class<T> type) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                field = SchemaField.of(0, to, type);
            } else {
                field = SchemaField.of(field.getIndex(), to, type);
            }

            SchemaField finalField = schemaSetter.addField(field);

            return dataFactory -> new FieldOps.Compute(dataFactory.getFieldSetterNamed(finalField.getName()),
                    func.apply(ReadSchema.from(incomingSchema)));
        };
    }

    // Whole Data Computes -  experimental.

    public static <T> OpDef computeFromDataNamed(String to,
                                                 Function<? super DidoData, ? extends T> func,
                                                 Class<T> type) {

        return (incomingSchema, schemaSetter) -> {

            SchemaField field = incomingSchema.getSchemaFieldNamed(to);
            if (field == null) {
                field = SchemaField.of(0, to, type);
            } else {
                field = SchemaField.of(field.getIndex(), to, type);
            }

            SchemaField finalField = schemaSetter.addField(field);

            return dataFactory -> new FieldOps.Compute(dataFactory.getFieldSetterNamed(finalField.getName()),
                    func);
        };
    }

    @Test
    void computeInPlaceWithGetter() {

        DidoTransform transformation = OpTransformBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(computeNamedGetter("Qty",
                        readSchema -> {
                            FieldGetter getter = readSchema.getFieldGetterNamed("Qty");
                            return data -> getter.getInt(data) * 2;
                        }, int.class))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.valuesWithSchema(schema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }

    @Test
    void computeInPlace() {

        DidoTransform transformation = OpTransformBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(computeFromDataNamed("Qty",
                        data -> data.getIntAt(2) * 2, int.class))
                .build();

        DidoData result = transformation.apply(data);

        assertThat(transformation.getResultantSchema(), is(schema));

        DidoData expectedData = ArrayData.valuesWithSchema(schema)
                .of("Apple", 20, 23.5);

        assertThat(result, is(expectedData));
    }


    @Test
    void computeNewField() {

        DidoTransform transformation = OpTransformBuilder.with()
                .copy(true)
                .forSchema(schema)
                .addOp(computeFromDataNamed("QtyDoubled",
                        data -> data.getIntAt(2) * 2, int.class))
                .build();

        DidoData result = transformation.apply(data);

        DataSchema expectedSchema = DataSchema.builder()
                .merge(schema)
                .addNamed("QtyDoubled", int.class)
                .build();

        assertThat(transformation.getResultantSchema(), is(expectedSchema));

        DidoData expectedData = ArrayData.valuesWithSchema(expectedSchema)
                .of("Apple", 10, 23.5, 20);

        assertThat(result, is(expectedData));
    }



}
