package dido.how.conversion;

public interface DidoConverter {

    <T> T convert(Object thing, Class<T> type);

    <T> T convertFromString(String string, Class<T> type);

    String convertToString(Object thing);

}
