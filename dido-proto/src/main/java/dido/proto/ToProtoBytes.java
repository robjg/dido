package dido.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import dido.data.DataSchema;
import dido.data.IndexedData;
import dido.data.SchemaField;

import java.util.function.Function;

/**
 * Converts {@link IndexedData} into a Protobuf message as a byte array using the provided protobuf descriptor.
 */
public class ToProtoBytes implements Function<IndexedData, byte[]> {

    private final Descriptors.Descriptor descriptor;

    private ToProtoBytes(Descriptors.Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    public static Function<IndexedData, byte[]> from(Descriptors.Descriptor descriptor) {
        return new ToProtoBytes(descriptor);
    }

    @Override
    public byte[] apply(IndexedData data) {

        Message.Builder builder = DynamicMessage.newBuilder(descriptor);

        DataSchema schema = data.getSchema();

        for (SchemaField schemaField : schema.getSchemaFields()) {
            if (schemaField.getName() == null) {
                continue;
            }
            Descriptors.FieldDescriptor fieldDescriptor =
                    descriptor.findFieldByName(schemaField.getName().toString());
            if (fieldDescriptor == null) {
                 continue;
            }
            builder.setField(fieldDescriptor, data.getAt(schemaField.getIndex()));
        }

        return builder.build().toByteArray();
    }



}
