package org.oddjob.poi;

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
import org.oddjob.dido.UnsupportedeDataOutException;

public class PoiBookOut implements BookOut {
	private static final Logger logger = Logger.getLogger(PoiBookOut.class);
	
	private final Workbook workbook;
	
	private final OutputStream output;
	
	private final StyleProvider styleProvider;
	
	public PoiBookOut(OutputStream output) {
		this(output, null, new DefaultStyleFactory());
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
	public boolean flush() throws DataException {
		try {
			workbook.write(output);
		} catch (IOException e) {
			throw new DataException(e);
		}
		logger.debug("Wrote workbook of " + workbook.getNumberOfSheets() +
				" sheet out.");
		return true;
	}
	
	@Override
	public CellStyle styleFor(String styleName) {
		return styleProvider.styleFor(styleName);
	}
	
	@Override
	public boolean hasData() {
		throw new RuntimeException("To Do.");
	}
	
	@Override
	public <T> T toValue(Class<T> type) {
		throw new RuntimeException("To Do.");
	}
	
	
	@Override
	public <T extends DataOut> T provide(Class<T> type) throws DataException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		throw new UnsupportedeDataOutException(this.getClass(), type);
	}
}
