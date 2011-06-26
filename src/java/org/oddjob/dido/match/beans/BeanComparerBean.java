package org.oddjob.dido.match.beans;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.dido.match.Comparer;
import org.oddjob.dido.match.comparers.ComparersByType;

/**
 * A bean that wraps a {@link BeanComparer}.
 * 
 * @author rob
 *
 */
public class BeanComparerBean implements ArooaSessionAware, ValueFactory<Comparer<Object>> {
	
	private String[] matchProperties;
	
	private PropertyAccessor accessor;
	
	private ComparersByType comparersByType;
	
	private ComparersByProperty comparersByProperty;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.accessor = session.getTools().getPropertyAccessor();
	}
	

	@Override
	public Comparer<Object> toValue() throws ArooaConversionException {
		
		ComparersByPropertyOrType comparerProvider =
			new ComparersByPropertyOrType(comparersByProperty, comparersByType);
					
		return new BeanComparer(matchProperties, accessor, comparerProvider);
	}


	public String[] getMatchProperties() {
		return matchProperties;
	}


	public void setMatchProperties(String[] matchProperties) {
		this.matchProperties = matchProperties;
	}


	public ComparersByType getComparersByType() {
		return comparersByType;
	}


	public void setComparersByType(ComparersByType comparersByType) {
		this.comparersByType = comparersByType;
	}


	public ComparersByProperty getComparersByProperty() {
		return comparersByProperty;
	}


	public void setComparersByProperty(ComparersByProperty comparersByProperty) {
		this.comparersByProperty = comparersByProperty;
	}

}
