package dido.oddjob.util;

import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.DidoConverter;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.registry.ServiceProvider;
import org.oddjob.arooa.registry.Services;
import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.SoftReset;

import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

/**
 * @oddjob.description Provides a {@link DidoConverter} using Oddjob's conversions.
 */
public class DidoConverterJob implements Runnable, ServiceProvider, ArooaSessionAware {

    public static final String DIDO_CONVERTER_SERVICE_NAME = "DidoConverter";

    public static final String DIDO_CONVERSION_PROVIDER_SERVICE_NAME = "DidoConversionProvider";

    private volatile ArooaSession arooaSession;

    private volatile String name;

    private volatile ConverterServices services;

    @ArooaHidden
    @Override
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    @Override
    public void run() {

        ArooaConverter arooaConverter = arooaSession.getTools().getArooaConverter();

        this.services = new ConverterServices(new ArooaDidoConverter(arooaConverter),
                new ArooaDidoConversionProvider(arooaConverter));
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

    public DidoConverter getConverter() {
        return Optional.ofNullable(this.services)
                .map(s -> s.didoConverter)
                .orElse(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    static class ConverterServices implements Services {

        private final DidoConverter didoConverter;

        private final DidoConversionProvider didoConversionProvider;

        ConverterServices(DidoConverter didoConverter, DidoConversionProvider didoConversionProvider) {
            this.didoConverter = didoConverter;
            this.didoConversionProvider = didoConversionProvider;
        }

        @Override
        public String serviceNameFor(Class<?> theClass, String flavour) {
            if (theClass == DidoConversionProvider.class) {
                return DIDO_CONVERSION_PROVIDER_SERVICE_NAME;
            } else if (theClass == DidoConverter.class) {
                return DIDO_CONVERTER_SERVICE_NAME;
            } else {
                return null;
            }
        }

        @Override
        public Object getService(String serviceName) throws IllegalArgumentException {

            if (DIDO_CONVERSION_PROVIDER_SERVICE_NAME.equals(serviceName)) {
                return didoConversionProvider;
            }
            if (DIDO_CONVERTER_SERVICE_NAME.equals(serviceName)) {
                return didoConverter;
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
        public <F, T> Function<F, T> conversionFor(Class<F> from, Class<T> to) {
            ConversionPath<F, T> conversionPath = arooaConverter.findConversion(from, to);
            if (conversionPath == null) {
                throw new IllegalArgumentException("No Conversion from " + from + " to " + to);
            }
            return f -> {
                try {
                    return conversionPath.convert(f, arooaConverter);
                } catch (ConversionFailedException e) {
                    throw new RuntimeException(e);
                }
            };
        }

        @Override
        public <F> ToIntFunction<F> toIntFrom(Class<F> from) {
            if (String.class == from) {
                return string -> Integer.parseInt((String) string);
            } else {
                Function<F, Integer> otherwise = conversionFor(from, int.class);
                return otherwise::apply;
            }
        }

        @Override
        public <F> ToDoubleFunction<F> toDoubleFrom(Class<F> from) {
            if (String.class == from) {
                return string -> Double.parseDouble((String) string);
            } else {
                Function<F, Double> otherwise = conversionFor(from, double.class);
                return otherwise::apply;
            }
        }

        @Override
        public <F> ToLongFunction<F> toLongFrom(Class<F> from) {
            if (String.class == from) {
                return string -> Long.parseLong((String) string);
            } else {
                Function<F, Long> otherwise = conversionFor(from, long.class);
                return otherwise::apply;
            }
        }

        @Override
        public <T> IntFunction<T> fromIntTo(Class<T> to) {
            Function<Integer, T> conversion = conversionFor(Integer.class, to);
            return conversion::apply;
        }

        @Override
        public <T> DoubleFunction<T> fromDoubleTo(Class<T> to) {
            Function<Double, T> conversion = conversionFor(Double.class, to);
            return conversion::apply;
        }

        @Override
        public <T> LongFunction<T> fromLongTo(Class<T> to) {
            Function<Long, T> conversion = conversionFor(Long.class, to);
            return conversion::apply;
        }
    }

    static class ArooaDidoConverter implements DidoConverter {

        private final ArooaConverter arooaConverter;

        ArooaDidoConverter(ArooaConverter arooaConverter) {
            this.arooaConverter = arooaConverter;
        }


        @Override
        public <T> T convert(Object from, Class<T> to) {
            try {
                return arooaConverter.convert(from, to);
            } catch (NoConversionAvailableException | ConversionFailedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public <T> T convertFromString(String string, Class<T> to) {
            return convert(string, to);
        }

        @Override
        public String convertToString(Object from) {
            return convert(from, String.class);
        }
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(this.name, () -> getClass().getSimpleName());
    }
}
