package dido.oddjob.transform;

import dido.data.DataSchema;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;

/**
 * @oddjob.description Set the value for a field or index. Participates in an {@link Transform}. If
 * no field or index is specified the index is taken by the position in the transform.
 * @oddjob.example Set a value.
 * {@oddjob.xml.resource dido/oddjob/transform/DataSetExample.xml}
 */
public class ValueSetFactory implements ValueFactory<TransformerFactory>, ArooaSessionAware {

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
    private ArooaValue value;

    /**
     * @oddjob.description The type. A conversion will be attempted from the value to this type.
     * This type will also be used for a new schema field.
     * @oddjob.required No. Defaults to that from the existing schema.
     */
    private Class<?> type;

    private ArooaSession session;

    @Override
    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public TransformerFactory toValue() {
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

    public ArooaValue getValue() {
        return value;
    }

    public void setValue(ArooaValue value) {
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

        private final ArooaValue value;

        private final Class<?> type;

        private final ArooaConverter converter;

        CopyTransformerFactory(ValueSetFactory config) {
            this.from = config.field;
            this.index = config.index;
            this.value = config.value;
            this.type = config.type;
            this.converter = config.session.getTools().getArooaConverter();
        }

        @Override
        public Transformer create(int position,
                                  DataSchema fromSchema,
                                  SchemaSetter schemaSetter) {

            int at;

            if (this.index == 0) {
                if (this.from == null) {
                    at = position;
                } else {
                    at = fromSchema.getIndexNamed(this.from);
                }
            } else {
                at = this.index;
            }

            String to = from;
            if (to == null) {
                to = fromSchema.getFieldNameAt(at);
            }

            Class<?> toType = type;
            if (type == null) {
                toType = fromSchema.getTypeAt(at);
            }

            schemaSetter.setFieldAt(at, to, toType);

            final Object value;
            try {
                value = converter.convert(this.value, toType);
            } catch (NoConversionAvailableException | ConversionFailedException e) {
                throw new IllegalArgumentException("Conversion of " + this.value +
                        " to " + toType + " failed", e);
            }

            return (from, into) -> into.setAt(at, value);
        }
    }

}
