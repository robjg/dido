package dido.data.useful;

import dido.data.DidoData;
import dido.data.FieldGetter;

import java.util.function.Function;

/**
 * A delegate {@link FieldGetter}s that defers to other data via
 * the provided function.
 */
public class FieldGetterDelegate implements FieldGetter {

    private final FieldGetter original;

    private final Function<? super DidoData, ? extends DidoData> to;

    public FieldGetterDelegate(FieldGetter original,
                               Function<? super DidoData, ? extends DidoData> to) {
        this.original = original;
        this.to = to;
    }

    @Override
    public Object get(DidoData data) {
        DidoData d = to.apply(data);
        return d == null ? null : original.get(d);
    }

    @Override
    public boolean has(DidoData data) {
        DidoData d = to.apply(data);
        return d != null && original.has(d);
    }

    @Override
    public boolean getBoolean(DidoData data) {
        return original.getBoolean(to.apply(data));
    }

    @Override
    public byte getByte(DidoData data) {
        return original.getByte(to.apply(data));
    }

    @Override
    public char getChar(DidoData data) {
        return original.getChar(to.apply(data));
    }

    @Override
    public short getShort(DidoData data) {
        return original.getShort(to.apply(data));
    }

    @Override
    public int getInt(DidoData data) {
        return original.getInt(to.apply(data));
    }

    @Override
    public long getLong(DidoData data) {
        return original.getLong(to.apply(data));
    }

    @Override
    public float getFloat(DidoData data) {
        return original.getFloat(to.apply(data));
    }

    @Override
    public double getDouble(DidoData data) {
        return original.getDouble(to.apply(data));
    }

    @Override
    public String getString(DidoData data) {
        DidoData d = to.apply(data);
        return d == null ? null : original.getString(d);
    }
}
