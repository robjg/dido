package dido.oddjob.transpose;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.MapData;
import dido.data.SchemaBuilder;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ValueCopyFactoryTest {

    @Test
    void testNewType() {

        ArooaSession session = new StandardArooaSession();

        ValueCopyFactory test =  new ValueCopyFactory();
        test.setArooaSession(session);
        test.setType(Integer.class);
        test.setTo("FooAmount");

        TransposerFactory<String, String> transposerFactory = test.toValue();

        DataSchema<String> inSchema = SchemaBuilder.forStringFields()
                .addField("Foo", String.class)
                .build();

        SchemaSetter<String> schemaSetter = mock(SchemaSetter.class);

        Transposer<String, String> transposer = transposerFactory.create(1, inSchema, schemaSetter);

        verify(schemaSetter).setFieldAt(1, "FooAmount", Integer.class);

        DataSetter<String> dataSetter = mock(DataSetter.class);

        GenericData<String> data = MapData.of("Foo", "423");

        transposer.transpose(data, dataSetter);

        verify(dataSetter).set("FooAmount", 423);
    }
}