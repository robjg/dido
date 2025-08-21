package dido.operators.transform;

import dido.data.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Defines an alternative view of field data. Something that wishes to
 * use a field view must provide an {@link Definition} that will
 * capture the {@link SchemaField}s and {@link FieldGetter}s for the new field.
 * The captured {@code FieldGetter}s will yield a view of the provided original
 * data.
 *
 * @see FieldViews for some predifined field views.
 * @see ViewTransformBuilder to build an {@link DidoTransform} from {@code FieldView}s.
 */
@FunctionalInterface
public interface FieldView {

    /**
     * Define a view by calling methods on the provided {@link Definition}.
     *
     * @param incomingSchema The schema that will match the input data to the view.
     * @param viewDefinition The definition object that will be configured to
     *                       provide the view.
     */
    void define(ReadSchema incomingSchema, Definition viewDefinition);

    interface Definition {

        /**
         * Add a new field to the definition.
         *
         * @param schemaField The definition of the field.
         * @param fieldGetter A getter that will provide the view of the original data.
         */
        void addField(SchemaField schemaField, FieldGetter fieldGetter);

        /**
         * Remove a field from the definition. A view that started with the existing
         * fields from the incoming schema would use this to remove some of the
         * fields.
         *
         * @param schemaField The field to remove.
         */
        void removeField(SchemaField schemaField);
    }

    /**
     * Converts this to an {@link FieldWrite} so that it can be used
     * in an {@link WriteTransformBuilder}.
     *
     * @return An equivalent Field Write.
     */
    default FieldWrite asFieldWrite() {

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
            return (FieldWrite.Prepare) writeSchema -> {
                List<BiConsumer<DidoData, WritableData>> copies = new ArrayList<>(getters.size());
                for (Map.Entry<String, FieldGetter> entry : getters.entrySet()) {
                    FieldSetter fieldSetter = writeSchema.getFieldSetterNamed(entry.getKey());
                    copies.add(
                        copyOpFor( entry.getValue(), fieldSetter,
                                writeSchema.getTypeNamed(entry.getKey())));
                }
                return ((didoData, writableData) -> {
                    for (BiConsumer<DidoData, WritableData> copy : copies) {
                        copy.accept(didoData, writableData);
                    }
                });
            };
        };
    }

    /**
     * @param getter The getter.
     * @param setter The setter.
     * @param type  The type.
     * @return The op.
     */
    static BiConsumer<DidoData, WritableData> copyOpFor(FieldGetter getter,
                              FieldSetter setter,
                              Type type) {

        if (boolean.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setBoolean(writableData, getter.getBoolean(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else if (byte.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setByte(writableData, getter.getByte(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else if (short.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setShort(writableData, getter.getShort(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else if (char.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setChar(writableData, getter.getChar(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else if (int.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setInt(writableData, getter.getInt(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else if (long.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setLong(writableData, getter.getLong(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else if (float.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setFloat(writableData, getter.getFloat(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else if (double.class == type) {
            return (didoData, writableData) -> {
                if (getter.has(didoData)) {
                    setter.setDouble(writableData, getter.getDouble(didoData));
                } else {
                    setter.clear(writableData);
                }
            };
        } else {
            return (didoData, writableData)
                    -> setter.set(writableData, getter.get(didoData));
        }
    }

}
