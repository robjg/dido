package dido.examples;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.RepeatingData;
import dido.data.schema.DataSchemaSchema;
import dido.data.schema.SchemaDefs;
import dido.how.DataOut;
import dido.json.DataOutJson;
import dido.json.SchemaAsJson;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SchemaExamplesTest {

    @Test
    void treeSchema() {

        // #treeSchema{
        DataSchema nodeSchema = DataSchema.builder()
                .withSchemaDefs(SchemaDefs.newInstance())
                .withSchemaName("Node")
                .addNamed("Name", String.class)
                .addRepeatingRefNamed("Children", "Node")
                .build();

        DidoData aTree = DidoData.withSchema(nodeSchema)
                .of("root", RepeatingData.of(
                        DidoData.withSchema(nodeSchema)
                                .of("child-a"),
                        DidoData.withSchema(nodeSchema)
                                .of("child-b", RepeatingData.of(
                                        DidoData.withSchema(nodeSchema).of("grandchild-1")))
                ));

        assertThat(aTree.toString(), ignoresAllWhitespaces(
                """
            {[1:Name]=root, [2:Children]=[
                {[1:Name]=child-a, [2:Children]=null},
                {[1:Name]=child-b, [2:Children]=[
                    {[1:Name]=grandchild-1, [2:Children]=null}]}]}
            """
        ));
        // }#treeSchema

        // #schemaAsData{
        DidoData schemaAsData = DataSchemaSchema.schemaToData(nodeSchema);

        assertThat(schemaAsData.toString(), ignoresAllWhitespaces(
                """
            {[1:Name]=Node, [2:Defs]=null, [3:Schema]={
                [1:Fields]=[
                    {[1:Index]=1, [2:Name]=Name, [3:Type]=java.lang.String, [4:Nested]=null},
                    {[1:Index]=2, [2:Name]=Children, [3:Type]=dido.data.RepeatingData, [4:Nested]={
                        [1:Ref]=Node, [2:Schema]=null}
                    }
                ]}
            }
            """
                // }#schemaAsData
        ));

        // #schemaDataAsJson{
        StringWriter output = new StringWriter();

        try (DataOut out = DataOutJson.with()
                .schema(schemaAsData.getSchema())
                .pretty()
                .toAppendable(output)) {

            out.accept(schemaAsData);
        }

        assertThat(output.toString(), ignoresAllWhitespaces(
                """
{
  "Name": "Node",
  "Schema": {
    "Fields": [
      {
        "Index": 1,
        "Name": "Name",
        "Type": "java.lang.String"
      },
      {
        "Index": 2,
        "Name": "Children",
        "Type": "dido.data.RepeatingData",
        "Nested": {
          "Ref": "Node"
        }
      }
    ]
  }
}
                        """));

        // }#schemaDataAsJson

    }

    @Test
    void schemaAsJson() throws Exception {

        DataSchema back = SchemaAsJson.fromJson(
                getClass().getResourceAsStream("/schema/SchemaAsJson.json")
        );

        assertThat(back, is(DataSchemaSchema.DATA_SCHEMA_SCHEMA));

    }

    public static IgnoresAllWhitespacesMatcher ignoresAllWhitespaces(String expected) {
        return new IgnoresAllWhitespacesMatcher(expected);
    }


    public static class IgnoresAllWhitespacesMatcher extends BaseMatcher<String> {
        public String expected;

        private IgnoresAllWhitespacesMatcher(String expected) {
            this.expected = expected.replaceAll("\\s+", "");
        }

        @Override
        public boolean matches(Object actual) {
            return expected.equals(((String) actual).replaceAll("\\s+", ""));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String.format("the given String should match '%s' without whitespaces", expected));
        }
    }
}
