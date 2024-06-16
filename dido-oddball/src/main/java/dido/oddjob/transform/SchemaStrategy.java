package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import dido.data.SchemaField;

import java.util.*;
import java.util.function.IntConsumer;

/**
 * Provided to {@link Transform} to decide how to handle the {@link DataSchema} of the outgoing
 * type.
 */
public enum SchemaStrategy {

    /**
     * The schema is just created from the {@link Transformer}s used.
     */
    NEW {
        public DataSchema newSchemaFrom(DataSchema existingSchema,
                                        List<SchemaFieldOptions> fields,
                                        IntConsumer copyThis) {

            int index = 0;

            SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

            for (SchemaFieldOptions fieldOptions : fields) {

                if (index < fieldOptions.getIndex()) {
                    index = fieldOptions.getIndex();
                } else {
                    ++index;
                }
                schemaBuilder.addSchemaField(
                        SchemaField.of(index, fieldOptions.getField(), fieldOptions.getType()));
            }

            return schemaBuilder.build();
        }
    },

    /**
     * The schema is a combination of the existing schema and the new fields defined by
     * the transform.
     */
    MERGE {
        public DataSchema newSchemaFrom(DataSchema existingSchema,
                                        List<SchemaFieldOptions> fields,
                                        IntConsumer copyThis) {

            SortedMap<Integer, SchemaField> fieldsByIndex = new TreeMap<>();

            Set<Integer> existingIndices = new HashSet<>(existingSchema.lastIndex());

            for (int index = existingSchema.firstIndex(); index > 0; index = existingSchema.nextIndex(index)) {

                fieldsByIndex.put(index, existingSchema.getSchemaFieldAt(index));
                existingIndices.add(index);
            }

            int lastIndex = existingSchema.lastIndex();
            List<SchemaFieldOptions> newFields = new ArrayList<>();

            for (SchemaFieldOptions fieldOptions : fields) {

                int index = fieldOptions.getIndex();
                if (index == 0) {

                    newFields.add(fieldOptions);
                } else {
                    fieldsByIndex.put(index, SchemaField.of(
                            index, fieldOptions.getField(), fieldOptions.getType()));
                    existingIndices.remove(index);
                    lastIndex = Math.max(lastIndex, index);
                }
            }

            for (SchemaFieldOptions fieldOptions : newFields) {

                ++lastIndex;
                fieldsByIndex.put(lastIndex,
                        SchemaField.of(lastIndex, fieldOptions.getField(), fieldOptions.getType()));

            }

            SchemaBuilder schemaBuilder = SchemaBuilder.newInstance();

            for (SchemaField schemaField : fieldsByIndex.values()) {

                schemaBuilder.addSchemaField(schemaField);
            }

            existingIndices.forEach(copyThis::accept);

            return schemaBuilder.build();

        }
    };

    abstract public DataSchema newSchemaFrom(DataSchema existingSchema,
                                             List<SchemaFieldOptions> fields,
                                             IntConsumer copyThis);

}
