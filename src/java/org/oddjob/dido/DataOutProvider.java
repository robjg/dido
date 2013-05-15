package org.oddjob.dido;

public interface DataOutProvider extends DataOut {

	<T extends DataOut> T provideOut(Class<T> type) 
	throws UnsupportedeDataOutException;
	
}
