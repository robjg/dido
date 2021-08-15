package org.oddjob.dido.poi.data;

import org.apache.poi.ss.usermodel.Sheet;
import org.oddjob.dido.DataException;
import org.oddjob.dido.DataIn;
import org.oddjob.dido.UnsupportedDataInException;
import org.oddjob.dido.poi.SheetIn;

public class PoiSheetIn implements SheetIn {

	private final Sheet sheet;
	
	public PoiSheetIn(Sheet sheet) {
		this.sheet = sheet;
	}
	
	@Override
	public Sheet getTheSheet() {
		return sheet;
	}
	
	@Override
	public <T extends DataIn> T provideDataIn(Class<T> type) throws DataException {

		if (type.isInstance(this)) {
			return type.cast(this);
		}
		
		throw new UnsupportedDataInException(this.getClass(), type);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + sheet.getSheetName();
	}
}
