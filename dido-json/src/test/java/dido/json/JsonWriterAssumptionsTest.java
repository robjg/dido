package dido.json;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JsonWriterAssumptionsTest {

    @Test
    void customSerialization() throws IOException {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Foo.class, new FooSerializer())
                .create();

        StringWriter writer = new StringWriter();

        JsonWriter jsonWriter = gson.newJsonWriter(writer);

        gson.toJson(new Foo("stuff"), Foo.class, jsonWriter);

        jsonWriter.close();

        assertThat(writer.toString(), is("\"Foo stuff\""));
    }

    static class FooSerializer implements JsonSerializer<Foo> {

        @Override
        public JsonElement serialize(Foo src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("Foo " + src.foo);
        }
    }

    record Foo(String foo) {

    }
}
