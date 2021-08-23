package dido.oddjob.bean;

import dido.data.DataSchema;
import dido.data.SchemaBuilder;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class ClassSchema {

    private final PropertyAccessor propertyAccessor;

    public ClassSchema(PropertyAccessor propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
    }

    public DataSchema<String> schemaForClass(Class<?> theClass) {

        return schemaForArooaClass(new SimpleArooaClass(theClass));
    }

    public DataSchema<String> schemaForArooaClass(ArooaClass arooaClass) {

        final BeanOverview overview =
                arooaClass.getBeanOverview(propertyAccessor);

        SchemaBuilder<String> schemaBuilder = SchemaBuilder.forStringFields();

        for (String property : overview.getProperties()) {

            if ("class".equals(property)) {
                continue;
            }

            if (!overview.hasReadableProperty(property)) {
                continue;
            }

            if (overview.isIndexed(property) || overview.isMapped(property)) {
                continue;
            }

            schemaBuilder.addField(property, overview.getPropertyType(property));
        }

        return schemaBuilder.build();
    }

}
