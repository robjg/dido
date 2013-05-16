package org.oddjob.dido;


public interface DataWriterFactory {

	DataWriter writerFor(DataOut dataOut)
	throws UnsupportedeDataOutException;
}
