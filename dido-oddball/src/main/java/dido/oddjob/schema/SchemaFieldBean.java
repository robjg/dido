package dido.oddjob.schema;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.utils.ClassUtils;

import java.util.Optional;

public class SchemaFieldBean implements ArooaValue {

    private String name;

    private int index;

    private String type;

    private SchemaWrapper nested;

    public static class Conversions implements ConversionProvider {
        @Override
        public void registerWith(ConversionRegistry registry) {
            registry.register(SchemaFieldBean.class, SchemaFieldDef.class,
                    from -> from.toSchemaField());
        }
    }

    public SchemaFieldDef toSchemaField() throws ArooaConversionException {

        Class<?> type;
        if (this.type == null) {
            type = String.class;
        }
        else {
            ClassLoader classLoader = Optional.ofNullable(
                            Thread.currentThread().getContextClassLoader())
                    .orElseGet(() -> getClass().getClassLoader());
            try {
                type = ClassUtils.classFor(this.type, classLoader);
            } catch (ClassNotFoundException e) {
                throw new ArooaConversionException(e);
            }
        }

        return new SchemaFieldDefImpl(name,
                index,
                type,
                nested);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNested(SchemaWrapper nested) {
        this.nested = nested;
    }

    static class SchemaFieldDefImpl implements SchemaFieldDef {

        private final String name;

        private final int index;

        private final Class<?> type;

        private final SchemaWrapper nested;

        SchemaFieldDefImpl(String name, int index, Class<?> type, SchemaWrapper nested) {
            this.name = name;
            this.index = index;
            this.type = type;
            this.nested = nested;
        }

        @Override
        public String getFieldName() {
            return this.name;
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        @Override
        public Class<?> getType() {
            return this.type;
        }

        @Override
        public SchemaWrapper getNested() {
            return this.nested;
        }

        @Override
        public String toString() {
            return "SchemaFieldImpl{" +
                    "name='" + name + '\'' +
                    ", index=" + index +
                    ", type=" + type +
                    ", nested=" + nested +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SchemaFieldBean{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }
}
