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
import org.oddjob.dido.poi.data.PoiWorkbook;
import org.oddjob.dido.poi.style.StyleBean;
import org.oddjob.dido.poi.style.StyleFactoryRegistry;

/**
 * @oddjob.description The definition of a Spreadsheet Book.
 * <p>
 * This layout type will probably be a top level node in layout definition
 * and will be used in conjunction with an {@link PoiWorkbook} for providing
 * data in and data out.
 * 
 * @author rob
 *
 */
public class DataBook 
extends LayoutNode {
	
	private static final Logger logger = Logger.getLogger(DataBook.class);
	
	/**
	 * @oddjob.property
	 * @oddjob.description Allow a number of named styles to be set for
	 * the book. See {@link StyleBean}.
	 * @oddjob.required No.
	 */
	private final StyleFactoryRegistry styles = new StyleFactoryRegistry();
	
	/**
	 * 
	 * @oddjob.property of
	 * @oddjob.description The child layouts of this sheet.
	 * @oddjob.required No, but pointless if missing.
	 * 
	 * @param index 0 based index.
	 * @param child The child, null will remove the child for the given index.
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
		
		// Note that we need this to close the book at the end, so the
		// book can be written out.
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
		
	/**
	 * Setter for mapped styles.
	 * 
	 * @param styleName The name of the styles.
	 * @param styleBean The style bean. If null then the style of the
	 * given name will be removed.
	 */
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
