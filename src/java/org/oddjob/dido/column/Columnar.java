package org.oddjob.dido.column;

public interface Columnar {

	public int columnIndexFor(String columnName, int columnIndex);
	
	public ColumnMetaData getColumnMetaData(int columnIndex);
}
