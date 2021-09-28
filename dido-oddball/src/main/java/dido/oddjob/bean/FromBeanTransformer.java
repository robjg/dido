package dido.oddjob.bean;

import dido.data.GenericData;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;

public class FromBeanTransformer implements ArooaSessionAware, ArooaValue {

    private ArooaClass arooaClass;

    private ArooaSession session;

    public static class Conversions implements ConversionProvider {

        @Override
        public void registerWith(ConversionRegistry registry) {

            registry.register(FromBeanTransformer.class, Function.class,
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

    public <T> Function<T, GenericData<String>> toBeanTransformer() throws ClassNotFoundException {

        PropertyAccessor accessor = session.getTools().getPropertyAccessor();

        FromBeanArooa fromBean = new FromBeanArooa(accessor);

        if (arooaClass != null) {
            return fromBean.ofArooaClass(arooaClass);
        }

        return fromBean.ofUnknown();
    }

    public ArooaClass getArooaClass() {
        return arooaClass;
    }

    public void setArooaClass(ArooaClass arooaClass) {
        this.arooaClass = arooaClass;
    }

    @Override
    public String toString() {
        return "ToBeanTransformer{" +
                "arooaClass=" + arooaClass +
                '}';
    }
}
