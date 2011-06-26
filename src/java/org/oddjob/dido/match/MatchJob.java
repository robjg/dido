package org.oddjob.dido.match;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.dido.match.beans.BeanCreatingResultProcessor;
import org.oddjob.dido.match.beans.ComparersByProperty;
import org.oddjob.dido.match.beans.ComparersByPropertyOrType;
import org.oddjob.dido.match.comparers.ComparersByType;
import org.oddjob.dido.match.comparers.MultiItemComparison;
import org.oddjob.dido.match.matchables.BeanMatchableFactory;
import org.oddjob.dido.match.matchables.MatchableFactory;
import org.oddjob.dido.match.matchables.MatchableGroup;
import org.oddjob.dido.match.matchables.MatchableMatchProcessor;
import org.oddjob.dido.match.matchables.OrderedMatchablesComparer;
import org.oddjob.dido.match.matchables.SortedBeanMatchables;
import org.oddjob.dido.match.matchables.UnsortedBeanMatchables;

/**
 * @oddjob.description A job that takes two streams of beans and
 * attempts to match the beans according to their properties.
 * 
 * @oddjob.example
 * 
 * A simple example.
 * 
 * {@oddjob.xml.resource org/oddjob/dido/match/MatchExample2.xml}
 * 
 * 
 * @author rob
 *
 */
public class MatchJob implements ArooaSessionAware, Runnable {

	private static final Logger logger = Logger.getLogger(MatchJob.class);
	
	/**
	 * @oddjob.property
	 * @oddjob.description The key properties. A comma separated list of
	 * the properties of the beans that will be used to decide which can
	 * be matched.
	 * @oddjob.required No.
	 */
	private String[] keyProperties;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The value properties. A comma separated list of
	 * the properties of the beans that will be used to match the two 
	 * beans against each other.
	 * @oddjob.required No.
	 */
	private String[] valueProperties;
	
	/**
	 * @oddjob.property
	 * @oddjob.description Other properties. A comma separated list of
	 * the properties of the beans that aren't used in the match but
	 * will be passed through to the results.
	 * @oddjob.required No.
	 */
	private String[] otherProperties;
	
	/**
	 * @oddjob.property
	 * @oddjob.description Used to specify additional comparers to the
	 * default ones. If a comparer is specified for an existing type it
	 * takes precedence.
	 * @oddjob.required No.
	 */
	private ComparersByType comparersByType;
	
	/**
	 * @oddjob.property
	 * @oddjob.description Used to specify comparers that will be used for 
	 * particular properties of the beans.
	 * @oddjob.required No.
	 */
	private ComparersByProperty comparersByProperty;
	
	/**
	 * @oddjob.property
	 * @oddjob.description A collection that result beans will be added to.
	 * @oddjob.required No.
	 */
	private Collection<Object> out;
	
	
	private PropertyAccessor accessor;
	
	/**
	 * @oddjob.property
	 * @oddjob.description A source of beans.
	 * @oddjob.required Yes.
	 */
	private Iterable<?> inX;
	
	/**
	 * @oddjob.property
	 * @oddjob.description A source of beans.
	 * @oddjob.required Yes.
	 */
	private Iterable<?> inY;
	
	/**
	 * @oddjob.property
	 * @oddjob.description This will be prefixed to property names in
	 * the result bean.
	 * @oddjob.required No.
	 */
	private String xPropertyPrefix;
	
	/**
	 * @oddjob.property
	 * @oddjob.description This will be prefixed to property names in
	 * the result bean.
	 * @oddjob.required No.
	 */
	private String yPropertyPrefix;
	
	private boolean sorted;
	
	@Override
	public void setArooaSession(ArooaSession session) {
		this.accessor = session.getTools().getPropertyAccessor();
	}
	
	@Override
	final public void run() {
		
		if (inX == null) {
			throw new NullPointerException("No X");
		}
		if (inY == null) {
			throw new NullPointerException("No Y");
		}
		
		MatchableMatchProcessor resultsListener = null;
		
		if (out != null) {
			resultsListener = new BeanCreatingResultProcessor(
					accessor,
					xPropertyPrefix, 
					yPropertyPrefix, 
					out);
		}
				
		MatchDefinition definition = new SimpleMatchDefinition(
				getKeyProperties(), getValueProperties(), getOtherProperties());
		
		BeanMatchableFactory factory = new BeanMatchableFactory(
				definition, accessor);
		
		ComparersByPropertyOrType comparerProvider =
			new ComparersByPropertyOrType(comparersByProperty, comparersByType);
			
		OrderedMatchablesComparer rec = new OrderedMatchablesComparer(
				accessor,
				comparerProvider,
				resultsListener);
		
		MultiItemComparison comparison = 
			rec.compare(getIterableMatchables(inX, factory), 
				getIterableMatchables(inY, factory));		
		
		logger.info("Xs Missing " + comparison.getXsMissing() +
				", Ys Missing " + comparison.getYsMissing() + 
				", Different " + comparison.getDifferent() + 
				", Same " + comparison.getSame());
	}
	
	
	private Iterable<MatchableGroup> getIterableMatchables(
			Iterable<?> in, MatchableFactory<Object> factory) {
		if (sorted) {
			return new SortedBeanMatchables<Object>(in, factory);
		}
		else {
			return new UnsortedBeanMatchables<Object>(in, factory);
		}
	}
	
	public Iterable<?> getInX() {
		return inX;
	}

	public void setInX(Iterable<?> x) {
		this.inX = x;
	}

	public Iterable<?> getInY() {
		return inY;
	}

	public void setInY(Iterable<?> y) {
		this.inY = y;
	}

	public String[] getValueProperties() {
		return valueProperties;
	}

	@ArooaAttribute
	public void setValueProperties(String[] matchProperties) {
		this.valueProperties = matchProperties;
	}

	public String[] getKeyProperties() {
		return keyProperties;
	}

	@ArooaAttribute
	public void setKeyProperties(String[] keys1) {
		this.keyProperties = keys1;
	}

	public String[] getOtherProperties() {
		return otherProperties;
	}

	@ArooaAttribute
	public void setOtherProperties(String[] others) {
		this.otherProperties = others;
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

	public Collection<Object> getOut() {
		return out;
	}

	public void setOut(Collection<Object> out) {
		this.out = out;
	}

	public String getxPropertyPrefix() {
		return xPropertyPrefix;
	}

	public void setxPropertyPrefix(String xPropertyPrefix) {
		this.xPropertyPrefix = xPropertyPrefix;
	}

	public String getyPropertyPrefix() {
		return yPropertyPrefix;
	}

	public void setyPropertyPrefix(String yPropertyPrefix) {
		this.yPropertyPrefix = yPropertyPrefix;
	}

	public boolean isSorted() {
		return sorted;
	}

	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}
}
