package dido.data.util;

import dido.data.DataSchema;
import dido.data.NoSuchFieldException;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * For creating things based on a selection of fields
 * @param <C>
 */
public class FieldSelectionFactory<C> {

    private final DataSchema schema;

    private final Function<int[], C> creator;

    public FieldSelectionFactory(DataSchema schema, Function<int[], C> creator) {
        this.schema = schema;
        this.creator = creator;
    }

    public C withNames(Collection<String> fields) {
        return withNames(fields.toArray(new String[0]));
    }

    public C withNames(String... fields) {

        return withIndices(indicesOf(fields));
    }

    public C withIndices(int... indices) {

        return create(indices);
    }

    public C excludingNames(Collection<String> fields) {
        return excludingNames(fields.toArray(new String[0]));
    }

    public C excludingNames(String... fields) {
        return excludingIndices(indicesOf(fields));
    }

    public C excludingIndices(int... indices) {

        TreeSet<Integer> all = Arrays.stream(schema.getIndices()).boxed()
                .collect(Collectors.toCollection(TreeSet::new));

        for (int index : indices) {
            all.remove(index);
        }

        return create(all.stream()
                .mapToInt(Integer::intValue).toArray());
    }

    protected int[] indicesOf(String... fields) {

        int[] indices = new int[fields.length];
        for (int i = 0; i < indices.length; i++) {
            String fieldName = fields[i];
            int index = schema.getIndexNamed(fieldName);
            if (index == 0) {
                throw new NoSuchFieldException(fieldName, schema);
            }
            indices[i] = index;
        }
        return indices;
    }

    protected C create(int[] indices) {
        Arrays.sort(indices);
        return creator.apply(indices);
    }
}