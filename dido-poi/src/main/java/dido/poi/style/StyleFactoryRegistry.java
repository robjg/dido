package dido.poi.style;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Create a {@link StyleProvider} from {@link StyleFactory}s.
 * 
 * @author rob
 *
 */
public class StyleFactoryRegistry implements StyleProviderFactory {
	
	private final Map<String, StyleFactory> styles =
			new LinkedHashMap<String, StyleFactory>();
	
	public void registerStyle(String name, StyleFactory factory) {
		
		styles.put(name, factory);
	}
	
	public void removeStyle(String name) {
		
		styles.remove(name);
	}
	
	public boolean hasStyles() {

		return !styles.isEmpty();
	}

	@Override
	public StyleProvider providerFor(final Workbook workbook) {
		
		final Map<String, CellStyle> cellStyles = 
			new HashMap<String, CellStyle>();
		
		for (Map.Entry<String, StyleFactory> entry : styles.entrySet()) {
			CellStyle style = entry.getValue().createStyle(workbook);
			cellStyles.put(entry.getKey(), style);
		}
		
		return new StyleProvider() {
			@Override
			public CellStyle styleFor(String styleName) {
				return cellStyles.get(styleName);
			}				
		};
	}
}
