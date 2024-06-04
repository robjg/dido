package dido.poi.style;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * A composite {@link StyleProvider}. Later added styles of the same
 * name override previous styles.
 * 
 * @author rob
 *
 */
public class CompositeStyleProvider implements StyleProvider {

	private final List<StyleProvider> styleProviders = 
			new ArrayList<StyleProvider>();
	
	public CompositeStyleProvider(StyleProvider... providers) {
		for (StyleProvider provider : providers) {
			this.styleProviders.add(0, provider);
		}
	}
	
	public void addStyleProvider(StyleProvider styleProvider) {
		styleProviders.add(0, styleProvider);
	}
	
	@Override
	public CellStyle styleFor(String styleName) {
		for (StyleProvider provider : styleProviders) {
			CellStyle style = provider.styleFor(styleName);
			if (style != null) {
				return style;
			}
		}
		return null;
	}
}
