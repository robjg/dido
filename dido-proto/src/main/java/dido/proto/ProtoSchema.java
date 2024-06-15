package dido.proto;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import dido.data.GenericDataSchema;

public class ProtoSchema {

    private String packageName;

    public Descriptors.Descriptor descriptorFromSchema(GenericDataSchema<?> schema) throws Descriptors.DescriptorValidationException {

        DescriptorProtos.FieldDescriptorProto idDEescriptor =
                DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("Id")
                        .setNumber(1)
                        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING)
                        .build();

        Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(
                DescriptorProtos.FileDescriptorProto.newBuilder()
                        .setPackage(packageName)
                        .addMessageType(DescriptorProtos.DescriptorProto.newBuilder()
                                .setName("")
                                .addField(idDEescriptor)
                        .build())
                .build(), new Descriptors.FileDescriptor[0]);

        Descriptors.Descriptor messageDescriptor = fileDescriptor.getMessageTypes().get(0);

        Descriptors.FieldDescriptor fieldDescriptor = messageDescriptor.getFields().get(0);

        DynamicMessage.Builder msgBuilder = DynamicMessage.newBuilder(messageDescriptor);

        msgBuilder.setField(fieldDescriptor, "Foo");

        DynamicMessage message = msgBuilder.build();

        message.getField(fieldDescriptor); // Foo

        return null;
    }


}
