package dido.operators.transform;

import dido.how.conversion.DidoConversionProvider;

import javax.inject.Inject;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @oddjob.description Copy a field from one position and/or field and/or type to another.
 * <p>
 *
 * @oddjob.example Copy from one field to another.
 * {@oddjob.xml.resource dido/operators/transform/DataCopyToDifferentNamesExample.xml}
 *
 * @oddjob.example Copy primitives.
 * {@oddjob.xml.resource dido/operators/transform/DataCopyToPrimitivesExample.xml}
 *
 * @oddjob.example Copy applying a function.
 * {@oddjob.xml.resource dido/operators/transform/DataCopyFunctionExample.xml}
 */
public class ValueCopyFactory implements Supplier<FieldView> {

    /**
     * @oddjob.description Copy from the field of this name.
     * @oddjob.required Either this or index.
     */
    private String field;

    /**
     * @oddjob.description Copy from the field of this index.
     * @oddjob.required Either this or field.
     */
    private int index;

    /**
     * @oddjob.description Copy to the field of this name.
     * @oddjob.required No. The field will be copied with the same name.
     */
    private String to;

    /**
     * @oddjob.description Copy to the field of this index.
     * @oddjob.required No. The field will be copied either to the provided to field or to the same index.
     */
    private int at;

    /**
     * @oddjob.description The to type of the new field.
     * @oddjob.required No. The new field will of they type of the existing field.
     */
    private Class<?> type;

    /**
     * @oddjob.description An optional Function that can be applied during the copy.
     * The function must return a value assignable to the given type, or the existing field
     * type.
     * @oddjob.required No.
     */
    private Function<Object, Object> function;

    /**
     * @oddjob.description A conversion provider to provide a conversion when a type is provided
     * but a function isn't. This will be injected by Oddjob if it can.
     * @oddjob.required No.
     */
    private DidoConversionProvider conversionProvider;

    @Inject
    public void setConversionProvider(DidoConversionProvider conversionProvider) {
        this.conversionProvider = conversionProvider;
    }

    @Override
    public FieldView get() {
        if (this.type ==null) {
            if (this.function == null) {
                return copyField(FieldViews.copy())
                        .view();
            }
            else {
                return copyField(FieldViews.map())
                        .func(this.function);
            }
        }
        else {
            if (this.function == null) {
                return copyField(FieldViews.copy())
                        .conversionProvider(conversionProvider)
                        .type(this.type)
                        .view();
            }
            else {
                return copyField(FieldViews.map())
                        .type(this.type)
                        .func(this.function);
            }
        }

    }

    <O>  O copyField(FieldViews.CopyField<O> copyField) {

        FieldViews.CopyTo<O> copyTo;
        if (this.field == null) {
            if (this.index == 0) {
                throw new IllegalArgumentException("Index or Field Name required.");
            } else {
                copyTo = copyField.index(this.index);
            }
        } else {
            copyTo = copyField.from(this.field);
        }

        if (this.to != null) {
            copyTo = copyTo.to(this.to);
        }
        if (this.at > 0) {
            copyTo = copyTo.at(this.at);
        }

        return copyTo.with();
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

    @Override
    public String toString() {
        return "ValueCopyFactory{" +
                "function=" + function +
                ", type=" + type +
                ", at=" + at +
                ", to='" + to + '\'' +
                ", index=" + index +
                ", field='" + field + '\'' +
                '}';
    }
}
