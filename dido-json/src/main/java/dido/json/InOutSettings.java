package dido.json;

import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import dido.data.DataSchema;
import dido.how.conversion.DidoConversionProvider;

import java.lang.reflect.Type;
import java.util.function.Consumer;

/**
 * Common Settings shared between In and Out.
 *
 * @param <B> The type of Settings Builder
 */
abstract public class InOutSettings<B extends InOutSettings<B>> {

    JsonDidoFormat didoFormat;

    DataSchema schema;

    final GsonBuilder gsonBuilder = new GsonBuilder();

    final DidoConversionAdaptorFactory.Settings didoConversion =
            DidoConversionAdaptorFactory.with();

    public B gsonBuilder(Consumer<? super GsonBuilder> withBuilder) {
        withBuilder.accept(gsonBuilder);
        return self();
    }

    public B strictness(Strictness strictness) {
        gsonBuilder.setStrictness(strictness == null ? Strictness.LEGACY_STRICT : strictness);
        return self();
    }

    public B schema(DataSchema schema) {
        this.schema = schema;
        return self();
    }

    public B conversionProvider(DidoConversionProvider conversionProvider) {
        didoConversion.conversionProvider(conversionProvider);
        return self();
    }

    public abstract B didoConversion(Type from, Type to);

    void registerGsonBuilderDefaults() {

        DidoConversionAdaptorFactory didoConversionAdaptorFactory = didoConversion.make();
        if (!didoConversionAdaptorFactory.isEmpty()) {
            gsonBuilder.registerTypeAdapterFactory(didoConversionAdaptorFactory);
        }
    }

    abstract B self();

}
