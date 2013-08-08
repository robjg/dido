package org.oddjob.dido.poi.data;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.style.CompositeStyleProvider;
import org.oddjob.dido.poi.style.DefaultStyleProivderFactory;
import org.oddjob.dido.poi.style.StyleProviderFactory;

public class PoiWorkbook implements ArooaSessionAware, DataIn, DataOut {

	private static final Logger logger = Logger.getLogger(PoiBookOut.class);
	
	private Workbook workbook;
	
	private ArooaConverter arooaConverter;
	
	private ArooaValue input;
	
	private ArooaValue output;
	
	private SpreadsheetVersion version;
	
	@Override
	public boolean isWrittenTo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setArooaSession(ArooaSession session) {
		this.arooaConverter = session.getTools().getArooaConverter();
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type)
	throws DataException {
		
		if (type.isAssignableFrom(BookIn.class)) {
			return type.cast(new PoiBookIn());
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
	throws DataException {
		
		if (type.isAssignableFrom(BookOut.class)) {
			return type.cast(new PoiBookOut());
		}
		
		throw new UnsupportedDataOutException(this.getClass(), type);
	}
	
	class PoiBookIn implements BookIn {

		private int sheet = 0;
		
		public PoiBookIn() {
	
			if (input != null) {

				if (arooaConverter == null) {
					throw new NullPointerException("No converter. Session not set?");
				}
				
				try {
					InputStream inputStream = arooaConverter.convert(input, InputStream.class);
					workbook = WorkbookFactory.create(inputStream);
				} 
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			if (workbook == null) {
				throw new NullPointerException("No Input For Workbook.");
			}
			
		}
		
		@Override
		public Sheet getSheet(String sheetName) {
			return workbook.getSheet(sheetName);
		}
		
		@Override
		public Sheet nextSheet() {
			return workbook.getSheetAt(sheet++);
		}	
		
		@Override
		public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {

			if (type.isInstance(this)) {
				return type.cast(this);
			}
			
			return new PoiSheetIn(nextSheet()).provideDataIn(type);
		}
	}	
	
	class PoiBookOut implements BookOut {
		
		private final CompositeStyleProvider styleProviders; 
				
		public PoiBookOut() {
			
			if (input != null) {
	
				if (arooaConverter == null) {
					throw new NullPointerException("No converter. Session not set?");
				}
				
				try {
					InputStream inputStream = arooaConverter.convert(input, InputStream.class);
					workbook = WorkbookFactory.create(inputStream);
				} 
				catch (Exception e) {
					throw new RuntimeException(e);
				}
				logger.debug("Created workbook from [" + input + "]");
			}
			else {
				logger.debug("Created empty workbook.");
			}
			
			if (version == null) {
				version = SpreadsheetVersion.EXCEL2007;
			}
			
	 		switch (version) {
			case EXCEL97:
				workbook = new HSSFWorkbook();
				break;
			default:
				workbook = new XSSFWorkbook();
				break;
			}
	 		
	 		styleProviders = new CompositeStyleProvider(
	 				new DefaultStyleProivderFactory().providerFor(workbook));
		}
		
		@Override
		public Sheet createSheet(String name) {
			if (name == null) {
				return workbook.createSheet();
			}
			else {
				return workbook.createSheet(name);
			}
		}
		
		@Override
		public void addStyleFactory(StyleProviderFactory styleProviderFactory) {
			styleProviders.addStyleProvider(
					styleProviderFactory.providerFor(workbook));
		}
		
		@Override
		public void close() throws DataException {
			
			if (output != null) {

				if (arooaConverter == null) {
					throw new NullPointerException("No converter. Session not set?");
				}
				
				try {
					OutputStream outputStream = arooaConverter.convert(output, OutputStream.class);
					workbook.write(outputStream);
				} 
				catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				logger.debug("Wrote workbook of " + workbook.getNumberOfSheets() +
						" sheet out.");
			}
		}
		
		@Override
		public CellStyle styleFor(String styleName) {

			return styleProviders.styleFor(styleName);
		}
		
		@Override
		public boolean isWrittenTo() {
			throw new RuntimeException("To Do.");
		}
			
		@Override
		public <T extends DataOut> T provideDataOut(Class<T> type) throws DataException {
	
			if (type.isInstance(this)) {
				return type.cast(this);
			}
			
			return new PoiSheetOut(createSheet(null), this).provideDataOut(type);
		}
	}

	public ArooaValue getInput() {
		return input;
	}

	public void setInput(ArooaValue input) {
		this.input = input;
	}

	public ArooaValue getOutput() {
		return output;
	}

	public void setOutput(ArooaValue output) {
		this.output = output;
	}

	public SpreadsheetVersion getVersion() {
		return version;
	}

	public void setVersion(SpreadsheetVersion version) {
		this.version = version;
	}
}
