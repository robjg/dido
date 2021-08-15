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
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.DataReadJob;
import org.oddjob.dido.DataWriteJob;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.poi.BookIn;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.SheetIn;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.layouts.DataBook;
import org.oddjob.dido.poi.style.CompositeStyleProvider;
import org.oddjob.dido.poi.style.DefaultStyleProivderFactory;
import org.oddjob.dido.poi.style.StyleProviderFactory;

/**
 * @oddjob.description A source or sink of data that is a Microsoft 
 * Excel Spreadsheet.
 * <p>
 * This will generally be used to set the data property of a 
 * {@link DataReadJob} or {@link DataWriteJob} when the layout definition
 * is a {@link DataBook}.
 * 
 * @author rob
 *
 */
public class PoiWorkbook implements ArooaSessionAware, DataIn, DataOut {

	private static final Logger logger = Logger.getLogger(PoiBookOut.class);
	
	/** The workbook created or read. */
	private Workbook workbook;
	
	/** Used to convert input and output to streams. */
	private ArooaConverter arooaConverter;
	
	/**
	 * @oddjob.property
	 * @oddjob.description An input type (i.e. file) that is an Excel 
	 * Workbook.
	 * @oddjob.required For reading yes but optional for writing.
	 */
	private ArooaValue input;
	
	/**
	 * @oddjob.property
	 * @oddjob.description An output type (i.e. file) that is an Excel 
	 * Workbook.
	 * @oddjob.required For writing yes, ignored for reading.
	 */
	private ArooaValue output;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The version of Excel to create. EXCEL97 or
	 * EXCEL2007. 
	 * @oddjob.required No. Default to EXCEL2007.
	 */
	private SpreadsheetVersion version;
	
	@Override
	public boolean isWrittenTo() {
		throw new UnsupportedOperationException();
	}

	@ArooaHidden
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
		
		if (type.isAssignableFrom(SheetIn.class)) {
			return new PoiBookIn().provideDataIn(type);
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
	
	@Override
	public <T extends DataOut> T provideDataOut(Class<T> type)
	throws DataException {
		
		if (type.isAssignableFrom(BookOut.class)) {
			return type.cast(new RootPoiBookOut());
		}
		
		if (type.isAssignableFrom(SheetOut.class)) {
			final PoiBookOut bookOut = new RootPoiBookOut();
			return type.cast(new PoiSheetOut(
					bookOut.createSheet(null), bookOut) {
				@Override
				public void close() throws DataException {
					bookOut.close();
				}
			});
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
					logger.info("Read workbook from [" + input + "]");
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
			
			if (type.isAssignableFrom(SheetIn.class)) {
				return type.cast(new PoiSheetIn(nextSheet()));
			}
			
			throw new UnsupportedDataInException(this.getClass(), type);
		}
	}	
	
	class PoiBookOut implements BookOut {
		
		CompositeStyleProvider styleProviders; 
				
		protected PoiBookOut() {
			
		}
		
		public PoiBookOut(PoiBookOut original) {
			styleProviders = original.styleProviders;
		}

		@Override
		public Sheet createSheet(String name) {
			if (name == null) {
				return workbook.createSheet();
			}
			else {
				Sheet sheet = workbook.getSheet(name);
				if (sheet == null) {
					sheet = workbook.createSheet(name);
					workbook.setSheetName(
							workbook.getSheetIndex(sheet), name);
				}
				return sheet;
			}
		}
		
		@Override
		public void addStyleFactory(StyleProviderFactory styleProviderFactory) {
			styleProviders.addStyleProvider(
					styleProviderFactory.providerFor(workbook));
		}
		
		@Override
		public void close() throws DataException {
			
			// Do nothing.
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
				return type.cast(new PoiBookOut(this));
			}
			
			if (type.isAssignableFrom(SheetOut.class)) {
				return type.cast(new PoiSheetOut(createSheet(null), 
						new PoiBookOut(this)));
			}
			
			throw new UnsupportedDataOutException(this.getClass(), type);
		}
	}
	
	class RootPoiBookOut extends PoiBookOut {
		
		public RootPoiBookOut() {
			
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
				logger.info("Read workbook from [" + input + "]");
			}
			else {
				logger.info("Created empty workbook.");
				
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
			}
			
	 		styleProviders = new CompositeStyleProvider(
	 				new DefaultStyleProivderFactory().providerFor(workbook));
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
				
				logger.info("Wrote workbook of " + workbook.getNumberOfSheets() +
						" sheet(s) to " + output);
			}
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
