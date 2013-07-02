package org.oddjob.dido.poi;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReader;
import org.oddjob.dido.DataWriter;
import org.oddjob.dido.Layout;
import org.oddjob.dido.layout.LayoutNode;
import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;

public class DataBook 
extends LayoutNode {
	
	private static final Logger logger = Logger.getLogger(DataBook.class);
	
	private SpreadsheetVersion version;
	
	private final Map<String, StyleBean> styles
		 = new LinkedHashMap<String, StyleBean>();
	
	public void setOf(int index, Layout child) {
		addOrRemoveChild(index, child);
	}
	
	@Override
	public DataReader readerFor(DataIn dataIn) throws DataException {
		
		StreamIn data = dataIn.provideDataIn(StreamIn.class);
		
		BookIn book = null;
		try {
			book = new PoiBookIn(data.inputStream());
		} catch (InvalidFormatException e) {
			throw new DataException(e);
		} catch (IOException e) {
			throw new DataException(e);
		}
		
		logger.debug("Created workbook from input stream.");
		
		return nextReaderFor(book);
	}
	
	@Override
	public DataWriter writerFor(DataOut dataOut) throws DataException {
		
		StreamOut data = dataOut.provideDataOut(StreamOut.class);
		
		StyleProviderFactory styleFactory;
		if (styles == null) {
			styleFactory = new DefaultStyleFactory();
		}
		else {
			styleFactory = new CompositeStyleFactory(
					new BeanStyleFactory(styles), new DefaultStyleFactory());
		}
		
		
		final BookOut bookOut = new PoiBookOut(data.outputStream(), version, 
				styleFactory);
		
		logger.debug("Created empty workbook.");
		
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
	
	public SpreadsheetVersion getVersion() {
		return version;
	}

	public void setVersion(SpreadsheetVersion version) {
		this.version = version;
	}
	
	public StyleBean getStyles(String styleName) {
		return styles.get(styleName);
	}
	
	public void setStyles(String styleName, StyleBean styleBean) {
		if (styleBean == null) {
			styles.remove(styleName);
		}
		else {
			styles.put(styleName, styleBean);
		}
	}
	
	@Override
	public String toString() {
		String name = getName();
		return getClass().getSimpleName() + 
			(name == null ? "" : " " + name);
	}

}
