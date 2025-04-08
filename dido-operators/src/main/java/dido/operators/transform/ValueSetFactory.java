package dido.operators.transform;

import dido.data.ReadSchema;
import dido.how.conversion.DidoConversionProvider;

import javax.inject.Inject;
import java.util.function.Supplier;

/**
 * @oddjob.description Set the value for a field or index. Participates in an {@link TransformationFactory}. If
 * no field or index is specified the index is taken by the position in the transform.
 * @oddjob.example Set a value.
 * {@oddjob.xml.resource dido/operators/transform/DataSetExample.xml}
 */
public class ValueSetFactory implements Supplier<OpDef> {

    /**
     * @oddjob.description The field name.
     * @oddjob.required No
     */
    private String field;

    /**
     * @oddjob.description The index to set the value at.
     * @oddjob.required No
     */
    private int at;

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

    public int getAt() {
        return at;
    }

    public void setAt(int at) {
        this.at = at;
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

    public DidoConversionProvider getConversionProvider() {
        return conversionProvider;
    }

    @Inject
    public void setConversionProvider(DidoConversionProvider conversionProvider) {
        this.conversionProvider = conversionProvider;
    }

    static class CopyTransformerFactory implements OpDef {

        private final String field;

        private final int at;

        private final Object value;

        private final Class<?> type;

        private final DidoConversionProvider conversionProvider;

        CopyTransformerFactory(ValueSetFactory config) {
            this.field = config.field;
            this.at = config.at;
            this.value = config.value;
            this.type = config.type;
            this.conversionProvider = config.conversionProvider;
        }

        @Override
        public Prepare prepare(ReadSchema fromSchema,
                               SchemaSetter schemaSetter) {

            FieldView fieldView = FieldOps.set()
                    .named(this.field)
                    .at(this.at)
                    .with()
                    .value(value)
                    .type(type)
                    .conversionProvider(conversionProvider)
                    .view();

            return fieldView.asOpDef().prepare(fromSchema, schemaSetter);
        }
    }
}
