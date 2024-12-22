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

	interface Capture {

		void accept(SchemaField schemaField, FieldGetter getter);
	}

	void capture(Capture capture);

}
