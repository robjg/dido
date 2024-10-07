package dido.data.generic;

import dido.data.DataSchema;
import dido.data.useful.SchemaDelegate;

import java.util.Collection;

public class GenericSchemaDelegate<F> extends SchemaDelegate implements GenericDataSchema<F> {

    private final GenericDataSchema<F> delegate;

    protected GenericSchemaDelegate(GenericDataSchema<F> delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public Class<F> getFieldType() {
        return delegate.getFieldType();
    }

    @Override
    public boolean hasField(F field) {
        return delegate.hasField(field);
    }

    @Override
    public F getFieldAt(int index) {
        return delegate.getFieldAt(index);
    }

    @Override
    public F getFieldNamed(String name) {
        return delegate.getFieldNamed(name);
    }

    @Override
    public GenericSchemaField<F> getSchemaFieldAt(int index) {
        return delegate.getSchemaFieldAt(index);
    }

    @Override
    public GenericSchemaField<F> getSchemaFieldNamed(String name) {
        return delegate.getSchemaFieldNamed(name);
    }

    @Override
    public GenericSchemaField<F> getSchemaFieldOf(F field) {
        return delegate.getSchemaFieldOf(field);
    }



    @Override
    public int getIndexOf(F field) {
        return delegate.getIndexOf(field);
    }

    @Override
    public String getFieldNameOf(F field) {
        return delegate.getFieldNameOf(field);
    }

    @Override
    public Class<?> getTypeOf(F field) {
        return delegate.getTypeOf(field);
    }

    @Override
    public DataSchema getSchemaOf(F field) {
        return delegate.getSchemaOf(field);
    }

    @Override
    public Collection<F> getFields() {
        return delegate.getFields();
    }

    @Override
    public Collection<GenericSchemaField<F>> getGenericSchemaFields() {
        return delegate.getGenericSchemaFields();
    }
}
