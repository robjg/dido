package dido.operators.transform;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.data.useful.AbstractFieldGetter;
import dido.data.util.DataBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

class FieldTransformTest {

    @Test
    void simpleOp() {

        FieldTransform fieldTransform = incomingSchema -> {

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

            return new FieldTransform.Definition(schemaField, fieldGetter);
        };

        DidoData data = DataBuilder.newInstance()
                .withDouble("price", 50.5)
                .build();

        DidoTransform transform = OpTransformBuilder.with()
                .copy(true).forSchema(data.getSchema()).addOp(fieldTransform.asOpDef()).build();

        DidoData result = transform.apply(data);

        assertThat(result.getDoubleNamed("markup"), Matchers.closeTo(60.6, 0.01));
    }
}