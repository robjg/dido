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

    public static boolean isAssignableFrom(Type type, Class<?> from) {
        if (type instanceof Class) {
            return ((Class<?>) type).isAssignableFrom(from);
        }
        else {
            return false;
        }
    }
}
