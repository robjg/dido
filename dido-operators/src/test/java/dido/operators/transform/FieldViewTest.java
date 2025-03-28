package dido.operators.transform;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.data.useful.AbstractFieldGetter;
import dido.data.util.DataBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

class FieldViewTest {

    @Test
    void simpleOp() {

        FieldView fieldView = (incomingSchema, definition) -> {

            FieldGetter incomingGetter = incomingSchema.getFieldGetterNamed("price");
            SchemaField schemaField = SchemaField.of(0, "markup", double.class);

            FieldGetter fieldGetter = new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return getDouble(data);
                }

                @Override
                public double getDouble(DidoData data) {
                    return incomingGetter.getDouble(data) * 1.2;
                }
            };

            definition.addField(schemaField, fieldGetter);
        };

        DidoData data = DataBuilder.newInstance()
                .withDouble("price", 50.5)
                .build();

        DidoTransform transform = OpTransformBuilder.with()
                .copy(true).forSchema(data.getSchema()).addOp(fieldView.asOpDef()).build();

        DidoData result = transform.apply(data);

        assertThat(result.getDoubleNamed("markup"), closeTo(60.6, 0.01));
    }
}