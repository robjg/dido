package dido.oddjob.transform;

import dido.data.GenericDataSchema;
import dido.data.GenericSchemaField;
import dido.data.SchemaBuilder;

import java.util.*;
import java.util.function.IntConsumer;

/**
 * Provided to {@link Transform} to decide how to handle the {@link GenericDataSchema} of the outgoing
 * type.
 */
public enum SchemaStrategy {

    /**
     * The schema is just created from the {@link Transformer}s used.
     */
    NEW {
        public <F> GenericDataSchema<F> newSchemaFrom(GenericDataSchema<F> existingSchema,
                                                      List<SchemaFieldOptions<F>> fields,
                                                      IntConsumer copyThis) {

            int index = 0;

            SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();

            for (SchemaFieldOptions<F> fieldOptions : fields) {

                if (index < fieldOptions.getIndex()) {
                    index = fieldOptions.getIndex();
                } else {
                    ++index;
                }
                schemaBuilder.addGenericSchemaField(
                        GenericSchemaField.of(index, fieldOptions.getField(), fieldOptions.getType()));
            }

            return schemaBuilder.build();
        }
    },

    /**
     * The schema is a combination of the existing schema and the new fields defined by
     * the transform.
     */
    MERGE {
        public <F> GenericDataSchema<F> newSchemaFrom(GenericDataSchema<F> existingSchema,
                                                      List<SchemaFieldOptions<F>> fields,
                                                      IntConsumer copyThis) {

            SortedMap<Integer, GenericSchemaField<F>> fieldsByIndex = new TreeMap<>();

            Set<Integer> existingIndices = new HashSet<>(existingSchema.lastIndex());

            for (int index = existingSchema.firstIndex(); index > 0; index = existingSchema.nextIndex(index)) {

                fieldsByIndex.put(index, existingSchema.getSchemaFieldAt(index));
                existingIndices.add(index);
            }

            int lastIndex = existingSchema.lastIndex();
            List<SchemaFieldOptions> newFields = new ArrayList<>();

            for (SchemaFieldOptions<F> fieldOptions : fields) {

                int index = fieldOptions.getIndex();
                if (index == 0) {

                    newFields.add(fieldOptions);
                }
                else {
                    fieldsByIndex.put(index, GenericSchemaField.of(
                            index, fieldOptions.getField(), fieldOptions.getType()));
                    existingIndices.remove(index);
                    lastIndex = Math.max(lastIndex, index);
                }
            }

            for (SchemaFieldOptions<F> fieldOptions : newFields) {

                ++lastIndex;
                fieldsByIndex.put(lastIndex,
                            GenericSchemaField.of(lastIndex, fieldOptions.getField(), fieldOptions.getType()));

            }

            SchemaBuilder<F> schemaBuilder = SchemaBuilder.impliedType();

            for (GenericSchemaField<F> schemaField : fieldsByIndex.values()) {

                schemaBuilder.addGenericSchemaField(schemaField);
            }

            existingIndices.forEach(i ->  copyThis.accept(i));

            return schemaBuilder.build();

        }
    }

    ;

    abstract public <F> GenericDataSchema<F> newSchemaFrom(GenericDataSchema<F> existingSchema,
                                                           List<SchemaFieldOptions<F>> fields,
                                                           IntConsumer copyThis);

}
