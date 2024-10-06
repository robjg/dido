package dido.poi.style;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CompositeStyleFactoryTest {

	@Test
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
