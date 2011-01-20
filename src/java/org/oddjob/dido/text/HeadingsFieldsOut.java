package org.oddjob.dido.text;


public class HeadingsFieldsOut extends BaseFieldsOut {

	private int column = 1;
	
	private final boolean hasHeadings;
	
	public HeadingsFieldsOut(FieldsWriter writer, boolean hasHeadings) {
		super(writer); 
		this.hasHeadings = hasHeadings;
	}
	
	@Override
	public int writeHeading(String heading, int column) {
		if (column < 1) {
			column = this.column++;
		}
		else {
			this.column = column + 1;
		}
		
		if (hasHeadings) {
			setValue(column, heading);
		}
		
		return column;
	}
	
	@Override
	public void setColumn(int column, String value) {
		throw new IllegalStateException(
				"Can't set columns during begin phase.");
	}
	
}
