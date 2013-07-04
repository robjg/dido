package org.oddjob.dido.poi.beancmpr;

import org.oddjob.arooa.reflect.PropertyAccessor;

public class ComparisonBeanWrapper {

	public static final String COMPARISON_X_PROPERTY = "x";
	
	public static final String COMPARISON_Y_PROPERTY = "y";

	public static final String COMPARISON_RESULT_PROPERTY = "result";
	
	private final int result;
	
	private final Object x;
	
	private final Object y;
	
	public ComparisonBeanWrapper(PropertyAccessor accessor, Object comparison) {
		
		this.result = (Integer) accessor.getProperty(
				comparison, COMPARISON_RESULT_PROPERTY);
		
		this.x =  accessor.getProperty(comparison, COMPARISON_X_PROPERTY);
		
		this.y =  accessor.getProperty(comparison, COMPARISON_Y_PROPERTY);
	}
	
	public Object getX() {
		return x;
	}
	
	public Object getY() {
		return y;
	}
	
	public int getResult() {
		return result;
	}
}
