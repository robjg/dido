package dido.operators.transform;

import dido.data.DataFactory;

/**
 * Created by {@link FieldOperationDefinition}
 */
public interface FieldOperationFactory {

    FieldOperation create(DataFactory<?> dataFactory);
}
