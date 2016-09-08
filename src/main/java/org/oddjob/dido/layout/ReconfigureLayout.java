package org.oddjob.dido.layout;

import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaContextAware;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;

public class ReconfigureLayout extends LayoutNode
implements ArooaContextAware {

	private RuntimeConfiguration runtime;
	
	@ArooaHidden
	@Override
	public void setArooaContext(ArooaContext context) {
		runtime = context.getRuntime();
	}
		
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		runtime.configure();
		
		return nextReaderFor(dataIn);
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		runtime.configure();
		
		return nextWriterFor(dataOut);
	}
}
