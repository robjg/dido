package dido.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import dido.data.DidoData;

import java.util.function.Function;

public class FromProtoBytes implements Function<byte[], DidoData> {

    private final Descriptors.Descriptor descriptor;

    public FromProtoBytes(Descriptors.Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public DidoData apply(byte[] bytes) {

        try {
            DynamicMessage dynamicMessage = DynamicMessage.parseFrom(descriptor, bytes);

        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }

        return null;
    }
}
