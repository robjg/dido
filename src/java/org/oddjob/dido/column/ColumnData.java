package org.oddjob.dido.column;

public interface ColumnData<T> {

	public int getColumnIndex();
	
	public Class<T> getColumnType();

}
