package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.GenericData;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.util.Collection;
import java.util.function.Function;

public class ToBeanArooa<T> implements Function<GenericData<String>, T> {

    private final ArooaClass arooaClass;

    private final BeanOverview beanOverview;

    private final PropertyAccessor accessor;

    public ToBeanArooa(ArooaClass arooaClass, PropertyAccessor accessor) {
        this.arooaClass = arooaClass;
        this.accessor = accessor;
        this.beanOverview = arooaClass.getBeanOverview(accessor);
    }

    @Override
    public T apply(GenericData<String> data) {

        T t = (T) arooaClass.newInstance();

        DataSchema<String> schema = data.getSchema();

        Collection<String> fields = schema.getFields();

        String[] propertyNames = beanOverview.getProperties();

        for (String propertyName : propertyNames) {

            if (!beanOverview.hasWriteableProperty(propertyName)) {
                continue;
            }

            if (!fields.contains(propertyName)) {
                continue;
            }

            int index = schema.getIndex(propertyName);
            if (!data.hasIndex(index)) {
                continue;
            }

            Class<?> type = schema.getTypeAt(index);
            Object value = data.getObject(propertyName, type);

            accessor.setSimpleProperty(t, propertyName, value);
        }

        return t;
    }
}
