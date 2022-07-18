package dido.oddjob.util;

import dido.data.DataSchema;
import dido.data.GenericData;
import dido.data.IndexedData;
import org.oddjob.beanbus.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

public class AnalyseSchema implements Consumer<IndexedData<?>> {

    private static final Logger logger = LoggerFactory.getLogger(AnalyseSchema.class);

    private String name;

    private volatile Consumer<? super GenericData<?>> to;

    private DataSchema<?> lastSchema;

    private int count;

    @Override
    public void accept(IndexedData<?> indexedData) {

        ++count;

        DataSchema<?> schema = indexedData.getSchema();
        if (lastSchema == null || !lastSchema.equals(schema)) {

            logger.info("New Schema at {}: {}", count, schema);
            lastSchema  = schema;
        }

        if (to != null) {
            to.accept(GenericData.from(indexedData));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Consumer<? super GenericData<?>> getTo() {
        return to;
    }

    @Destination
    public void setTo(Consumer<? super GenericData<?>> to) {
        this.to = to;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElseGet(name, () -> getClass().getSimpleName());
    }
}
