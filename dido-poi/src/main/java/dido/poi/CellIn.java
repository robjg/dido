package dido.poi;

import dido.data.FieldGetter;
import dido.data.SchemaField;

/**
 * Represents a spreadsheet cell for reading data from.
 * 
 * @author rob
 *
 */
public interface CellIn {

	/**
	 * Used to capture the getter from the CellIn.
	 */
	interface Capture {

		void accept(SchemaField schemaField, FieldGetter getter);
	}

	/**
	 * Capture the getter from the Cell.
	 */
	void capture(Capture capture);

}
