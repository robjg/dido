package dido.data.operators;

import dido.data.*;

import java.util.Objects;
import java.util.function.Function;

public class FieldOperations {

    static class Copy implements FieldOperation {

        private final Getter getter;

        private final Setter setter;

        Copy(Getter getter, Setter setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public void accept(DidoData data) {
            if (getter.has(data)) {
                setter.set(getter.get(data));
            } else {
                setter.clear();
            }
        }
    }

    public static FieldOperationDefinition copyAt(int index) {

        return (incomingSchema, operationManager) -> {

            Getter getter = incomingSchema.getDataGetterAt(index);

            SchemaField originalField = incomingSchema.getSchemaFieldAt(index);

            operationManager.addOperation(originalField,
                    dataFactory -> new Copy(getter, dataFactory.getSetterNamed(originalField.getName())));
        };

    }

    static class Compute implements FieldOperation {

        private final Setter setter;

        private final Function<? super DidoData, ?> func;

        Compute(Setter setter, Function<? super DidoData, ?> func) {
            this.setter = setter;
            this.func = func;
        }

        @Override
        public void accept(DidoData data) {
            Object now = func.apply(data);
            if (now == null) {
                setter.clear();
            } else {
                setter.set(now);
            }
        }
    }

    public static FieldOperationDefinition copyNamed(String from, String to) {

        Objects.requireNonNull(from, "From");
        Objects.requireNonNull(to, "To");

        return (incomingSchema, operationManager) -> {

            Getter getter = incomingSchema.getDataGetterNamed(from);

            operationManager.addOperation(incomingSchema.getSchemaFieldNamed(from)
                    .mapToFieldName(to),

                    dataFactory -> new Copy(getter, dataFactory.getSetterNamed(to)));
        };

    }

    public static <T> FieldOperationDefinition computeNamed(String to,
                                                            Class<T> type,
                                                            Function<? super DidoData, T> func) {

        return new FieldOperationDefinition() {
            @Override
            public void  define(DataSchema incomingSchema,
                                FieldOperationManager operationManager) {

                operationManager.addOperation(SchemaField.of(1, to, type),
                        new FieldOperationFactory() {

                    @Override
                    public FieldOperation create(DataFactory<?> dataFactory) {
                        return new Compute(dataFactory.getSetterNamed(to), func);
                    }
                });
            }
        };
    }

    public static FieldOperationDefinition removeNamed(String name) {

        return new FieldOperationDefinition() {
            @Override
            public void define(DataSchema incomingSchema, FieldOperationManager operationManager) {
                operationManager.removeField(incomingSchema.getSchemaFieldNamed(name));
            }
        };
    }
}
