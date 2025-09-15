package dido.oddjob.schema;

import dido.data.DataSchema;
import dido.data.schema.SchemaBuilder;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SchemaDefsBeanTest {

    @Test
    void nestedSchema() throws ArooaConversionException {

        ArooaSession session = new StandardArooaSession();

        SchemaFieldBean fruitField = new SchemaFieldBean();
        fruitField.setArooaSession(session);
        fruitField.setName("fruit");
        fruitField.setType(String.class.getName());

        SchemaFieldBean qtyField = new SchemaFieldBean();
        qtyField.setArooaSession(session);
        qtyField.setName("qty");
        qtyField.setType(int.class.getName());

        SchemaBean lineSchema = new SchemaBean();
        lineSchema.setOf(0, fruitField);
        lineSchema.setOf(1, qtyField);

        SchemaFieldBean orderIdField = new SchemaFieldBean();
        orderIdField.setArooaSession(session);
        orderIdField.setName("orderId");
        orderIdField.setType(String.class.getName());

        SchemaFieldBean orderLinesField = new SchemaFieldBean();
        orderLinesField.setArooaSession(session);
        orderLinesField.setName("orderLines");
        orderLinesField.setRepeating(true);
        orderLinesField.setNested(lineSchema.toSchema());

        SchemaBean orderSchema = new SchemaBean();
        orderSchema.setOf(0, orderIdField);
        orderSchema.setOf(1, orderLinesField);

        DataSchema schema = orderSchema.toSchema();

        DataSchema nestSchema = schema.getSchemaNamed("orderLines");

        assertThat(nestSchema, is(SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .build()));
    }


    @Test
    void defsNestedRef() throws ArooaConversionException {

        ArooaSession session = new StandardArooaSession();

        SchemaFieldBean fruitField = new SchemaFieldBean();
        fruitField.setArooaSession(session);
        fruitField.setName("fruit");
        fruitField.setType(String.class.getName());

        SchemaFieldBean qtyField = new SchemaFieldBean();
        qtyField.setArooaSession(session);
        qtyField.setName("qty");
        qtyField.setType(int.class.getName());

        SchemaBean lineSchema = new SchemaBean();
        lineSchema.setName("orderLine");
        lineSchema.setOf(0, fruitField);
        lineSchema.setOf(1, qtyField);

        SchemaFieldBean orderIdField = new SchemaFieldBean();
        orderIdField.setArooaSession(session);
        orderIdField.setName("orderId");
        orderIdField.setType(String.class.getName());

        SchemaFieldBean orderLinesField = new SchemaFieldBean();
        orderLinesField.setArooaSession(session);
        orderLinesField.setName("orderLines");
        orderLinesField.setRepeating(true);
        orderLinesField.setRef("orderLine");

        SchemaDefsBean defsBean = new SchemaDefsBean();
        defsBean.setSchemas(0, lineSchema);

        SchemaBean orderSchema = new SchemaBean();
        orderSchema.setName("order");
        orderSchema.setOf(0, orderIdField);
        orderSchema.setOf(1, orderLinesField);
        orderSchema.setDefs(defsBean.toValue());

        DataSchema schema = orderSchema.toSchema();

        DataSchema nestSchema = schema.getSchemaNamed("orderLines");

        assertThat(nestSchema, is(SchemaBuilder.newInstance()
                .addNamed("fruit", String.class)
                .addNamed("qty", int.class)
                .build()));
    }

    @Test
    void defsRecursive() throws ArooaConversionException {

        ArooaSession session = new StandardArooaSession();

        SchemaFieldBean name = new SchemaFieldBean();
        name.setArooaSession(session);
        name.setName("name");
        name.setType(String.class.getName());

        SchemaFieldBean children = new SchemaFieldBean();
        children.setArooaSession(session);
        children.setName("children");
        children.setRepeating(true);
        children.setRef("person");

        SchemaBean schemaBean = new SchemaBean();
        schemaBean.setName("person");
        schemaBean.setOf(0, name);
        schemaBean.setOf(1, children);

        SchemaDefsBean defsBean = new SchemaDefsBean();
        schemaBean.setDefs(defsBean.toValue());

        DataSchema schema = schemaBean.toSchema();

        assertThat(schema.getSchemaNamed("children"), is(schema));

    }
}