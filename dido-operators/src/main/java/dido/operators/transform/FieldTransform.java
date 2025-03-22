package dido.operators.transform;

import dido.data.*;

import java.util.function.BiConsumer;

/**
 * A single field transformation.
 */
@FunctionalInterface
public interface FieldTransform {

    /**
     * @return
     */
    Definition define(ReadSchema incomingSchema);

    class Definition {

        private final SchemaField schemaField;

        private final FieldGetter fieldGetter;

        public Definition(SchemaField schemaField, FieldGetter fieldGetter) {
            this.schemaField = schemaField;
            this.fieldGetter = fieldGetter;
        }

        public SchemaField schemaField() {
            return schemaField;
        }

        public FieldGetter fieldGetter() {
            return fieldGetter;
        }

        public BiConsumer<DidoData, WritableData> createCopy(FieldSetter fieldSetter) {

            return (incomingData, writableData) ->
                    fieldSetter.set(writableData, fieldGetter().get(incomingData));
        }
    }

    default OpDef asOpDef() {

        return (incomingSchema, schemaSetter) -> {
            Definition definition = define(incomingSchema);
            SchemaField schemaField = schemaSetter.addField(definition.schemaField());
            return (OpDef.Prepare) writeSchema -> {
                FieldSetter fieldSetter = writeSchema.getFieldSetterAt(schemaField.getIndex());
                return definition.createCopy(fieldSetter);
            };
        };
    }
}
