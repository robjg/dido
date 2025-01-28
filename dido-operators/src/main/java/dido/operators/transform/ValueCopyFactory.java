package dido.operators.transform;

import dido.data.*;
import dido.data.util.TypeUtil;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Copy a field from one position and/or field and/or type to another.
 * <p>
 */
public class ValueCopyFactory implements Supplier<OpDef> {

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

    private DidoConversionProvider conversionProvider;

    @Inject
    public void setConversionProvider(DidoConversionProvider conversionProvider) {
        this.conversionProvider = conversionProvider;
    }

    @Override
    public OpDef get() {
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

    static class CopyTransformerFactory implements OpDef {

        private final String from;

        private final String to;

        private final int index;

        private final int at;

        private final Class<?> type;

        private final Function<Object, Object> function;

        private final DidoConversionProvider conversionProvider;

        CopyTransformerFactory(ValueCopyFactory from) {
            this.from = from.field;
            this.to = from.to;
            this.index = from.index;
            this.at = from.at;
            this.type = from.type;
            this.function = from.function;
            this.conversionProvider = Objects.requireNonNullElseGet(from.conversionProvider,
                    DefaultConversionProvider::defaultInstance);
        }

        @Override
        public Prepare prepare(DataSchema fromSchema,
                               SchemaSetter schemaSetter) {

            String from;
            int index;

            if (this.from == null) {
                if (this.index == 0) {
                    throw new IllegalArgumentException("Index or Field Name required.");
                } else {
                    index = this.index;
                }
                from = fromSchema.getFieldNameAt(index);
            } else {
                from = this.from;
                // ignore index - should we warn?
                index = fromSchema.getIndexNamed(from);
                if (index < 1) {
                    throw new IllegalArgumentException("No field [" + from + "]");
                }
            }

            String to;
            if (this.to == null) {
                to = from;
            } else {
                to = this.to;
            }

            Function<Function<Object, ?>, Prepare> transformerFn;
            ReadStrategy readStrategy = ReadStrategy.fromSchema(fromSchema);

            logger.info("Creating Copy from {} to {}", from, to);
            transformerFn = (conversion) ->
                    writableSchema -> {
                        FieldSetter setter = writableSchema.getFieldSetterNamed(to);
                        FieldGetter getter = readStrategy.getFieldGetterAt(index);
                        return (fromData, toData) -> setter.set(toData, conversion.apply(getter.get(fromData)));
                    };

            Class<?> fromType = TypeUtil.classOf(fromSchema.getTypeAt(index));
            Class<?> toType;
            if (this.type == null) {
                toType = TypeUtil.classOf(fromSchema.getTypeAt(index));
            } else {
                toType = type;
            }

            Function<Object, ?> function;
            if (this.function == null) {
                if (fromType == toType) {
                    function = Function.identity();
                }
                else {
                    //noinspection unchecked
                    function =  (Function<Object, ?>) conversionProvider.conversionFor(fromType, toType);
                }
            }
            else {
                function = this.function;
            }

            schemaSetter.addField(SchemaField.of(this.at, to, toType));

            return transformerFn.apply(function);
        }
    }

}
