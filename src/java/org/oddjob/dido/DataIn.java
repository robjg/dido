package org.oddjob.dido;



public interface DataIn {

	<T extends DataIn> T provideDataIn(Class<T> type) 
	throws DataException;
	
}
