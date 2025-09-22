package dido.operators;

import dido.data.CompactData;
import dido.data.DataSchema;
import dido.data.DidoData;
import dido.data.immutable.CompactDatas;

import java.util.function.Function;

/**
 * Provide {@link CompactData} that is a subset of some other data.
 *
 */
public class CompactSubData {


    private static class MappingFunc implements Function<DidoData, CompactData> {

        private final int[] indices;

        private DataSchema lastSchema;

        private CompactData.Extractor func;

        private MappingFunc(int[] indices) {
            this.indices = indices;
        }

        @Override
        public CompactData apply(DidoData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                func = CompactDatas.extractorForIndices(lastSchema, indices);
            }
            return func.apply(original);
        }
    }

    public static class FieldMappingFunc implements Function<DidoData, CompactData> {

        private final String[] fields;

        private DataSchema lastSchema;

        private CompactData.Extractor func;

        private FieldMappingFunc(String[] fields) {
            this.fields = fields;
        }

        @Override
        public CompactData apply(DidoData original) {

            if (lastSchema == null || !lastSchema.equals(original.getSchema())) {
                lastSchema = original.getSchema();
                func = CompactDatas.extractorForNames(lastSchema, fields);
            }
            return func.apply(original);
        }
    }

    public static class Configuration {

        public Function<DidoData, CompactData> andIndices(int... indices) {

            return new MappingFunc(indices);
        }

        public Function<DidoData, CompactData> andFields(String... names) {

            return new FieldMappingFunc(names);
        }
    }

    public static Configuration with() {
        return new Configuration();
    }

    public static Function<DidoData, CompactData> ofIndices(int... indices) {

        return new MappingFunc(indices);
    }

    public static Function<DidoData, CompactData> ofFields(String... fields) {

        return new FieldMappingFunc(fields);
    }

}
