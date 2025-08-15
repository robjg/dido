package dido.data.useful;

import dido.data.DataSchema;
import dido.data.SchemaField;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Used when adapting a schema to an {@link dido.data.ReadSchema}
 * or {@link dido.data.WriteSchema}.
 */
public class SchemaDelegate extends AbstractDataSchema implements DataSchema {

    private final DataSchema delegate;

    protected SchemaDelegate(DataSchema delegate) {
        this.delegate = delegate;
    }

    protected DataSchema getDelegate() {
        return delegate;
    }

    @Override
    public boolean hasNamed(String name) {
        return delegate.hasNamed(name);
    }

    @Override
    public SchemaField getSchemaFieldAt(int index) {
        return delegate.getSchemaFieldAt(index);
    }

    @Override
    public String getFieldNameAt(int index) {
        return delegate.getFieldNameAt(index);
    }

    @Override
    public DataSchema getSchemaAt(int index) {
        return delegate.getSchemaAt(index);
    }

    @Override
    public SchemaField getSchemaFieldNamed(String name) {
        return delegate.getSchemaFieldNamed(name);
    }

    @Override
    public int getIndexNamed(String name) {
        return delegate.getIndexNamed(name);
    }

    @Override
    public Type getTypeNamed(String name) {
        return delegate.getTypeNamed(name);
    }

    @Override
    public DataSchema getSchemaNamed(String name) {
        return delegate.getSchemaNamed(name);
    }

    @Override
    public Collection<String> getFieldNames() {
        return delegate.getFieldNames();
    }

    @Override
    public Collection<SchemaField> getSchemaFields() {
        return delegate.getSchemaFields();
    }

    @Override
    public boolean hasIndex(int index) {
        return delegate.hasIndex(index);
    }

    @Override
    public int firstIndex() {
        return delegate.firstIndex();
    }

    @Override
    public int nextIndex(int index) {
        return delegate.nextIndex(index);
    }

    @Override
    public int lastIndex() {
        return delegate.lastIndex();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public Type getTypeAt(int index) {
        return delegate.getTypeAt(index);
    }
}
