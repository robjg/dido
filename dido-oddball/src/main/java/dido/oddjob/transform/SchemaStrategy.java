package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.DataSchemaFactory;
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
                                        List<SchemaField> fields,
                                        IntConsumer copyThis) {

            int index = 0;

            DataSchemaFactory schemaFactory = DataSchemaFactory.newInstance();

            for (SchemaField schemaField : fields) {

                if (index < schemaField.getIndex()) {
                    index = schemaField.getIndex();
                } else {
                    ++index;
                }

                schemaFactory.addSchemaField(schemaField.mapToIndex(index));
            }

            return schemaFactory.toSchema();
        }
    },

    /**
     * The schema is a combination of the existing schema and the new fields defined by
     * the transform.
     */
    MERGE {
        public DataSchema newSchemaFrom(DataSchema existingSchema,
                                        List<SchemaField> fields,
                                        IntConsumer copyThis) {

            SortedMap<Integer, SchemaField> fieldsByIndex = new TreeMap<>();

            Set<Integer> existingIndices = new HashSet<>(existingSchema.lastIndex());

            for (int index = existingSchema.firstIndex(); index > 0; index = existingSchema.nextIndex(index)) {

                fieldsByIndex.put(index, existingSchema.getSchemaFieldAt(index));
                existingIndices.add(index);
            }

            int lastIndex = existingSchema.lastIndex();
            List<SchemaField> newFields = new ArrayList<>();

            for (SchemaField field : fields) {

                int index = field.getIndex();
                if (index == 0) {

                    newFields.add(field);
                } else {
                    fieldsByIndex.put(index, field);
                    existingIndices.remove(index);
                    lastIndex = Math.max(lastIndex, index);
                }
            }

            for (SchemaField field : newFields) {

                ++lastIndex;
                fieldsByIndex.put(lastIndex, field.mapToIndex(lastIndex));
            }

            DataSchemaFactory schemaBuilder = DataSchemaFactory.newInstance();

            for (SchemaField schemaField : fieldsByIndex.values()) {

                schemaBuilder.addSchemaField(schemaField);
            }

            existingIndices.forEach(copyThis::accept);

            return schemaBuilder.toSchema();
        }
    };

    abstract public DataSchema newSchemaFrom(DataSchema existingSchema,
                                             List<SchemaField> fields,
                                             IntConsumer copyThis);

}
