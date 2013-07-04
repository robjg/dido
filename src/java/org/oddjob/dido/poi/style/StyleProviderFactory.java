package org.oddjob.dido.poi.style;

import org.apache.poi.ss.usermodel.Workbook;

public interface StyleProviderFactory {

	public StyleProvider providerFor(Workbook workbook);
}
