package dido.oddjob.beanbus;

import dido.data.DataSchema;
import dido.data.schema.SchemaAware;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Helper class for when a schema is available.
 */
public class SchemaTracker implements SchemaAware {

    private static final Logger logger = Logger.getLogger(SchemaTracker.class.getName());

    private volatile Consumer<? super DataSchema> onSchema;

    private volatile DataSchema schema;

    @Override
    public void setSchema(DataSchema schema) {
        if (schema == null) {
            this.schema = null;
            this.onSchema = null;
        }
        else {
            if (this.schema == null) {
                this.schema = schema;

                if (onSchema != null) {
                    onSchema.accept(schema);
                }
            }
            else {
                logger.info("Ignoring change in schema.");
            }
        }
    }

    public void onSchema(Consumer<? super DataSchema> onSchema) {

        if (schema != null) {
            onSchema.accept(schema);
        }
        else {
            this.onSchema = onSchema;
        }
    }

    public DataSchema getSchema() {
        return schema;
    }

}
