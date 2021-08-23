package dido.oddjob.bean;

import dido.data.GenericData;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.utils.ClassUtils;

import java.util.function.Function;

public class ToBeanTransformer implements ArooaSessionAware, ArooaValue {

    private String className;

    private ArooaSession session;

    public static class Conversions implements ConversionProvider {

        @Override
        public void registerWith(ConversionRegistry registry) {

            registry.register(ToBeanTransformer.class, Function.class,
                    from -> {
                        try {
                            return from.toBeanTransformer();
                        } catch (ClassNotFoundException e) {
                            throw new ArooaConversionException(e);
                        }
                    });
        }
    }

    @Override
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    public Function<GenericData<String>, Object> toBeanTransformer() throws ClassNotFoundException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }

        Class<?> theClass = ClassUtils.classFor(className, classLoader);

        return new ToBeanArooa<>(new SimpleArooaClass(theClass), session.getTools().getPropertyAccessor());
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
