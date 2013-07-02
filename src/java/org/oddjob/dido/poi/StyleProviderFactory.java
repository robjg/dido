package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.Workbook;

public interface StyleProviderFactory {

	public StyleProvider providerFor(Workbook workbook);
}
