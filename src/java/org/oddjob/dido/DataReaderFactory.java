package org.oddjob.dido;


public interface DataReaderFactory {

	public DataReader readerFor(DataInProvider dataInProvider)
	throws UnsupportedeDataInException;
	
}
