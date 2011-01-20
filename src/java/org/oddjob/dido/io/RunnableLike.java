package org.oddjob.dido.io;

import org.oddjob.dido.DataException;

interface RunnableLike {
	public void run() throws DataException;
}