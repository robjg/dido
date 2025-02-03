package dido.data.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Very simple utilities for working with Types.
 */
public class TypeUtil {

    public static Class<?> classOf(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type);
        }
        else if (type instanceof ParameterizedType){
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        else {
            throw new IllegalArgumentException("No idea how to get class for " + type);
        }
    }

    public static boolean isPrimitive(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).isPrimitive();
        }
        else {
            return false;
        }
    }

    /**
     * A really rubbish basic check for assignment compatibility.
     * Number from Double is true, List from List&lt;String&gt; is true
     * but stuff like List&lt;?&gt; from List&lt;String&gt; is wrongly false.
     *
     * @param type The type to be assignable to.
     * @param from The type to test
     *
     * @return true if they are assignable after a few checks.
     */
    public static boolean isAssignableFrom(Type type, Type from) {
        if (type.equals(from)) {
            return true;
        }

        Class<?> fromClass;
        if (from instanceof Class) {
            fromClass = (Class<?>) from;
        }
        else if (from instanceof ParameterizedType) {
            fromClass = (Class<?>) ((ParameterizedType) from).getRawType();
        }
        else {
            return false;
        }

        if (type instanceof Class) {
            return ((Class<?>) type).isAssignableFrom(fromClass);
        }
        else {
            return false;
        }
    }
}
