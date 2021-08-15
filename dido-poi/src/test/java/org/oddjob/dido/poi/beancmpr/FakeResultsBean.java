package org.oddjob.dido.poi.beancmpr;

import java.util.LinkedHashMap;
import java.util.Map;

public class FakeResultsBean {

	public static class FakeComparison {
		
		private int result;
		
		private Object x;
		
		private Object y;
		
		FakeComparison(Object x, Object y, int result) {
			this.x = x;
			this.y = y;
			this.result = result;
		}
		
		public int getResult() {
			return result;
		}
		
		public Object getX() {
			return x;
		}
		
		public Object getY() {
			return y;
		}
	}
	
	public static class Builder {
		
		private FakeResultsBean bean = new FakeResultsBean();

		public Builder(int resultType) {
			bean.resultType = resultType;
		}

		public Builder addKey(String name, Object key) {
			bean.keys.put(name, key);
			return this;
		}
		
		public Builder addComparison(String name, Object x, Object y, int result) {
			bean.comparisons.put(name, new FakeComparison(x, y, result));
			return this;
		}
		
		public Object build() {
			return bean;
		}
	}
	
	private int resultType;
	
	private final Map<String, Object> keys = 
			new LinkedHashMap<String, Object>();
	
	private final Map<String, Object> comparisons = 
			new LinkedHashMap<String, Object>();
	
	public int getResultType() {
		return resultType;
	}
	
	public Map<String, Object> getKeys() {
		return keys;
	}
	
	public Map<String, Object> getComparisons() {
		return comparisons;
	}
}
