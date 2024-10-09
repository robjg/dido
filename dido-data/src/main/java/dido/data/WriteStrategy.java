package dido.data;

/**
 * Provide a performant strategy for writing data.
 */
public interface WriteStrategy {

    FieldSetter getFieldSetterAt(int index);

    FieldSetter getFieldSetterNamed(String name);

    static WriteStrategy fromSchema(DataSchema schema) {

        if (schema instanceof WriteStrategy) {
            return (WriteStrategy) schema;
        }
        else {
            return new WriteStrategy() {
                @Override
                public FieldSetter getFieldSetterAt(int index) {
                    if (schema.hasIndex(index)) {
                        return FieldSetter.at(index);
                    }
                    else {
                        throw new NoSuchFieldException(index, schema);
                    }
                }

                @Override
                public FieldSetter getFieldSetterNamed(String name) {
                    if (schema.hasNamed(name)) {
                        return FieldSetter.named(name);
                    }
                    else {
                        throw new NoSuchFieldException(name, schema);
                    }
                }
            };
        }

    }
}
