package dido.poi.style;

import org.apache.poi.ss.usermodel.Workbook;

public class CompositeStyleFactory implements StyleProviderFactory {

	private final StyleProviderFactory[] factories;
	
	public CompositeStyleFactory(StyleProviderFactory... factories) {
		
		this.factories = factories;
	}
	
	@Override
	public StyleProvider providerFor(Workbook workbook) {
		
		
		CompositeStyleProvider providers = new CompositeStyleProvider();
		
		for (int i = 0; i < factories.length; ++i) {
			providers.addStyleProvider(factories[i].providerFor(workbook));
		}

		return providers;
	}
}
