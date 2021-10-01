package dido.poi;

import dido.data.GenericData;
import org.oddjob.dido.poi.HeaderRowOut;
import org.oddjob.dido.poi.RowOut;

/**
 * Represents a spreadsheet cell for writing data too.
 * 
 * @author rob
 *
 * @param <T>
 */
public interface CellOut<T> {

	void writeHeader(HeaderRowOut headerRowOut);

	void setValue(RowOut rowOut, GenericData<String> data);
}
