package dido.proto;

import com.google.protobuf.Descriptors;
import dido.data.AbstractDataSchema;
import dido.data.GenericDataSchema;
import dido.data.GenericSchemaField;

import java.util.List;

public class DescriptorSchema extends AbstractDataSchema {





    public static GenericDataSchema<String> from(Descriptors.Descriptor descriptor) {

        List<Descriptors.FieldDescriptor> fieldDescriptors = descriptor.getFields();

        for(Descriptors.FieldDescriptor fieldDescriptor : fieldDescriptors) {

        }

        return null;
    }


    @Override
    public GenericSchemaField<String> getSchemaFieldAt(int index) {
        return null;
    }

    @Override
    public int getIndexNamed(String field) {
        return 0;
    }

    @Override
    public int firstIndex() {
        return 0;
    }

    @Override
    public int nextIndex(int index) {
        return 0;
    }

    @Override
    public int lastIndex() {
        return 0;
    }
}
