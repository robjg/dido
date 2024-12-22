package dido.poi;

import dido.data.ReadSchema;
import dido.how.conversion.DidoConversionProvider;

public interface CellOutProvider extends CellProvider {

    CellOut provideCellOut(ReadSchema schema,
                           int index,
                           DidoConversionProvider conversionProvider);
}
