package dido.proto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provide a function that can be used to transform a
 * @param <T>
 */
public class ProtoToBeanType<T> implements Supplier<Function<byte[], T>> {

    private Class<T> protoClass;


    @Override
    public Function<byte[], T> get() {


        Method method;
        try {
            method = Objects.requireNonNull(protoClass, "No Class Provided")
                    .getMethod("parseFrom", byte[].class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Proto classes expect parseFrom method.", e);
        }


        return bytes -> {
            try {
                //noinspection unchecked
                return (T) method.invoke(null, new Object[] { bytes } );
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Failed to invoke proto parse function.", e);
            }
        };
    }

    public Class<T> getProtoClass() {
        return protoClass;
    }

    public void setProtoClass(Class<T> protoClass) {
        this.protoClass = protoClass;
    }

    @Override
    public String toString() {
        return "Proto to " + protoClass;
    }
}
