package dido.operators.transform;

import dido.data.DataSchema;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @oddjob.description Set the value for a field or index. Participates in an {@link TransformationFactory}. If
 * no field or index is specified the index is taken by the position in the transform.
 * @oddjob.example Set a value.
 * {@oddjob.xml.resource dido/oddjob/transform/DataSetExample.xml}
 */
public class ValueSetFactory implements Supplier<TransformerDefinition> {

    /**
     * @oddjob.description The field name.
     * @oddjob.required No
     */
    private String field;

    /**
     * @oddjob.description The value.
     * @oddjob.required No. If not specified null will be attempted to be set on the field.
     */
    private Object value;

    /**
     * @oddjob.description The type. A conversion will be attempted from the value to this type.
     * This type will also be used for a new schema field.
     * @oddjob.required No. Defaults to that from the existing schema.
     */
    private Class<?> type;

    /**
     * @oddjob.description A Conversion provider that will be used to convert the value to the type.
     * @oddjob.required No. Defaults to a simple one.
     */
    private DidoConversionProvider conversionProvider;

    @Inject
    public void setConversionProvider(DidoConversionProvider conversionProvider) {
        this.conversionProvider = conversionProvider;
    }

    @Override
    public TransformerDefinition get() {
        return new CopyTransformerFactory(this);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    static class CopyTransformerFactory implements TransformerDefinition {

        private final String field;

        private final Object value;

        private final Class<?> type;

        private final DidoConversionProvider conversionProvider;

        CopyTransformerFactory(ValueSetFactory config) {
            this.field = config.field;
            this.value = config.value;
            this.type = config.type;
            this.conversionProvider = Objects.requireNonNullElseGet(config.conversionProvider,
                    DefaultConversionProvider::defaultInstance);
        }

        @Override
        public TransformerFactory define(DataSchema fromSchema,
                                         SchemaSetter schemaSetter) {

            int index = fromSchema.getIndexNamed(
                    Objects.requireNonNull(field, "Field Name must be provided"));

            Class<?> toType;
            if (type == null) {
                if (index == 0) {
                    toType = Object.class;
                }
                else {
                    toType = fromSchema.getTypeAt(index);
                }
            }
            else {
                toType = type;
            }

            final Object value;
            if (this.value == null) {
                value = null;
            }
            else {
                value = inferredConversion(this.value, toType);
            }

            return FieldOperations.setNamed(field, value, toType).define(fromSchema, schemaSetter);
        }

        <F, T> T inferredConversion(F from, Class<T> toType) {
            //noinspection unchecked
            return conversionProvider.conversionFor((Class<F>) from.getClass(), toType)
                    .apply(from);
        }
    }

}
