package dido.oddjob.transform;

import dido.data.DataSchema;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Copy a field from one position and/or field and/or type to another.
 */
public class ValueCopyFactory implements ValueFactory<TransformerFactory<String, String>>, ArooaSessionAware {

    private static final Logger logger = LoggerFactory.getLogger(ValueCopyFactory.class);

    /**
     * From field.
     */
    private String field;

    /**
     * From index.
     */
    private int index;

    /**
     * To field.
     */
    private String to;

    /**
     * To Index.
     */
    private int at;

    /**
     * The to type
     */
    private Class<?> type;

    /**
     * Function
     */
    private Function<Object, Object> function;

    private ArooaSession session;

    @Override
    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    @Override
    public TransformerFactory<String, String> toValue() {
        return new CopyTransformerFactory(this);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAt() {
        return at;
    }

    public void setAt(int at) {
        this.at = at;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Function<Object, Object> getFunction() {
        return function;
    }

    public void setFunction(Function<Object, Object> function) {
        this.function = function;
    }

    static class CopyTransformerFactory implements TransformerFactory<String, String> {

        private final String from;

        private final String to;

        private final int index;

        private final int at;

        private final Class<?> type;

        private final Function<Object, Object> function;

        private final ArooaConverter converter;

        CopyTransformerFactory(ValueCopyFactory from) {
            this.from = from.field;
            this.to = from.to;
            this.index = from.index;
            this.at = from.at;
            this.type = from.type;
            this.function = from.function;
            this.converter = from.session.getTools().getArooaConverter();

        }

        @Override
        public Transformer<String, String> create(int position,
                                                  DataSchema<String> fromSchema,
                                                  SchemaSetter<String> schemaSetter) {

            String from;
            int index;

            if (this.from == null) {
                if (this.index == 0) {
                    index = position;
                }
                else {
                    index = this.index;
                }
                from = fromSchema.getFieldAt(index);
            }
            else {
                from = this.from;
                // ignore index - should we warn?
                index = fromSchema.getIndex(from);
                if (index < 1) {
                    throw new IllegalArgumentException("No field [" + from + "]");
                }
            }

            int at;
            if (this.at == 0) {
                at = index;
            }
            else {
                at = this.at;
            }

            String to;
            if (this.to == null) {
                to = from;
            }
            else {
                to = this.to;
            }

            Function<Function<Object,Object>, Transformer<String, String>> transformerFn;

            if (to == null) {
                if (from == null) {
                    logger.info("Creating Copy from {} to {}", index, at);
                    transformerFn = (conversion) ->
                            (fromData, into) -> into.setAt(at, conversion.apply(fromData.getAt(index)));
                }
                else {
                    logger.info("Creating Copy from {} to {}", from, at);
                    transformerFn = (conversion) ->
                            (fromData, into) -> into.setAt(at, conversion.apply(fromData.get(from)));
                }
            }
            else {
                if (from == null) {
                    logger.info("Creating Copy from {} to {}", index, to);
                    transformerFn = (conversion) ->
                            (fromData, into) -> into.set(to, conversion.apply(fromData.getAt(index)));
                }
                else {
                    logger.info("Creating Copy from {} to {}", from, to);
                    transformerFn = (conversion) ->
                            (fromData, into) -> into.set(to, conversion.apply(fromData.get(from)));

                }
            }

            Class<?> toType;
            Function<Object, Object> conversion;
            if (this.type == null) {
                conversion = Function.identity();
                toType = fromSchema.getTypeAt(index);
            }
            else {
                toType = type;
                conversion = in -> {
                    try {
                        return converter.convert(in, toType);
                    } catch (ConversionFailedException | NoConversionAvailableException e) {
                        throw new IllegalArgumentException(e);
                    }
                };
            }

            if (function != null) {
                conversion = function;
            }

            schemaSetter.setFieldAt(at, to, toType);

            return transformerFn.apply(conversion);
        }
    }

}