package dido.proto;

import com.google.protobuf.Descriptors;
import dido.data.AbstractDataSchema;
import dido.data.DataSchema;
import dido.data.SchemaField;

import java.util.List;

public class DescriptorSchema extends AbstractDataSchema<String> {





    public static DataSchema<String> from(Descriptors.Descriptor descriptor) {

        List<Descriptors.FieldDescriptor> fieldDescriptors = descriptor.getFields();

        for(Descriptors.FieldDescriptor fieldDescriptor : fieldDescriptors) {

        }

        return null;
    }


    @Override
    public SchemaField<String> getSchemaFieldAt(int index) {
        return null;
    }

    @Override
    public int getIndex(String field) {
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
