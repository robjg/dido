package dido.data.enums;

import dido.data.generic.GenericData;
import dido.data.generic.GenericDataBuilder;
import dido.data.schema.SchemaDefs;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

class EnumMapDataTest {

    public interface HasType {

        Class<?> getType();
    }

    enum Fields implements HasType {
        Fruit(String.class),
        Qty(int.class),
        Price(double.class);

        private final Class<?> type;

        Fields(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    @Test
    void testSchema() {

        EnumSchema<Fields> schema = EnumSchema.schemaFor(Fields.class, Fields::getType);

        assertThat(schema.firstIndex(), is(1));
        assertThat(schema.lastIndex(), is(3));

        assertThat(schema.getTypeAt(1), is(String.class));
        assertThat(schema.getTypeOf(Fields.Fruit), is(String.class));
        assertThat(schema.getTypeAt(2), is(int.class));
        assertThat(schema.getTypeOf(Fields.Qty), is(int.class));
        assertThat(schema.getTypeAt(3), is(double.class));
        assertThat(schema.getTypeOf(Fields.Price), is(double.class));

        assertThat(schema.getFieldAt(1), is(Fields.Fruit));
        assertThat(schema.getIndexOf(Fields.Fruit), is(1));
        assertThat(schema.getFieldAt(2), is(Fields.Qty));
        assertThat(schema.getIndexOf(Fields.Qty), is(2));
        assertThat(schema.getFieldAt(3), is(Fields.Price));
        assertThat(schema.getIndexOf(Fields.Price), is(3));

        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.getFields(), contains(Fields.Fruit, Fields.Qty, Fields.Price));
    }

    @Test
    void builderWithNoSchema() {

        GenericDataBuilder<Fields> builderNoSchema = EnumMapData.builderForEnum(Fields.class);

        GenericData<Fields> data3 = builderNoSchema
                .withString(Fields.Fruit, "Apple")
                .withInt(Fields.Qty, 2)
                .withDouble(Fields.Price, 75.3)
                .build();

        assertThat(data3.getSchema().getTypeOf(Fields.Qty), is(int.class));
        assertThat(data3.getSchema().getTypeAt(2), is(int.class));

        assertThat(data3.getSchema().getTypeOf(Fields.Price), is(double.class));
        assertThat(data3.getSchema().getTypeAt(3), is(double.class));
    }

    @Test
    void testWithHasType() {

        EnumSchema<Fields> schema = EnumSchema.schemaFor(Fields.class, Fields::getType);

        GenericDataBuilder<Fields> builder = EnumMapData.newBuilder(schema);

        GenericData<Fields> data1 = builder
                .with(Fields.Fruit, "Apple")
                .with(Fields.Qty, 2)
                .with(Fields.Price, 75.3)
                .build();

        GenericData<Fields> data2 = builder
                .with(Fields.Fruit, "Orange")
                .with(Fields.Qty, 3)
                .with(Fields.Price, 47.3)
                .build();

        assertThat(data1.hasAt(1), is(true));
        assertThat(data1.has(Fields.Fruit), is(true));

        assertThat(data1.getString(Fields.Fruit), is("Apple"));
        assertThat(data1.getStringAt(1), is("Apple"));
        assertThat(data1.getInt(Fields.Qty), is(2));
        assertThat(data1.getIntAt(2), is(2));
        assertThat(data1.getDouble(Fields.Price), is(75.3));
        assertThat(data1.getDoubleAt(3), is(75.3));

        assertThat(data2.getString(Fields.Fruit), is("Orange"));
        assertThat(data2.getStringAt(1), is("Orange"));

        GenericDataBuilder<Fields> builderNoSchema = EnumMapData.builderForEnum(Fields.class);

        GenericData<Fields> data3 = builderNoSchema
                .withString(Fields.Fruit, "Apple")
                .withInt(Fields.Qty, 2)
                .withDouble(Fields.Price, 75.3)
                .build();

        assertThat(data3.getSchema(), is(data1.getSchema()));

        assertThat(data3, is(data1));
        assertThat(data3.hashCode(), is(data1.hashCode()));

        assertThat(data1.toString(), is("{[Fruit]=Apple, [Qty]=2, [Price]=75.3}"));
    }

    enum Person {
        NAME,
        SEX,
        AGE
    }

    enum Family {
        MUM,
        DAD,
        CHILDREN
    }

    enum Sex {
        MALE,
        FEMALE
    }

    @Test
    void givenNestedSchemaThenFieldTypeWorksOut() {

        EnumSchema<Person> personSchema = EnumSchemaBuilder.forEnumType(Person.class)
                .addField(Person.NAME, String.class)
                .addField(Person.SEX, Sex.class)
                .addField(Person.AGE, int.class)
                .build();

        EnumSchema<Family> familySchema = EnumSchemaBuilder.forEnumType(Family.class)
                .addNestedField(Family.MUM, personSchema)
                .addNestedField(Family.DAD, personSchema)
                .addRepeatingField(Family.CHILDREN, personSchema)
                .build();

        GenericData<Family> family = EnumMapData.newBuilder(familySchema)
                .with(Family.MUM, EnumMapData.newBuilder(personSchema)
                        .with(Person.NAME, "Kate")
                        .with(Person.SEX, Sex.FEMALE)
                        .withInt(Person.AGE, 40)
                        .build())
                .with(Family.DAD, EnumMapData.newBuilder(personSchema)
                        .with(Person.NAME, "William")
                        .with(Person.SEX, Sex.MALE)
                        .withInt(Person.AGE, 39)
                        .build())
                .with(Family.CHILDREN, Arrays.asList(
                        EnumMapData.newBuilder(personSchema)
                                .with(Person.NAME, "George")
                                .with(Person.SEX, Sex.MALE)
                                .withInt(Person.AGE, 8)
                                .build(),
                        EnumMapData.newBuilder(personSchema)
                                .with(Person.NAME, "Charlotte")
                                .with(Person.SEX, Sex.FEMALE)
                                .withInt(Person.AGE, 7)
                                .build(),
                        EnumMapData.newBuilder(personSchema)
                                .with(Person.NAME, "Louis")
                                .with(Person.SEX, Sex.MALE)
                                .withInt(Person.AGE, 4)
                                .build()))
                .build();

        assertThat(family.toString(), is("{[MUM]={[NAME]=Kate, [SEX]=FEMALE, [AGE]=40}, " +
                "[DAD]={[NAME]=William, [SEX]=MALE, [AGE]=39}, " +
                "[CHILDREN]=[{[NAME]=George, [SEX]=MALE, [AGE]=8}, " +
                "{[NAME]=Charlotte, [SEX]=FEMALE, [AGE]=7}, " +
                "{[NAME]=Louis, [SEX]=MALE, [AGE]=4}]}"));
    }

    @Test
    void schemaWithRefs() {

        SchemaDefs defs = SchemaDefs.newInstance();

        EnumSchema<Person> personSchema = EnumSchemaBuilder.forEnumType(Person.class)
                .withSchemaDefs(defs)
                .withSchemaName("person")
                .addField(Person.NAME, String.class)
                .addField(Person.SEX, Sex.class)
                .addField(Person.AGE, int.class)
                .build();

        EnumSchema<Family> familySchema = EnumSchemaBuilder.forEnumType(Family.class)
                .withSchemaDefs(defs)
                .addRef(Family.MUM, "person")
                .addRef(Family.DAD, "person")
                .addRepeatingField(Family.CHILDREN, personSchema)
                .build();

        GenericData<Family> family = EnumMapData.newBuilder(familySchema)
                .with(Family.MUM, EnumMapData.newBuilder(personSchema)
                        .with(Person.NAME, "Kate")
                        .with(Person.SEX, Sex.FEMALE)
                        .withInt(Person.AGE, 40)
                        .build())
                .with(Family.DAD, EnumMapData.newBuilder(personSchema)
                        .with(Person.NAME, "William")
                        .with(Person.SEX, Sex.MALE)
                        .withInt(Person.AGE, 39)
                        .build())
                .with(Family.CHILDREN, Arrays.asList(
                        EnumMapData.newBuilder(personSchema)
                                .with(Person.NAME, "George")
                                .with(Person.SEX, Sex.MALE)
                                .withInt(Person.AGE, 8)
                                .build(),
                        EnumMapData.newBuilder(personSchema)
                                .with(Person.NAME, "Charlotte")
                                .with(Person.SEX, Sex.FEMALE)
                                .withInt(Person.AGE, 7)
                                .build(),
                        EnumMapData.newBuilder(personSchema)
                                .with(Person.NAME, "Louis")
                                .with(Person.SEX, Sex.MALE)
                                .withInt(Person.AGE, 4)
                                .build()))
                .build();

        assertThat(family.toString(), is("{[MUM]={[NAME]=Kate, [SEX]=FEMALE, [AGE]=40}, " +
                "[DAD]={[NAME]=William, [SEX]=MALE, [AGE]=39}, " +
                "[CHILDREN]=[{[NAME]=George, [SEX]=MALE, [AGE]=8}, " +
                "{[NAME]=Charlotte, [SEX]=FEMALE, [AGE]=7}, " +
                "{[NAME]=Louis, [SEX]=MALE, [AGE]=4}]}"));
    }

    enum Node {
        NAME,
        CHILDREN
    }

    @Test
    void canBuildRecursiveSchema() {

        EnumSchema<Node> nodeSchema = EnumSchemaBuilder.forEnumType(Node.class)
                .withSchemaDefs(SchemaDefs.newInstance())
                .withSchemaName("node")
                .addField(Node.NAME, String.class)
                .addRepeatingRef(Node.CHILDREN, "node")
                .build();

        assertThat(nodeSchema.toString(),
                is("{[1:NAME]=java.lang.String, [2:CHILDREN]=[Ref#node]}"));

        GenericData<Node> george = EnumMapData.newBuilder(nodeSchema)
                .with(Node.NAME, "George")
                .build();
        GenericData<Node> charlotte = EnumMapData.newBuilder(nodeSchema)
                .with(Node.NAME, "Charlot")
                .build();
        GenericData<Node> louis = EnumMapData.newBuilder(nodeSchema)
                .with(Node.NAME, "Louis")
                .build();

        GenericData<Node> william = EnumMapData.newBuilder(nodeSchema)
                .with(Node.NAME, "William")
                .with(Node.CHILDREN, Arrays.asList(george, charlotte, louis))
                .build();

        assertThat(william.toString(), is("{[NAME]=William, [CHILDREN]=[" +
                "{[NAME]=George, [CHILDREN]=null}, " +
                "{[NAME]=Charlot, [CHILDREN]=null}, " +
                "{[NAME]=Louis, [CHILDREN]=null}]}"));
    }
}