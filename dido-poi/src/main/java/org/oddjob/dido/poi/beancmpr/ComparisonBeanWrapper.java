package org.oddjob.dido.poi.beancmpr;

import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class ComparisonBeanWrapper {

	public static final String COMPARISON_X_PROPERTY = "x";
	
	public static final String COMPARISON_Y_PROPERTY = "y";

	public static final String COMPARISON_RESULT_PROPERTY = "result";
	
	private final int result;
	
	private final Object x;
	
	private final Class<?> typeOfX;
	
	private final Object y;
	
	private final Class<?> typeOfY;
	
	public ComparisonBeanWrapper(PropertyAccessor accessor, Object comparison) {
		
		this.result = (Integer) accessor.getProperty(
				comparison, COMPARISON_RESULT_PROPERTY);
		
		BeanOverview overview = accessor.getBeanOverview(
				comparison.getClass());
		
		// In theory X and Y should be the same type...
		this.typeOfX = overview.getPropertyType(COMPARISON_X_PROPERTY);
		
		this.typeOfY = overview.getPropertyType(COMPARISON_Y_PROPERTY);
		
		this.x =  accessor.getProperty(comparison, COMPARISON_X_PROPERTY);
		
		this.y =  accessor.getProperty(comparison, COMPARISON_Y_PROPERTY);
	}
	
	public Object getX() {
		return x;
	}
	
	public Class<?> getTypeOfX() {
		return typeOfX;
	}
	
	public Object getY() {
		return y;
	}
	
	public Class<?> getTypeOfY() {
		return typeOfY;
	}
	
	public int getResult() {
		return result;
	}
	
}
