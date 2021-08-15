package org.oddjob.dido.poi.data;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataOut;
import org.oddjob.dido.UnsupportedDataOutException;
import org.oddjob.dido.poi.SheetOut;
import org.oddjob.dido.poi.style.StyleProvider;

public class PoiSheetOut implements SheetOut {

	private final Sheet sheet;
	
	private final StyleProvider styleProvider;
	
	public PoiSheetOut(Sheet sheet) {
		this(sheet, new StyleProvider() {
			
			@Override
			public CellStyle styleFor(String style) {
				return null;
			}
		});
	}	
	
	public PoiSheetOut(Sheet sheet, StyleProvider styleProvider) {
		if (sheet == null) {
			throw new NullPointerException("Sheet.");
		}
		
		if (styleProvider == null) {
			throw new NullPointerException("Style Provider.");
		}
		
		this.sheet = sheet;
		this.styleProvider = styleProvider;
	}
	
	@Override
	public CellStyle styleFor(String style) {
		return styleProvider.styleFor(style);
	}
	
	@Override
	public Sheet getTheSheet() {
		return sheet;
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
		
		throw new UnsupportedDataOutException(this.getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}
	
	@Override
	public void close() throws DataException {
		// Override by subclasses that need to close the book.
	}
}
