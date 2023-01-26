package dido.how.conversion;

public interface DidoConverter {

    <T> T convert(Object from, Class<T> to);

    <T> T convertFromString(String string, Class<T> to);

    String convertToString(Object from);

}
