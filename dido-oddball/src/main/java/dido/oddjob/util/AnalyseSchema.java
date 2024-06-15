package dido.oddjob.util;

import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.GenericData;
import org.oddjob.beanbus.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

public class AnalyseSchema implements Consumer<DidoData> {

    private static final Logger logger = LoggerFactory.getLogger(AnalyseSchema.class);

    private String name;

    private volatile Consumer<? super DidoData> to;

    private DataSchema lastSchema;

    private int count;

    @Override
    public void accept(DidoData didoData) {

        ++count;

        DataSchema schema = didoData.getSchema();
        if (lastSchema == null || !lastSchema.equals(schema)) {

            logger.info("New Schema at {}: {}", count, schema);
            lastSchema  = schema;
        }

        if (to != null) {
            to.accept(didoData);
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
    public void setTo(Consumer<? super DidoData> to) {
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
