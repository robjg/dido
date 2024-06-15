package dido.data;

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
        assertThat(schema.getIndex(Fields.Fruit), is(1));
        assertThat(schema.getFieldAt(2), is(Fields.Qty));
        assertThat(schema.getIndex(Fields.Qty), is(2));
        assertThat(schema.getFieldAt(3), is(Fields.Price));
        assertThat(schema.getIndex(Fields.Price), is(3));

        assertThat(schema.nextIndex(1), is(2));
        assertThat(schema.nextIndex(2), is(3));
        assertThat(schema.getFields(), contains(Fields.Fruit, Fields.Qty, Fields.Price));
    }

    @Test
    void builderWithNoSchema() {

        GenericDataBuilder<Fields> builderNoSchema = EnumMapData.builderForEnum(Fields.class);

        GenericData<Fields> data3 = builderNoSchema
                .setString(Fields.Fruit, "Apple")
                .setInt(Fields.Qty, 2)
                .setDouble(Fields.Price, 75.3)
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
                .set(Fields.Fruit, "Apple")
                .set(Fields.Qty, 2)
                .set(Fields.Price, 75.3)
                .build();

        GenericData<Fields> data2 = builder
                .set(Fields.Fruit, "Orange")
                .set(Fields.Qty, 3)
                .set(Fields.Price, 47.3)
                .build();

        assertThat(data1.hasIndex(1), is(true));
        assertThat(data1.hasFieldOf(Fields.Fruit), is(true));

        assertThat(data1.getStringOf(Fields.Fruit), is("Apple"));
        assertThat(data1.getStringAt(1), is("Apple"));
        assertThat(data1.getIntOf(Fields.Qty), is(2));
        assertThat(data1.getIntAt(2), is(2));
        assertThat(data1.getDoubleOf(Fields.Price), is(75.3));
        assertThat(data1.getDoubleAt(3), is(75.3));

        assertThat(data2.getStringOf(Fields.Fruit), is("Orange"));
        assertThat(data2.getStringAt(1), is("Orange"));

        GenericDataBuilder<Fields> builderNoSchema = EnumMapData.builderForEnum(Fields.class);

        GenericData<Fields> data3 = builderNoSchema
                .setString(Fields.Fruit, "Apple")
                .setInt(Fields.Qty, 2)
                .setDouble(Fields.Price, 75.3)
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

        EnumData<Family> family = EnumMapData.newBuilder(familySchema)
                .set(Family.MUM, EnumMapData.newBuilder(personSchema)
                        .set(Person.NAME, "Kate")
                        .set(Person.SEX, Sex.FEMALE)
                        .setInt(Person.AGE, 40)
                        .build())
                .set(Family.DAD, EnumMapData.newBuilder(personSchema)
                        .set(Person.NAME, "William")
                        .set(Person.SEX, Sex.MALE)
                        .setInt(Person.AGE, 39)
                        .build())
                .set(Family.CHILDREN, Arrays.asList(
                        EnumMapData.newBuilder(personSchema)
                                .set(Person.NAME, "George")
                                .set(Person.SEX, Sex.MALE)
                                .setInt(Person.AGE, 8)
                                .build(),
                        EnumMapData.newBuilder(personSchema)
                                .set(Person.NAME, "Charlotte")
                                .set(Person.SEX, Sex.FEMALE)
                                .setInt(Person.AGE, 7)
                                .build(),
                        EnumMapData.newBuilder(personSchema)
                                .set(Person.NAME, "Louis")
                                .set(Person.SEX, Sex.MALE)
                                .setInt(Person.AGE, 4)
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

        SchemaReference<Node> nodeSchemaRef = SchemaReference.blank();

        EnumSchema<Node> nodeSchema = EnumSchemaBuilder.forEnumType(Node.class)
                .addField(Node.NAME, String.class)
                .addRepeatingField(Node.CHILDREN, nodeSchemaRef)
                .build();

        assertThat(nodeSchema.toString(), is("{[1:NAME]=java.lang.String, [2:CHILDREN]=[SchemaReference (unset)]}"));

        nodeSchemaRef.set(nodeSchema);

        assertThat(nodeSchema.toString(), is("{[1:NAME]=java.lang.String, [2:CHILDREN]=[SchemaReference]}"));

        EnumData<Node> george = EnumMapData.newBuilder(nodeSchema)
                .set(Node.NAME, "George")
                .build();
        EnumData<Node> charlotte = EnumMapData.newBuilder(nodeSchema)
                .set(Node.NAME, "Charlot")
                .build();
        EnumData<Node> louis = EnumMapData.newBuilder(nodeSchema)
                .set(Node.NAME, "Louis")
                .build();

        EnumData<Node> william = EnumMapData.newBuilder(nodeSchema)
                .set(Node.NAME, "William")
                .set(Node.CHILDREN, Arrays.asList(george, charlotte, louis))
                .build();

        assertThat(william.toString(), is("{[NAME]=William, [CHILDREN]=[" +
                "{[NAME]=George, [CHILDREN]=null}, " +
                "{[NAME]=Charlot, [CHILDREN]=null}, " +
                "{[NAME]=Louis, [CHILDREN]=null}]}"));
    }
}