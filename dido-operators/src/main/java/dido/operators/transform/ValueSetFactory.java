package dido.operators.transform;

import dido.how.conversion.DidoConversionProvider;

import javax.inject.Inject;
import java.util.function.Supplier;

/**
 * @oddjob.description Set the value for a field or index. Participates in an {@link TransformationFactory}. If
 * no field or index is specified the index is taken by the position in the transform.
 * @oddjob.example Set a value.
 * {@oddjob.xml.resource dido/operators/transform/DataSetExample.xml}
 */
public class ValueSetFactory implements Supplier<FieldView> {

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
    public FieldView get() {
        return FieldViews.set()
                .named(this.field)
                .at(this.at)
                .with()
                .value(value)
                .type(type)
                .conversionProvider(conversionProvider)
                .view();
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

    @Override
    public String toString() {
        return "ValueSetFactory{" +
                "field='" + field + '\'' +
                ", at=" + at +
                ", value=" + value +
                ", type=" + type +
                '}';
    }
}
