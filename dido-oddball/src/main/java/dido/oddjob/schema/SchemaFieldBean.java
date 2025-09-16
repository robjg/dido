package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.SchemaFactory;
import dido.data.SchemaField;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * @oddjob.description Define the field of a Schema. See {@link SchemaBean} for examples.
 */
public class SchemaFieldBean implements ArooaSessionAware, SchemaFactoryConsumer {

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
     * @oddjob.description A nested schema.
     * @oddjob.required No.
     */
    private DataSchema nested;

    /**
     * @oddjob.description A reference to a nested schema defined elsewhere.
     * @oddjob.required No.
     */
    private String ref;

    /**
     * @oddjob.description Is the nested schema repeating.
     * @oddjob.required No, defaults to false.
     */
    private boolean repeating;

    private ArooaSession arooaSession;


    @Override
    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }


    @Override
    public void acceptSchemaFactory(SchemaFactory schemaFactory) throws ArooaConversionException {

        if (ref != null) {
            schemaFactory.addSchemaReference(repeating ?
                    SchemaField.ofRepeatingRef(index, name, ref) :
                    SchemaField.ofRef(index, name, ref));
        } else if (nested != null) {
            schemaFactory.addSchemaField(repeating ?
                    SchemaField.ofRepeating(index, name, nested) :
                    SchemaField.ofNested(index, name, nested));
        } else {
            ClassResolver classResolver = this.arooaSession.getArooaDescriptor().getClassResolver();
            Type type = classResolver.findClass(Objects.requireNonNull(this.type, "No Type Specified."));
            if (type == null) {
                throw new ArooaConversionException("Failed to find class " + this.type +
                        " from class loaders " + Arrays.toString(classResolver.getClassLoaders()));
            }
            schemaFactory.addSchemaField(SchemaField.of(index, name, type));
        }
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

    public DataSchema getNested() {
        return nested;
    }

    public void setNested(DataSchema nested) {
        this.nested = nested;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "SchemaFieldBean{" +
                "name='" + name + '\'' +
                ", index=" + index +
                ", type=" + type +
                ", nested=" + nested +
                ", repeating=" + repeating +
                '}';
    }
}
