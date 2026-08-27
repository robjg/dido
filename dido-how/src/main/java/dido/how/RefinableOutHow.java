package dido.how;

import dido.data.schema.SchemaRefinable;

/**
 * A Data Out How that can be refined with a Schema.
 *
 * @param <O> The output type.
 */
public interface RefinableOutHow<O> extends DataOutHow<O>, SchemaRefinable<DataOutHow<O>> {

}
