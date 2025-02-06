package dido.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DidoConversionAdaptorFactory implements TypeAdapterFactory {

    private final DidoConversionProvider conversionProvider;

    private final Map<Type, Type> tos;

    protected DidoConversionAdaptorFactory(Settings settings) {
        this.conversionProvider = Objects.requireNonNullElseGet(
                settings.conversionProvider, DefaultConversionProvider::defaultInstance);
        this.tos = new HashMap<>(settings.tos);
    }

    public static class Settings {

        private DidoConversionProvider conversionProvider;

        private final Map<Type, Type> tos = new HashMap<>();

        public Settings conversionProvider(DidoConversionProvider conversionProvider) {
            this.conversionProvider = conversionProvider;
            return this;
        }

        /**
         * Conversions supported relative to the read side.
         */
        public Settings register(Type from, Type to) {
            tos.put(to, from);
            return this;
        }

        public DidoConversionAdaptorFactory make() {
            return new DidoConversionAdaptorFactory(this);
        }
    }

    public static Settings with() {
        return new Settings();
    }

    public boolean isEmpty() {
        return tos.isEmpty();
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Type from = tos.get(type.getType());

        if (from == null) {
            return null;
        }

        return infer(gson, TypeToken.get(from), type);
    }

    protected  <F, T> TypeAdapter<T> infer(Gson gson, TypeToken<F> original, TypeToken<T> type) {

        TypeAdapter<F> fromAdaptor = gson.getAdapter(original);

        Function<F, T> readConversion = conversionProvider.conversionFor(original.getType(), type.getType());
        Function<T, F> writeConversion = conversionProvider.conversionFor(type.getType(), original.getType());

        return new DidoAdaptor<>(original.getType(), type.getType(),
                fromAdaptor, readConversion, writeConversion);
    }


    static class DidoAdaptor<F, T> extends TypeAdapter<T> {

        private final Type from;

        private final Type to;

        private final TypeAdapter<F> typeAdapter;

        private final Function<F, T> readConversion;

        private final Function<T, F> writeConversion;

        DidoAdaptor(Type from,
                    Type to,
                    TypeAdapter<F> typeAdapter,
                    Function<F, T> readConversion,
                    Function<T, F> writeConversion) {
            this.from = from;
            this.to = to;
            this.typeAdapter = typeAdapter;
            this.readConversion = readConversion;
            this.writeConversion = writeConversion;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {

            F original = Objects.requireNonNull(writeConversion,
                    "No Dido Conversion to " + from + " from " + to)
                    .apply(value);

            typeAdapter.write(out, original);
        }

        @Override
        public T read(JsonReader in) throws IOException {

            F original = typeAdapter.read(in);

            return Objects.requireNonNull(readConversion,
                            "No Dido Conversion from " + from + " to " + to)
                    .apply(original);
        }
    }
}
