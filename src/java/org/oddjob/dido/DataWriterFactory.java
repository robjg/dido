package org.oddjob.dido;


public interface DataWriterFactory {

	DataWriter writerFor(DataOutProvider dataOutProvider)
	throws UnsupportedeDataOutException;
}
