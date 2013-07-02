package org.oddjob.dido.poi;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class BeanStyleFactory implements StyleProviderFactory {
	
	private final Map<String, StyleBean> styles;
	
	public BeanStyleFactory(Map<String, StyleBean> styles) {
		this.styles = styles;
	}
	
	@Override
	public StyleProvider providerFor(final Workbook workbook) {
		
		final Map<String, CellStyle> cellStyles = 
			new HashMap<String, CellStyle>();
		
		for (Map.Entry<String, StyleBean> entry : styles.entrySet()) {
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
