package dido.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import dido.data.DidoData;
import dido.data.useful.AbstractData;

public class DynamicMessageData extends AbstractData implements DidoData {

    private final DescriptorSchema schema;

    final DynamicMessage message;

    private DynamicMessageData(DescriptorSchema schema, DynamicMessage message) {
        this.schema = schema;
        this.message = message;
    }

    public static DidoData from(DescriptorSchema schema, DynamicMessage message) {
        return new DynamicMessageData(schema, message);
    }

    @Override
    public DescriptorSchema getSchema() {
        return schema;
    }

    @Override
    public Object getAt(int index) {
        Descriptors.FieldDescriptor fieldDescriptor = schema.getFieldDescriptorAt(index);
        return fieldDescriptor == null ? null : message.getField(fieldDescriptor);
    }

    @Override
    public Object getNamed(String name) {

        Descriptors.FieldDescriptor fieldDescriptor = schema.getFieldDescriptorNamed(name);
        return fieldDescriptor == null ? null : message.getField(fieldDescriptor);
    }
}
