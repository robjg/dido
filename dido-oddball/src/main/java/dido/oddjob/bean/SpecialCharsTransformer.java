package dido.oddjob.bean;

import dido.data.DidoData;
import org.oddjob.arooa.types.ValueFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SpecialCharsTransformer implements ValueFactory<Function<DidoData, DidoData>> {

    /** The characters in a field name that need to be replace. */
    private final static Set<Character> special =
            new HashSet<>(Arrays.asList('[', ']', '(', ')', '.'));

    @Override
    public Function<DidoData, DidoData> toValue() {
        return new Impl();
    }

    static class Impl implements Function<DidoData, DidoData> {

        private Function<DidoData, DidoData> renamedData;

        @Override
        public DidoData apply(DidoData data) {

            if (renamedData == null) {

                RenamedData.TransformBuilder builder =
                        RenamedData.transformerWithNameClash(to -> to + "_");

                for (String field : data.getSchema().getFieldNames()) {
                    builder.addMapping(field, replaceSpecialCharacters(field));
                }

                renamedData = builder.build();
            }

            return renamedData.apply(data);
        }
    }

    // Should be centralised - maybe on ClassCreator?
    static protected String replaceSpecialCharacters(String name) {

        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            if (special.contains(chars[i])) {
                chars[i] = '_';
            }
        }
        return new String(chars);
    }

}
