package org.oddjob.dido.text;


public class MappedFieldsOut extends BaseFieldsOut {

	public MappedFieldsOut(FieldsWriter writer) {
		super(writer); 
	}
	
	@Override
	public int writeHeading(String heading, int column) {
		throw new IllegalStateException(
				"Can't write headings during data phase.");
	}
	
	@Override
	public void setColumn(int column, String value) {
		setValue(column, value);
	}	
}
