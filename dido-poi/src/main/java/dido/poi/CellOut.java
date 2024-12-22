package dido.poi;

import dido.data.DidoData;

/**
 * Represents a spreadsheet cell for writing data too.
 * 
 * @author rob
 *
 */
public interface CellOut {

	void writeHeader(HeaderRowOut headerRowOut);

	void setValue(RowOut rowOut, DidoData data);
}
