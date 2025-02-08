package dido.data.mutable;

abstract public class AbstractMalleableData extends AbstractMutableData implements MalleableData {

    @Override
    public void setAt(int index, Object value) {

        setAt(index, value, value == null ? void.class : value.getClass());
    }

    @Override
    public void setBooleanAt(int index, boolean value) {

        setAt(index, value, boolean.class);
    }

    @Override
    public void setByteAt(int index, byte value) {

        setAt(index, value, byte.class);
    }

    @Override
    public void setCharAt(int index, char value) {

        setAt(index, value, char.class);
    }

    @Override
    public void setShortAt(int index, short value) {

        setAt(index, value, short.class);
    }

    @Override
    public void setIntAt(int index, int value) {

        setAt(index, value, int.class);
    }

    @Override
    public void setLongAt(int index, long value) {

        setAt(index, value, long.class);
    }

    @Override
    public void setFloatAt(int index, float value) {

        setAt(index, value, float.class);
    }

    @Override
    public void setDoubleAt(int index, double value) {

        setAt(index, value, double.class);
    }

    @Override
    public void setStringAt(int index, String value) {

        setAt(index, value, String.class);
    }

    @Override
    public void setNamed(String name, Object value) {
        setNamed(name, value,
                value == null ? void.class : value.getClass());
    }

    @Override
    public void setBooleanNamed(String name, boolean value) {

        setNamed(name, value, boolean.class);
    }

    @Override
    public void setByteNamed(String name, byte value) {

        setNamed(name, value, byte.class);
    }

    @Override
    public void setCharNamed(String name, char value) {

        setNamed(name, value, char.class);
    }

    @Override
    public void setShortNamed(String name, short value) {

        setNamed(name, value, short.class);
    }

    @Override
    public void setIntNamed(String name, int value) {

        setNamed(name, value, int.class);
    }

    @Override
    public void setLongNamed(String name, long value) {

        setNamed(name, value, long.class);
    }

    @Override
    public void setFloatNamed(String name, float value) {

        setNamed(name, value, float.class);
    }

    @Override
    public void setDoubleNamed(String name, double value) {

        setNamed(name, value, double.class);
    }

    @Override
    public void setStringNamed(String name, String value) {

        setNamed(name, value, String.class);
    }

    @Override
    public void setNamedAt(int index, String name, Object value) {

        setNamedAt(index, name, value,
                value == null ? void.class : value.getClass());
    }

    @Override
    public void setBooleanNamedAt(int index, String name, boolean value) {

        setNamedAt(index, name, value, boolean.class);
    }

    @Override
    public void setByteNamedAt(int index, String name, byte value) {

        setNamedAt(index, name, value, byte.class);
    }

    @Override
    public void setCharNamedAt(int index, String name, char value) {

        setNamedAt(index, name, value, char.class);
    }

    @Override
    public void setShortNamedAt(int index, String name, short value) {

        setNamedAt(index, name, value, short.class);
    }

    @Override
    public void setIntNamedAt(int index, String name, int value) {

        setNamedAt(index, name, value, int.class);
    }

    @Override
    public void setLongNamedAt(int index, String name, long value) {

        setNamedAt(index, name, value, long.class);
    }

    @Override
    public void setFloatNamedAt(int index, String name, float value) {

        setNamedAt(index, name, value, float.class);
    }

    @Override
    public void setDoubleNamedAt(int index, String name, double value) {

        setNamedAt(index, name, value, double.class);
    }

    @Override
    public void setStringNamedAt(int index, String name, String value) {

        setNamedAt(index, name, value, String.class);
    }

}
