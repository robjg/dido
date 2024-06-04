package dido.poi.style;

import dido.poi.style.*;
import junit.framework.TestCase;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class CompositeStyleFactoryTest extends TestCase {

	public void testTwoFactories() {
		
		StyleBean percentage = new StyleBean();
		percentage.setFormat("#0.00%");
		
		StyleFactoryRegistry styles = new StyleFactoryRegistry();
		
		styles.registerStyle("percentage", percentage);
		
		StyleProviderFactory test = new CompositeStyleFactory(
				styles, new DefaultStyleProivderFactory());
		
		StyleProvider provder = test.providerFor(new HSSFWorkbook());

		assertNotNull(provder.styleFor("percentage"));
	}
}
