package dido.oddjob.bean;

import dido.data.DidoData;
import dido.data.GenericDataSchema;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.function.Function;

public class ToBeanTransformer implements ArooaSessionAware, ArooaValue {

    private ArooaClass arooaClass;

    private Class<?> beanClass;

    private GenericDataSchema<String> schema;

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

    public <T> Function<DidoData, T> toBeanTransformer() throws ClassNotFoundException {

        PropertyAccessor accessor = session.getTools().getPropertyAccessor();

        ToBeanArooa toBean = new ToBeanArooa(accessor);

        if (this.schema != null) {
            return toBean.ofSchema(schema);
        }

        if (this.beanClass != null) {
            return toBean.ofClass((Class<T>) beanClass);
        }

        if (arooaClass != null) {
            return toBean.ofArooaClass(arooaClass);
        }

        return toBean.ofUnknown();
    }

    public ArooaClass getArooaClass() {
        return arooaClass;
    }

    public void setArooaClass(ArooaClass arooaClass) {
        this.arooaClass = arooaClass;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public GenericDataSchema<String> getSchema() {
        return schema;
    }

    public void setSchema(GenericDataSchema<String> schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "ToBeanTransformer{" +
                "arooaClass=" + arooaClass +
                ", schema=" + schema +
                '}';
    }
}
