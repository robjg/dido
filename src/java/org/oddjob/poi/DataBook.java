package org.oddjob.poi;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.oddjob.dido.AbstractParent;
import org.oddjob.dido.DataException;
import org.oddjob.dido.WhereNextIn;
import org.oddjob.dido.WhereNextOut;
import org.oddjob.dido.stream.StreamIn;
import org.oddjob.dido.stream.StreamOut;

public class DataBook 
extends AbstractParent<StreamIn, BookIn, StreamOut, BookOut> {
	
	private static final Logger logger = Logger.getLogger(DataBook.class);
	
	private SpreadsheetVersion version;
	
	private final Map<String, StyleBean> styles
		 = new LinkedHashMap<String, StyleBean>();
	
	@Override
	public WhereNextIn<BookIn> in(
			StreamIn data) throws DataException {
		
		BookIn book = null;
		try {
			book = new PoiBookIn(data.getStream());
		} catch (InvalidFormatException e) {
			throw new DataException(e);
		} catch (IOException e) {
			throw new DataException(e);
		}
		
		logger.debug("Created workbook from input stream.");
		return new WhereNextIn<BookIn>(childrenToArray(), book);
	}
	
	@Override
	public WhereNextOut<BookOut> out(
			StreamOut data) throws DataException {
		
		StyleProviderFactory styleFactory;
		if (styles == null) {
			styleFactory = new DefaultStyleFactory();
		}
		else {
			styleFactory = new CompositeStyleFactory(
					new BeanStyleFactory(styles), new DefaultStyleFactory());
		}
		
		
		BookOut book = new PoiBookOut(data.getStream(), version, 
				styleFactory);
		
		logger.debug("Created empty workbook.");
		return new WhereNextOut<BookOut>(childrenToArray(), book);
	}

	@Override
	public void flush(StreamOut data, BookOut childData) throws DataException {
		childData.flush();
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
