package dido.operators;

import dido.data.*;
import dido.data.NoSuchFieldException;
import dido.data.schema.SubSchema;
import dido.data.useful.AbstractData;
import dido.data.useful.FieldGetterDelegate;
import dido.data.util.FieldSelectionFactory;
import dido.operators.transform.DidoTransform;

/**
 * Provide data that is a subset of some other data.
 */
public class SubData extends AbstractData implements DidoData {

    private final ReadSchema subSchema;

    private final DidoData original;

    private SubData(ReadSchema subSchema, DidoData original) {
        this.subSchema = subSchema;
        this.original = original;
    }

    public static FieldSelectionFactory<DidoData> of(DidoData data) {

        DataSchema schema = data.getSchema();
        return new FieldSelectionFactory<>(schema, ints -> {
            DidoTransform func = asMappingFrom(schema).withIndices(ints);
            return func.apply(data);
        });
    }

    public static FieldSelectionFactory<DidoTransform> asMappingFrom(DataSchema original) {

        return new FieldSelectionFactory<>(original, ints -> {
            DataSchema subSchema = SubSchema.from(original).withIndices(ints);
            ReadSchema readSchema = ReadSchema.from(subSchema,
                    new Read(subSchema, ReadSchema.from(original)));
            return new DidoTransform() {
                @Override
                public DataSchema getResultantSchema() {
                    return readSchema;
                }

                @Override
                public DidoData apply(DidoData didoData) {
                    return new SubData(readSchema, didoData);
                }
            };
        });
    }

    @Override
    public ReadSchema getSchema() {
        return subSchema;
    }

    @Override
    public Object getAt(int index) {
        return original.getAt(index);
    }

    @Override
    public boolean hasAt(int index) {
        return original.hasAt(index);
    }

    static class Read implements ReadStrategy {

        private final DataSchema subSchema;

        private final ReadSchema original;

        Read(DataSchema subSchema, ReadSchema original) {
            this.subSchema = subSchema;
            this.original = original;
        }

        @Override
        public FieldGetter getFieldGetterAt(int index) {
            if (subSchema.hasIndex(index)) {
                return new FieldGetterDelegate(
                        original.getFieldGetterAt(index),
                        data -> ((SubData) data).original);
            }
            else {
                throw new NoSuchFieldException(index, subSchema);
            }
        }

        @Override
        public FieldGetter getFieldGetterNamed(String name) {
            int index = subSchema.getIndexNamed(name);
            if (index == 0) {
                throw new NoSuchFieldException(name, subSchema);
            }
            return getFieldGetterAt(index);
        }
    }

}
