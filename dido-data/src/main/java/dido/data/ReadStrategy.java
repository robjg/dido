package dido.data;

/**
 * Provide a performant strategy for reading data.
 */
public interface ReadStrategy {

    FieldGetter getFieldGetterAt(int index);

    FieldGetter getFieldGetterNamed(String name);

    static ReadStrategy fromSchema(DataSchema schema) {
        if (schema instanceof ReadStrategy) {
            return (ReadStrategy) schema;
        }
        else {
            return new ReadStrategy() {
                @Override
                public FieldGetter getFieldGetterAt(int index) {
                    if (schema.hasIndex(index)) {
                        return FieldGetter.at(index);
                    }
                    else {
                        throw new NoSuchFieldException(index, schema);
                    }
                }

                @Override
                public FieldGetter getFieldGetterNamed(String name) {
                    if (schema.hasNamed(name)) {
                        return FieldGetter.named(name);
                    }
                    else {
                        throw new NoSuchFieldException(name, schema);
                    }
                }
            };
        }
    }
}
