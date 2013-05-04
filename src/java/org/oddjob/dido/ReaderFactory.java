package org.oddjob.dido;

import org.oddjob.dido.io.DataReader;

public interface ReaderFactory {

	DataReader readerFor(DataInProvider dataInProvider)
	throws UnsupportedeDataInException;
}
