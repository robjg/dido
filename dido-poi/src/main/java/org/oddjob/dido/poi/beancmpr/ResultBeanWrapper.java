package org.oddjob.dido.poi.beancmpr;

import java.util.LinkedHashMap;
import java.util.Map;

import org.oddjob.arooa.reflect.PropertyAccessor;

public class ResultBeanWrapper {

	public static final String RESULT_TYPE_PROPERTY = "resultType";
	
	public static final String KEYS_PROPERTY = "keys";
	
	public static final String COMPARISONS_PROPERTY = "comparisons";
	
	private final int resultType;
	
	private final Map<String, Object> keys;
	
	private final Map<String, ComparisonBeanWrapper> comparisons;
	
	@SuppressWarnings("unchecked")
	public ResultBeanWrapper(PropertyAccessor accessor, Object bean) {

		this.resultType = (Integer) accessor.getProperty(
				bean, RESULT_TYPE_PROPERTY);
		
		this.keys = (Map<String, Object>) accessor.getProperty(
				bean, KEYS_PROPERTY);
		
		Map<String, Object> comparisonBeans = (Map<String, Object>) 
				accessor.getProperty(bean, COMPARISONS_PROPERTY);
	
		this.comparisons = new LinkedHashMap<String, ComparisonBeanWrapper>();
		
		for (Map.Entry<String, Object> entry : comparisonBeans.entrySet()) {
			
			this.comparisons.put(entry.getKey(), 
					new ComparisonBeanWrapper(accessor, entry.getValue()));
		}
	}
	
	public int getResultType() {
		return resultType;
	}
	
	public Map<String, Object> getKeys() {
		return keys;
	}
	
	public Map<String, ComparisonBeanWrapper> getComparisons() {
		return comparisons;
	}
}
