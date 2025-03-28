package dido.operators.transform;

import dido.data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A single field transformation.
 */
@FunctionalInterface
public interface FieldView {

    /**
     *
     */
    void define(ReadSchema incomingSchema, Definition definition);

    interface Definition {

        void addField(SchemaField schemaField, FieldGetter fieldGetter);

        void removeField(SchemaField schemaField);
    }


    default OpDef asOpDef() {

        Map<String, FieldGetter> getters = new HashMap<>();
        return (incomingSchema, schemaSetter) -> {
            define(incomingSchema,
                    new Definition() {
                        @Override
                        public void addField(SchemaField schemaField, FieldGetter fieldGetter) {
                            SchemaField newField = schemaSetter.addField(schemaField);
                            getters.put(newField.getName(), fieldGetter);
                        }

                        @Override
                        public void removeField(SchemaField schemaField) {
                            schemaSetter.removeField(schemaField);
                        }
                    });
            return (OpDef.Prepare) writeSchema -> {
                List<BiConsumer<DidoData, WritableData>> copies = new ArrayList<>(getters.size());
                for (Map.Entry<String, FieldGetter> entry : getters.entrySet()) {
                    FieldSetter fieldSetter = writeSchema.getFieldSetterNamed(entry.getKey());
                    copies.add((didoData, writableData) -> {
                        fieldSetter.set(writableData, entry.getValue().get(didoData));
                    });
                }
                return ((didoData, writableData) -> {
                    for (BiConsumer<DidoData, WritableData> copy : copies) {
                        copy.accept(didoData, writableData);
                    }
                });
            };
        };
    }
}
