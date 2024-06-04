package dido.poi.style;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Something that is able to provide a style for the given name.
 * 
 * @author rob
 *
 */
public interface StyleProvider {

	/**
	 * Provide a style.
	 * 
	 * @param styleName The name of the style.
	 * @return The style. Will be null if one doesn't exist for that name.
	 */
	public CellStyle styleFor(String styleName);
	
}
