package dido.operators.transform;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.ReadSchema;
import dido.data.SchemaField;
import dido.data.useful.AbstractFieldGetter;
import dido.data.util.DataBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FieldViewTest {

    @Test
    void simpleOp() {

        SchemaField schemaField = SchemaField.of(0, "markup", double.class);

        FieldView fieldView = (incomingSchema, definition) -> {

            FieldGetter incomingGetter = incomingSchema.getFieldGetterNamed("price");

            FieldGetter fieldGetter = new AbstractFieldGetter.ForDouble() {
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

        List<SchemaField> schemaFields = new ArrayList<>();
        List<FieldGetter> fieldGetters = new ArrayList<>();

        fieldView.define(ReadSchema.from(data.getSchema()), new FieldView.Definition() {
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

        assertThat(schemaFields, contains(schemaField));
        assertThat(fieldGetters.size(), is(1));

        FieldGetter fieldGetter = fieldGetters.getFirst();

        double result = fieldGetter.getDouble(data);
        assertThat(result, closeTo(60.6, 0.01));
    }

    @Test
    void clearField() {

        FieldView fieldView = (incomingSchema, definition) -> {

            FieldGetter fieldGetter = new AbstractFieldGetter() {
                @Override
                public Object get(DidoData data) {
                    return null;
                }
            };

            definition.addField(incomingSchema.getSchemaFieldNamed("colour"), fieldGetter);
        };

        DidoData data = DataBuilder.newInstance()
                .withString("colour", "red")
                .build();

        DidoTransform transform = WriteTransformBuilder.with()
                .existingFields(true).forSchema(data.getSchema()).addFieldWrite(fieldView.asFieldWrite()).build();

        DidoData result = transform.apply(data);

        assertThat(result.hasNamed("colour"), is(false));
        assertThat(result.getNamed("colour"), nullValue());
    }

}