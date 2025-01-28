package dido.how.util;

import java.lang.reflect.Type;

/**
 * Copied from Google... as only Json pulls in the dependency.
 */
public class Primitives {

    /**
     * Returns the corresponding wrapper type of {@code type} if it is a primitive
     * type; otherwise returns {@code type} itself. Idempotent.
     * <pre>
     *     wrap(int.class) == Integer.class
     *     wrap(Integer.class) == Integer.class
     *     wrap(String.class) == String.class
     * </pre>
     */
    public static Type wrap(Type type) {
        if (type == int.class) return Integer.class;
        if (type == float.class) return Float.class;
        if (type == byte.class) return  Byte.class;
        if (type == double.class) return Double.class;
        if (type == long.class) return Long.class;
        if (type == char.class) return Character.class;
        if (type == boolean.class) return Boolean.class;
        if (type == short.class) return Short.class;
        if (type == void.class) return Void.class;
        return type;
    }

    /**
     * Returns the corresponding primitive type of {@code type} if it is a
     * wrapper type; otherwise returns {@code type} itself. Idempotent.
     * <pre>
     *     unwrap(Integer.class) == int.class
     *     unwrap(int.class) == int.class
     *     unwrap(String.class) == String.class
     * </pre>
     */
    public static Type unwrap(Type type) {
        if (type == Integer.class) return int.class;
        if (type == Float.class) return float.class;
        if (type == Byte.class) return byte.class;
        if (type == Double.class) return double.class;
        if (type == Long.class) return long.class;
        if (type == Character.class) return char.class;
        if (type == Boolean.class) return boolean.class;
        if (type == Short.class) return short.class;
        if (type == Void.class) return void.class;
        return type;
    }

}
