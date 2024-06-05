package dido.oddjob.schema;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;

import java.util.Arrays;

/**
 * @oddjob.description Define the field of a Schema. See {@link SchemaBean} for examples.
 */
public class SchemaFieldBean implements ArooaValue, ArooaSessionAware {

    /**
     * @oddjob.description The name of the field.
     * @oddjob.required No.
     */
    private String name;

    /**
     * @oddjob.description The index of the field.
     * @oddjob.required No. The next available field will be used.
     */
    private int index;

    /**
     * @oddjob.description The type of the field.
     * @oddjob.required Yes, unless nested.
     */
    private String type;

    /**
     * @oddjob.description The nested schema.
     * @oddjob.required No.
     */
    private SchemaWrapper nested;

    /**
     * @oddjob.description Is the nested schema repeating.
     * @oddjob.required No, defaults to false.
     */
    private boolean repeating;

    private ArooaSession arooaSession;

    public static class Conversions implements ConversionProvider {
        @Override
        public void registerWith(ConversionRegistry registry) {
            registry.register(SchemaFieldBean.class, SchemaFieldDef.class,
                    SchemaFieldBean::toSchemaField);
        }
    }

    @Override
    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    public SchemaFieldDef toSchemaField() throws ArooaConversionException {

        Class<?> type;

        if (nested == null) {
            if (this.type == null) {
                type = String.class;
            }
            else {
                ClassResolver classResolver = this.arooaSession.getArooaDescriptor().getClassResolver();
                type = classResolver.findClass(this.type);
                if (type == null) {
                    throw new ArooaConversionException("Failed to find class " + this.type +
                            " from class loaders " + Arrays.toString(classResolver.getClassLoaders()));
                }
            }
        }
        else {
            type = null;
        }

        return new SchemaFieldDefImpl(name,
                index,
                type,
                nested,
                repeating);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public void setIndex(int index) {
        this.index = index;
    }


    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SchemaWrapper getNested() {
        return nested;
    }

    public void setNested(SchemaWrapper nested) {
        this.nested = nested;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    static class SchemaFieldDefImpl implements SchemaFieldDef {

        private final String name;

        private final int index;

        private final Class<?> type;

        private final SchemaWrapper nested;

        private final boolean repeating;

        SchemaFieldDefImpl(String name,
                           int index,
                           Class<?> type,
                           SchemaWrapper nested,
                           boolean repeating) {
            this.name = name;
            this.index = index;
            this.type = type;
            this.nested = nested;
            this.repeating = repeating;
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
        public boolean isRepeating() {
            return repeating;
        }

        @Override
        public String toString() {
            return "SchemaFieldImpl{" +
                    "name='" + name + '\'' +
                    ", index=" + index +
                    ", type=" + type +
                    ", nested=" + nested +
                    ", repeating=" + repeating +
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
