package dido.data;

import dido.data.util.DataBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Abstract of the various ways of creating Dido Data from field values.
 */
public interface FromValues {

    DataSchema getSchema();

    DidoData of(Object... values);

    DidoData ofCollection(Collection<?> values);

    DidoData ofMap(Map<String, ?> map);

    DidoData copy(DidoData from);

    DataBuilder asBuilder();

        /**
         * Allows a stream of values to be collected into an item of data. Not sure how useful this is.
         *
         * @return A collector.
         */
    Collector<Object, WritableData, DidoData> toCollector();

    Many many();

    interface Many {

        Many of(Object... values);

        Stream<DidoData> toStream();

        List<DidoData> toList();
    }

}
