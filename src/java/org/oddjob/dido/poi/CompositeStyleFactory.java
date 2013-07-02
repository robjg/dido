package org.oddjob.dido.poi;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class CompositeStyleFactory implements StyleProviderFactory {

	private final StyleProviderFactory[] factories;
	
	public CompositeStyleFactory(StyleProviderFactory... factories) {
		
		this.factories = factories;
	}
	
	@Override
	public StyleProvider providerFor(Workbook workbook) {
		
		final StyleProvider[] providers = new StyleProvider[factories.length];

		for (int i = 0; i < providers.length; ++i) {
			providers[i] = factories[i].providerFor(workbook);
		}
		
		return new StyleProvider() {
			
			@Override
			public CellStyle styleFor(String styleName) {
				for (StyleProvider provider : providers) {
					CellStyle style = provider.styleFor(styleName);
					if (style != null) {
						return style;
					}
				}
				return null;
			}
		};
	}
}
