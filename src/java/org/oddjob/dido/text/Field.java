package org.oddjob.dido.text;

import org.oddjob.dido.AbstractParentStencil;
import org.oddjob.dido.BoundedDataNode;
import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;


public class Field 
extends AbstractParentStencil<String, FieldsIn, TextIn, FieldsOut, TextOut>
implements BoundedDataNode<FieldsIn, TextIn, FieldsOut, TextOut> {

	private String title;
	
	private int column;
	
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	@Override
	public void begin(FieldsIn in) {
		String title = getTitle();
		if (title == null) {
			column = in.columnFor(getName(), false, column);
		}		
		else {
			column = in.columnFor(title, true, column);
		}
	}
	
	public WhereNextIn<TextIn> in(FieldsIn fields) {
		
		String field = null;
		if (column > 0) {
			field = fields.getColumn(column);
		}

		if (!hasChildren()) {
			setValue(field);
			return new WhereNextIn<TextIn>();
		}
		else {
			return new WhereNextIn<TextIn>(
					childrenToArray(), new StringTextIn(field));
		}
	}
	
	@Override
	public void end(FieldsIn in) {
	}
	
	@Override
	public void begin(FieldsOut out) {

		String heading = title;
		if (heading == null) {
			heading = getName();
		}
		column = out.writeHeading(heading, column);
	}
	
	
	public WhereNextOut<TextOut> out(final FieldsOut outgoing) 
	throws DataException {

		if (column == 0) {
			return null;
		}
		
		if (!hasChildren()) {
			outgoing.setColumn(column, getValue());
			return new WhereNextOut<TextOut>();
		}
		else {
			TextOut textOut = new StringTextOut() {
				@Override
				public boolean flush() throws DataException {
					outgoing.setColumn(column, toString());
					return true;
				}
			};
			
			return new WhereNextOut<TextOut>(
					childrenToArray(), textOut);
		}
	}
	
	@Override
	public void flush(FieldsOut data, TextOut childData) throws DataException {
		childData.flush();
	}
	
	@Override
	public void end(FieldsOut out) {
		
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}	
	
	public void setValue(String value) {
		this.value(value);
	}
	
	public String getValue() {
		return this.value();
	}
}
