package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.SchemaFactory;
import dido.data.SchemaField;
import dido.data.schema.SchemaDefs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @oddjob.description Create a Schema from another schema by merging, concatenating or excluding fields.
 *
 * @oddjob.example Exclude fields.
 * {@oddjob.xml.resource dido/oddjob/schema/SchemaFromExclude.xml}
 *
 * @oddjob.example Merge another schema.
 * {@oddjob.xml.resource dido/oddjob/schema/SchemaFromMerge.xml}
 *
 * @oddjob.example Concatenate another schema.
 * {@oddjob.xml.resource dido/oddjob/schema/SchemaFromConcat.xml}
 */
public class SchemaFromBean implements NestedSchema {

    /**
     * @oddjob.description The name of the schema. This is so this schema may be
     * referenced elsewhere in the definition. If set then SchemaDefs must also be set, either directly
     * or because this is nested within another Schema.
     * @oddjob.required No.
     */
    private String name;

    /**
     * @oddjob.description The schema to start from.
     * @oddjob.required Yes.
     */
    private DataSchema from;

    /**
     * @oddjob.description The schemas to merge.
     * @oddjob.required No.
     */
    private final List<DataSchema> merge = new ArrayList<>();

    /**
     * @oddjob.description The schemas to concatenate.
     * @oddjob.required No.
     */
    private final List<DataSchema> concat = new ArrayList<>();

    /**
     * @oddjob.description The fields to exclude.
     * @oddjob.required No.
     */
    private String[] exclude;

    /**
     * @oddjob.description Reindex the fields to remove gaps.
     * @oddjob.required No.
     */
    private boolean reIndex;

    public static class Conversions implements ConversionProvider {

        @Override
        public void registerWith(ConversionRegistry registry) {
            registry.register(SchemaFromBean.class, DataSchema.class,
                    SchemaFromBean::toSchema);
        }
    }

    public DataSchema toSchema() throws ArooaConversionException {

        return toSchema(null);
    }

    @Override
    public DataSchema toSchema(SchemaDefs defs) throws ArooaConversionException {

        DataSchema from = Objects.requireNonNull(this.from, "No From Schema");

        SchemaFactory factory = SchemaFactory.newInstanceFrom(from);

        for (DataSchema schema : merge) {
            factory.merge(schema);
        }

        for (DataSchema schema : concat) {
            factory.concat(schema);
        }

        if (exclude != null) {
            for (String remove : exclude) {
                factory.removeNamed(remove);
            }
        }

        if (reIndex) {
            int index = 0;
            SchemaFactory reIndexed = SchemaFactory.newInstance();
            for (SchemaField field : factory.getSchemaFields()) {
                reIndexed.addSchemaField(field.mapToIndex(++index));
            }
            reIndexed.setSchemaDefs(defs);
            reIndexed.setSchemaName(name);
            return reIndexed.toSchema();
        }
        else {
            factory.setSchemaDefs(defs);
            factory.setSchemaName(name);
            return factory.toSchema();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataSchema getFrom() {
        return from;
    }

    public void setFrom(DataSchema from) {
        this.from = from;
    }

    public DataSchema getMerge(int index) {
        return merge.get(index);
    }

    public void setMerge(int index, DataSchema merge) {
        this.merge.add(index, merge);
    }

    public DataSchema getConcat(int index) {
        return concat.get(index);
    }

    public void setConcat(int index, DataSchema concat) {
        this.concat.add(index, concat);
    }

    public String[] getExclude() {
        return exclude;
    }

    public void setExclude(String[] exclude) {
        this.exclude = exclude;
    }

    public boolean isReIndex() {
        return reIndex;
    }

    public void setReIndex(boolean reIndex) {
        this.reIndex = reIndex;
    }

    @Override
    public String toString() {
        return "SchemaFromBean{" +
                "name='" + name + '\'' +
                ", from=" + from +
                ", merge=" + merge +
                ", concat=" + concat +
                ", exclude=" + Arrays.toString(exclude) +
                ", reIndex=" + reIndex +
                '}';
    }
}
