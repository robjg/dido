package dido.data.util;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.FieldSetter;
import dido.data.WritableData;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

/**
 * Utility methods for Field Operations.
 */
public class FieldOps {

    /**
     * Create a copy operation appropriate to the type.
     *
     * @param getter The getter.
     * @param setter The setter.
     * @param type  The type.
     *
     * @return The op.
     */
    public static BiConsumer<DidoData, WritableData> copyOpFor(FieldGetter getter,
                                                        FieldSetter setter,
                                                        Type type) {

        if (boolean.class == type) {
            return new BooleanCopy(getter, setter);
        } else if (byte.class == type) {
            return new ByteCopy(getter, setter);
        } else if (short.class == type) {
            return new ShortCopy(getter, setter);
        } else if (char.class == type) {
            return new CharCopy(getter, setter);
        } else if (int.class == type) {
            return new IntCopy(getter, setter);
        } else if (long.class == type) {
            return new LongCopy(getter, setter);
        } else if (float.class == type) {
            return new FloatCopy(getter, setter);
        } else if (double.class == type) {
            return new DoubleCopy(getter, setter);
        } else {
            return (didoData, writableData)
                    -> setter.set(writableData, getter.get(didoData));
        }
    }

    abstract static class FieldCopy implements BiConsumer<DidoData, WritableData> {

        protected final FieldGetter getter;
        protected final FieldSetter setter;

        protected FieldCopy(FieldGetter getter, FieldSetter setter) {
            this.getter = getter;
            this.setter = setter;
        }
    }

    static class ObjectCopy extends FieldCopy {

        ObjectCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {
            setter.set(to,  getter.get(from));
        }
    }

    static class BooleanCopy extends FieldCopy {

        BooleanCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setBoolean(to, getter.getBoolean(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

    static class ByteCopy extends FieldCopy {

        ByteCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setByte(to, getter.getByte(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

    static class ShortCopy extends FieldCopy {

        ShortCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setShort(to, getter.getShort(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

    static class CharCopy extends FieldCopy {

        CharCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setChar(to, getter.getChar(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

    static class IntCopy extends FieldCopy {

        IntCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setInt(to, getter.getInt(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

    static class LongCopy extends FieldCopy {

        LongCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setLong(to, getter.getLong(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

    static class FloatCopy extends FieldCopy {

        FloatCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setFloat(to, getter.getFloat(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

    static class DoubleCopy extends FieldCopy {

        DoubleCopy(FieldGetter getter, FieldSetter setter) {
            super(getter, setter);
        }

        @Override
        public void accept(DidoData from, WritableData to) {

            if (getter.has(from)) {
                setter.setDouble(to, getter.getDouble(from));
            }
            else {
                setter.clear(to);
            }
        }
    }

}
