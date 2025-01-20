package dido.proto;

import com.google.protobuf.Descriptors;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DescriptorUtilTest {

    @Test
    void descriptorLoadsAndIsAsExpected() throws Descriptors.DescriptorValidationException, IOException {

        Descriptors.Descriptor descriptor = DescriptorUtil.from(
                Files.newInputStream(Paths.get("src/test/proto/protobin/Descriptor.protobin")));

        List<Descriptors.FieldDescriptor> fieldDescriptors = descriptor.getFields();

        assertThat(fieldDescriptors.size(), is(3));

        Descriptors.FieldDescriptor descriptor1 = fieldDescriptors.get(0);
        assertThat(descriptor1.getIndex(), is(0));
        assertThat(descriptor1.getName(), is("name"));
        assertThat(descriptor1.getJavaType(), is(Descriptors.FieldDescriptor.JavaType.STRING));

        Descriptors.FieldDescriptor descriptor2 = fieldDescriptors.get(1);
        assertThat(descriptor2.getIndex(), is(1));
        assertThat(descriptor2.getName(), is("id"));
        assertThat(descriptor2.getJavaType(), is(Descriptors.FieldDescriptor.JavaType.INT));
    }

}