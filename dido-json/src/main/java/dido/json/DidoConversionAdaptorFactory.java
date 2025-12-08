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

/**
 * Provides a Type Adaptor between what can be parsed/serialised by Gson and what can be converted by Dido.
 */
public class DidoConversionAdaptorFactory implements TypeAdapterFactory {

    private final DidoConversionProvider conversionProvider;

    /** Conversion map from a Gson Parseable Type to a convertible with Dido type */
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
        public Settings register(Type gsonType, Type didoType) {
            if (Objects.requireNonNull(gsonType) == Objects.requireNonNull(didoType)) {
                throw new IllegalArgumentException("Can't register conversion of identical types:" + gsonType);
            }
            tos.put(didoType, gsonType);
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
    public <D> TypeAdapter<D> create(Gson gson, TypeToken<D> didoTypeToken) {
        Type didoType = tos.get(didoTypeToken.getType());

        if (didoType == null) {
            return null;
        }

        return infer(gson, TypeToken.get(didoType), didoTypeToken);
    }

    protected  <G, D> TypeAdapter<D> infer(Gson gson,
                                           TypeToken<G> gsonTypeToken,
                                           TypeToken<D> didoTypeToken) {

        TypeAdapter<G> fromAdaptor = gson.getAdapter(gsonTypeToken);

        if (fromAdaptor == null) {
            throw new IllegalArgumentException("No TypeAdaptor for " + gsonTypeToken +
                    ", can only provide a conversion on top of something Gson knows about.");
        }

        Function<G, D> readConversion = conversionProvider.conversionFor(gsonTypeToken.getType(), didoTypeToken.getType());
        Function<D, G> writeConversion = conversionProvider.conversionFor(didoTypeToken.getType(), gsonTypeToken.getType());

        return new DidoAdaptor<>(gsonTypeToken.getType(), didoTypeToken.getType(),
                fromAdaptor, readConversion, writeConversion);
    }


    static class DidoAdaptor<G, D> extends TypeAdapter<D> {

        private final Type gsonType;

        private final Type didoType;

        private final TypeAdapter<G> gsonTypeAdapter;

        private final Function<G, D> readConversion;

        private final Function<D, G> writeConversion;

        DidoAdaptor(Type gsonType,
                    Type didoType,
                    TypeAdapter<G> gsonTypeAdapter,
                    Function<G, D> readConversion,
                    Function<D, G> writeConversion) {
            this.gsonType = gsonType;
            this.didoType = didoType;
            this.gsonTypeAdapter = gsonTypeAdapter;
            this.readConversion = readConversion;
            this.writeConversion = writeConversion;
        }

        @Override
        public void write(JsonWriter out, D value) throws IOException {

            G original = Objects.requireNonNull(writeConversion,
                    "No Dido Conversion to " + gsonType + " from " + didoType)
                    .apply(value);

            gsonTypeAdapter.write(out, original);
        }

        @Override
        public D read(JsonReader in) throws IOException {

            G original = gsonTypeAdapter.read(in);

            return Objects.requireNonNull(readConversion,
                            "No Dido Conversion from " + gsonType + " to " + didoType)
                    .apply(original);
        }
    }
}
