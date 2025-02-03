package dido.oddjob.util;

import dido.data.util.TypeUtil;
import dido.how.conversion.DefaultConversionProvider;
import dido.how.conversion.DidoConversionProvider;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.registry.ServiceProvider;
import org.oddjob.arooa.registry.Services;
import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @oddjob.description Provides a {@link DidoConversionProvider} using Oddjob's conversions.
 */
public class DidoConverterJob implements Runnable, ServiceProvider, ArooaSessionAware {

    public static final String DIDO_CONVERSION_PROVIDER_SERVICE_NAME = "DidoConversionProvider";

    private volatile ArooaSession arooaSession;

    /**
     * @oddjob.property
     * @oddjob.description A Name for the service.
     * @oddjob.required No. Defaults to the simple class name.
     */
    private volatile String name;

    private volatile ConverterServices services;

    private final List<CustomConversion<?, ?>> conversions = new ArrayList<>();

    @ArooaHidden
    @Override
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    @Override
    public void run() {

        ArooaConverter arooaConverter = arooaSession.getTools().getArooaConverter();

        if (conversions.isEmpty()) {
            this.services = new ConverterServices(new ArooaDidoConversionProvider(arooaConverter));
        } else {
            DidoConversionProvider conversions = this.conversions.stream()
                    .collect(DefaultConversionProvider::with,
                            (builder, customConversion) ->
                                    builder.conversion(customConversion.from, customConversion.to,
                                            customConversion.conversion),
                            (builder1, builder2) -> {
                            }
                    )
                    .make();
            this.services = new ConverterServices(new PriorityConversionProvider(conversions,
                    new ArooaDidoConversionProvider(arooaConverter)));
        }
    }

    @HardReset
    @SoftReset
    public void reset() {

        this.services = null;
    }


    @Override
    public Services getServices() {
        return this.services;
    }

    public DidoConversionProvider getConversionProvider() {
        return Optional.ofNullable(this.services)
                .map(s -> s.didoConversionProvider)
                .orElse(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomConversion<?, ?> getConversions(int i) {
        return conversions.get(i);
    }

    public void setConversions(int i, CustomConversion<?, ?> conversion) {
        if (conversion == null) {
            conversions.remove(i);
        }
        else {
            conversions.add(i, conversion);
        }
    }

    static class ConverterServices implements Services {

        private final DidoConversionProvider didoConversionProvider;

        ConverterServices(DidoConversionProvider didoConversionProvider) {
            this.didoConversionProvider = didoConversionProvider;
        }

        @Override
        public String serviceNameFor(Class<?> theClass, String flavour) {
            if (theClass == DidoConversionProvider.class) {
                return DIDO_CONVERSION_PROVIDER_SERVICE_NAME;
            } else {
                return null;
            }
        }

        @Override
        public Object getService(String serviceName) throws IllegalArgumentException {

            if (DIDO_CONVERSION_PROVIDER_SERVICE_NAME.equals(serviceName)) {
                return didoConversionProvider;
            } else {
                throw new IllegalArgumentException("No Service " + serviceName);
            }
        }

        @Override
        public String toString() {
            return "ArooaDidoConversions";
        }
    }

    static class ArooaDidoConversionProvider implements DidoConversionProvider {

        private final ArooaConverter arooaConverter;

        ArooaDidoConversionProvider(ArooaConverter arooaConverter) {
            this.arooaConverter = arooaConverter;
        }

        @Override
        public <F, T> Function<F, T> conversionFor(Type from, Type to) {
            @SuppressWarnings("unchecked") ConversionPath<F, T> conversionPath =
                    (ConversionPath<F, T>) arooaConverter.findConversion(
                            TypeUtil.classOf(from), TypeUtil.classOf(to));
            if (conversionPath == null) {
                return null;
            }
            return f -> {
                try {
                    return conversionPath.convert(f, arooaConverter);
                } catch (ConversionFailedException e) {
                    throw new RuntimeException(e);
                }
            };
        }

    }

    static class PriorityConversionProvider implements DidoConversionProvider {

        private final DidoConversionProvider first;

        private final DidoConversionProvider second;

        PriorityConversionProvider(DidoConversionProvider first, DidoConversionProvider second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public <F, T> Function<F, T> conversionFor(Type from, Type to) {

            Function<F, T> first = this.first.conversionFor(from, to);
            if (first == null) {
                return second.conversionFor(from, to);
            } else {
                return first;
            }
        }

    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(this.name, () -> getClass().getSimpleName());
    }

    public static class CustomConversion<F, T> {

        private Class<F> from;

        private Class<T> to;

        private Function<? super F, ? extends T> conversion;

        public Class<F> getFrom() {
            return from;
        }

        public void setFrom(Class<F> from) {
            this.from = from;
        }

        public Class<T> getTo() {
            return to;
        }

        public void setTo(Class<T> to) {
            this.to = to;
        }

        public Function<? super F, ? extends T> getConversion() {
            return conversion;
        }

        public void setConversion(Function<? super F, ? extends T> conversion) {
            this.conversion = conversion;
        }
    }
}
