package org.oddjob.dido;



public interface DataIn {

	<T extends DataIn> T provide(Class<T> type) 
	throws UnsupportedeDataInException;
	
}
