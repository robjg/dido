package dido.proto;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.MapData;
import dido.foo.Person;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DynamicMessageDataTest {

    @Test
    void fromProto() throws InvalidProtocolBufferException {

        DataSchema schema = DataSchema.newBuilder()
                .addNamed("name", String.class)
                .addNamed("id", int.class)
                .addNamed("email", String.class)
                .build();

        DidoData data = MapData.newBuilder(schema)
                .with("name", "Alice")
                .with("id", 234)
                .with("email", "alice@foo.com")
                .build();

        byte[] bytes = ToProtoBytes.from(Person.getDescriptor()).apply(data);

        DynamicMessage message = DynamicMessage.parseFrom(Person.getDescriptor(), bytes);

        DescriptorSchema descriptorSchema = DescriptorSchema.from(Person.getDescriptor());

        DidoData copy = DynamicMessageData.from(descriptorSchema, message);

        assertThat(copy.getSchema(), is(data.getSchema()));

        assertThat(copy, is(data));
    }
}