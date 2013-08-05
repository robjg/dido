package org.oddjob.dido.poi.data;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.poi.BookOut;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.style.DefaultStyleFactory;
import org.oddjob.dido.poi.style.StyleProvider;
import org.oddjob.dido.poi.style.StyleProviderFactory;

public class PoiBookOut implements BookOut {
	private static final Logger logger = Logger.getLogger(PoiBookOut.class);
	
	private final Workbook workbook;
	
	private final OutputStream output;
	
	private final StyleProvider styleProvider;
	
	public PoiBookOut(OutputStream output) {
		this(output, null, new DefaultStyleFactory());
	}
	
	@Override
	public void addStyleFactory(StyleProviderFactory styleProviderFactory) {
		// TODO Auto-generated method stub
		
	}
	
	public PoiBookOut(OutputStream output, SpreadsheetVersion version,
			StyleProviderFactory styleProviderFactory) {
		this.output = output;
		
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
 		
 		styleProvider = styleProviderFactory.providerFor(workbook);
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
	public void close() throws DataException {
		
		try {
			workbook.write(output);
		} 
		catch (IOException e) {
			throw new DataException(e);
		}
		
		logger.debug("Wrote workbook of " + workbook.getNumberOfSheets() +
				" sheet out.");
	}
	
	@Override
	public CellStyle styleFor(String styleName) {
		return styleProvider.styleFor(styleName);
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
		
		if (type.isAssignableFrom(SheetOut.class)) {
			return type.cast(new PoiSheetOut(createSheet(null), this));
		}
		
		throw new UnsupportedDataOutException(this.getClass(), type);
	}
}
