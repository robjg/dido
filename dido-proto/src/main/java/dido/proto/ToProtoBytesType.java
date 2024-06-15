package dido.proto;

import com.google.protobuf.Descriptors;
import dido.data.IndexedData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
public class ToProtoBytesType implements Supplier<Function<IndexedData, byte[]>> {

    private Class<?> protoClass;


    @Override
    public Function<IndexedData, byte[]> get() {

        Method method;
        try {
            method = Objects.requireNonNull(protoClass, "No Class Provided")
                    .getMethod("getDescriptor");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Proto classes expected descriptor method.", e);
        }

        Descriptors.Descriptor descriptor;
        try {
            //noinspection unchecked
            descriptor = (Descriptors.Descriptor) method.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to invoke proto descriptor function.", e);
        }

        return ToProtoBytes.from(descriptor);
    }

    public Class<?> getProtoClass() {
        return protoClass;
    }

    public void setProtoClass(Class<?> protoClass) {
        this.protoClass = protoClass;
    }

}
