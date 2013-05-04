package org.oddjob.dido;

public interface DataInProvider extends DataIn {

	<T extends DataIn> T provideIn(Class<T> type) 
	throws UnsupportedeDataInException;
	
}
