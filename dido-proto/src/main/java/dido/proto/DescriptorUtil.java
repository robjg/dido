package dido.proto;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class DescriptorUtil {

    public static Descriptors.Descriptor fromFile(Path file) throws IOException, Descriptors.DescriptorValidationException {

        DescriptorProtos.FileDescriptorProto fileDescriptorProto =
                DescriptorProtos.FileDescriptorProto.parseFrom(Files.newInputStream(file));

        Descriptors.FileDescriptor fileDescriptor =
                Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[0]);

        return fileDescriptor.getMessageTypes().get(0);
    }

    public static Descriptors.Descriptor from(InputStream inputStream) throws IOException, Descriptors.DescriptorValidationException {

        DescriptorProtos.FileDescriptorSet fileDescriptorSet =
                DescriptorProtos.FileDescriptorSet.parseFrom(
                        Objects.requireNonNull(inputStream));

        DescriptorProtos.FileDescriptorProto fileDescriptorProto =
                fileDescriptorSet.getFile(0);

        Descriptors.FileDescriptor fileDescriptor =
                Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[0]);

        return fileDescriptor.getMessageTypes().get(0);
    }
}
