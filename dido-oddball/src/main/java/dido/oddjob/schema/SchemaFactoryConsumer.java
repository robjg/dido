package dido.oddjob.schema;

import dido.data.SchemaFactory;
import org.oddjob.arooa.convert.ArooaConversionException;

/**
 * Something that can add itself to a {@link SchemaFactory}.
 *
 * @see SchemaFieldBean
 */
public interface SchemaFactoryConsumer {

    void acceptSchemaFactory(SchemaFactory schemaFactory)
            throws ArooaConversionException;
}
