package org.oddjob.dido.poi.layouts;

import org.apache.log4j.Logger;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutNode;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.style.StyleBean;
import org.oddjob.dido.poi.style.StyleFactoryRegistry;

/**
 * The {@link Layout} representation of a Spreadsheet Book. 
 * 
 * @author rob
 *
 */
public class DataBook 
extends LayoutNode {
	
	private static final Logger logger = Logger.getLogger(DataBook.class);
	
	private final StyleFactoryRegistry styles = new StyleFactoryRegistry();
	
	/**
	 * Add or remove a child.
	 * 
	 * @param index
	 * @param child
	 */
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		logger.debug("Creating reader from [" + dataIn + "]");
		
		BookIn book = dataIn.provideDataIn(BookIn.class);
				
		return nextReaderFor(book);
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		logger.debug("Creating writer from [" + dataOut + "]");
		
		final BookOut bookOut = dataOut.provideDataOut(BookOut.class);
		
		if (styles.hasStyles()) {
			bookOut.addStyleFactory(styles);
		}
		
		final DataWriter nextWriter = nextWriterFor(bookOut);
		
		return new DataWriter() {
			
			@Override
			public boolean write(Object object) throws DataException {
				return nextWriter.write(object);
			}
			
			@Override
			public void close() throws DataException {
				nextWriter.close();
				bookOut.close();
			}
		};
	}

	@Override
	public void reset() {
		super.reset();
	}
		
	public void setStyles(String styleName, StyleBean styleBean) {
		if (styleBean == null) {
			styles.removeStyle(styleName);
		}
		else {
			styles.registerStyle(styleName, styleBean);
		}
	}
	
	@Override
	public String toString() {
		String name = getName();
		return getClass().getSimpleName() + 
			(name == null ? "" : " " + name);
	}

}
