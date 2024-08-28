package dido.oddjob.transform;

import dido.data.DataSchema;
import dido.data.SchemaField;
import dido.data.Setter;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @oddjob.description Set the value for a field or index. Participates in an {@link Transform}. If
 * no field or index is specified the index is taken by the position in the transform.
 * @oddjob.example Set a value.
 * {@oddjob.xml.resource dido/oddjob/transform/DataSetExample.xml}
 */
public class ValueSetFactory implements Supplier<TransformerFactory> {

    /**
     * @oddjob.description The field name.
     * @oddjob.required No
     */
    private String field;

    /**
     * @oddjob.description The index.
     * @oddjob.required No
     */
    private int index;

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

    private DidoConversionProvider conversionProvider;

    @ArooaHidden
    @Inject
    public void setConversionProvider(DidoConversionProvider conversionProvider) {
        this.conversionProvider = conversionProvider;
    }

    @Override
    public TransformerFactory get() {
        return new CopyTransformerFactory(this);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    static class CopyTransformerFactory implements TransformerFactory {

        private final String from;

        private final int index;

        private final Object value;

        private final Class<?> type;

        private final DidoConversionProvider conversionProvider;

        CopyTransformerFactory(ValueSetFactory config) {
            this.from = config.field;
            this.index = config.index;
            this.value = config.value;
            this.type = config.type;
            this.conversionProvider = Objects.requireNonNullElseGet(config.conversionProvider,
                    DefaultConversionProvider::defaultInstance);
        }

        @Override
        public Transformer create(DataSchema fromSchema,
                                  SchemaSetter schemaSetter) {

            int at;

            if (this.index == 0) {
                if (this.from == null) {
                    throw new IllegalArgumentException("Index or Field Name required.");
                } else {
                    at = fromSchema.getIndexNamed(this.from);
                }
            } else {
                at = this.index;
            }

            String to;
            if (this.from == null) {
                to = fromSchema.getFieldNameAt(at);
            } else {
                to = from;
            }

            Class<?> toType = type;
            if (type == null) {
                if (at != 0 && fromSchema.hasIndex(at)) {
                    toType = fromSchema.getTypeAt(at);
                }
                else {
                    toType = Object.class;
                }

            }

            schemaSetter.addField(SchemaField.of(at, to, toType));

            final Object value;
            if (this.value == null) {
                value = null;
            }
            else {
                value = inferredConversion(this.value, toType);
            }

            return into -> {
                Setter setter = into.getSetterAt(at);
                return from ->
                    setter.set(value);
            };
        }

        <F, T> T inferredConversion(F from, Class<T> toType) {
            //noinspection unchecked
            return conversionProvider.conversionFor((Class<F>) from.getClass(), toType)
                    .apply(from);
        }
    }

}
