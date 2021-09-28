package dido.oddjob.bean;

import dido.data.GenericData;
import org.oddjob.arooa.types.ValueFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SpecialCharsTransformer implements ValueFactory<Function<GenericData<String>, GenericData<String>>> {

    /** The characters in a field name that need to be replace. */
    private final static Set<Character> special =
            new HashSet<>(Arrays.asList('[', ']', '(', ')', '.'));

    @Override
    public Function<GenericData<String>, GenericData<String>> toValue() {
        return new Impl();
    }

    static class Impl implements Function<GenericData<String>, GenericData<String>> {

        private Function<GenericData<String>, GenericData<String>> renamedData;

        @Override
        public GenericData<String> apply(GenericData<String> data) {

            if (renamedData == null) {

                RenamedData.TransformBuilder<String, String> builder =
                        RenamedData.transformerWithNameClash(to -> to + "_");

                for (String field : data.getSchema().getFields()) {
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
