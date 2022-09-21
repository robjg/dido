package dido.proto;

import com.google.protobuf.Descriptors;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

class DescriptorUtilTest {

    @Test
    void test() throws Descriptors.DescriptorValidationException, IOException {

        InputStream inputStream = Objects.requireNonNull(
                getClass().getResourceAsStream("/Descriptor.protobin"));

        Path file = Paths.get("/src/test/proto/Person.proto").toAbsolutePath();

        Descriptors.Descriptor descriptor = DescriptorUtil.from(inputStream);


    }

}