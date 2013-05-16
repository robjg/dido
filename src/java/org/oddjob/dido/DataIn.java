package org.oddjob.dido;



public interface DataIn {

	<T extends DataIn> T provideIn(Class<T> type) 
	throws UnsupportedeDataInException;
	
}
