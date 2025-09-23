package dido.operators;

import dido.data.DidoData;
import dido.operators.transform.DidoTransform;

import java.util.function.Function;

/**
 * Provide data that is a subset of some other data allowing for the schema to change.
 *
 * @see SubData
 */
public class FlexibleSubData {

    private static class SchemaUnknownMappingFunc implements Function<DidoData, DidoData> {

        private final int[] indices;

        private DidoTransform mappingFunc;

        private SchemaUnknownMappingFunc(int[] indices) {
            this.indices = indices;
        }

        @Override
        public DidoData apply(DidoData original) {

            if (mappingFunc == null || !mappingFunc.getResultantSchema().equals(original.getSchema())) {
                mappingFunc = SubData.asMappingFrom(original.getSchema()).withIndices(indices);
            }

            return mappingFunc.apply(original);
        }
    }

    public static class SchemaUnknownFieldMappingFunc implements Function<DidoData, DidoData> {

        private final String[] fields;

        private DidoTransform mappingFunc;

        private SchemaUnknownFieldMappingFunc(String[] fields) {
            this.fields = fields;
        }

        @Override
        public DidoData apply(DidoData original) {

            if (mappingFunc == null || !mappingFunc.getResultantSchema().equals(original.getSchema())) {
                mappingFunc = SubData.asMappingFrom(original.getSchema()).withNames(fields);
            }

            return mappingFunc.apply(original);
        }
    }

    public static Function<DidoData, DidoData> ofIndices(int... indices) {

        return new SchemaUnknownMappingFunc(indices);
    }

    public static Function<DidoData, DidoData> ofFields(String... fields) {

        return new SchemaUnknownFieldMappingFunc(fields);
    }

}
