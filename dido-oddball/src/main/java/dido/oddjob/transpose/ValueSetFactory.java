package dido.oddjob.transpose;

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
 * Copy a field from one position and/or field and/or type to another.
 */
public class ValueSetFactory implements ValueFactory<TransposerFactory<String, String>>, ArooaSessionAware {

    /**
     * From field.
     */
    private String field;

    /**
     * From index.
     */
    private int index;

    private ArooaValue value;

    private Class<?> type;

    private ArooaSession session;

    @Override
    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public TransposerFactory<String, String> toValue() {
        return new CopyTransposerFactory(this);
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

    static class CopyTransposerFactory implements TransposerFactory<String, String> {

        private final String from;

        private final int index;

        private final ArooaValue value;

        private final Class<?> type;

        private final ArooaConverter converter;

        CopyTransposerFactory(ValueSetFactory config) {
            this.from = config.field;
            this.index = config.index;
            this.value = config.value;
            this.type = config.type;
            this.converter = config.session.getTools().getArooaConverter();
        }

        @Override
        public Transposer<String, String> create(int position,
                                               DataSchema<String> fromSchema,
                                               SchemaSetter<String> schemaSetter) {

            int at;

            if (this.index == 0) {
                if (this.from == null) {
                    at = position;
                }
                else {
                    at = fromSchema.getIndex(this.from);
                }
            }
            else {
                at = this.index;
            }

            String to = from;
            if (to == null) {
                to = fromSchema.getFieldAt(at);
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
