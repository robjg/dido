package org.oddjob.dido.poi;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.oddjob.dido.poi.style.BeanStyleFactory;
import org.oddjob.dido.poi.style.CompositeStyleFactory;
import org.oddjob.dido.poi.style.DefaultStyleFactory;
import org.oddjob.dido.poi.style.StyleBean;
import org.oddjob.dido.poi.style.StyleProvider;
import org.oddjob.dido.poi.style.StyleProviderFactory;

import junit.framework.TestCase;

public class CompositeStyleFactoryTest extends TestCase {

	public void testTwoFactories() {
		
		StyleBean percentage = new StyleBean();
		percentage.setFormat("#0.00%");
		
		Map<String, StyleBean> styles = new HashMap<String, StyleBean>();
		styles.put("percentage", percentage);
		
		StyleProviderFactory test = new CompositeStyleFactory(
				new BeanStyleFactory(styles), new DefaultStyleFactory());
		
		StyleProvider provder = test.providerFor(new HSSFWorkbook());

		assertNotNull(provder.styleFor("percentage"));
	}
}
