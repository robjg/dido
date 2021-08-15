package org.oddjob.dido.poi.style;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Something that can create an {@link StyleProvider}.
 * 
 * @author rob
 *
 */
public interface StyleProviderFactory {

	public StyleProvider providerFor(Workbook workbook);
}
