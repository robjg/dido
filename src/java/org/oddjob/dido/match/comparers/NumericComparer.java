package org.oddjob.dido.match.comparers;

import org.oddjob.dido.match.Comparer;

/**
 * A {@link Comparer} for numbers that supports tolerances and provides
 * the comparison as a difference between the numbers and as a percentage 
 * change between numbers.
 * <p>
 * The change is considered as going from x to y. If x is 200 and y is 190, the
 * delta is -10, and the percentage chnage is 5%.
 * <p>
 * Two numbers are only considered different if their delta is more
 * the given tolerance or more. The tolerance may be specified as either a 
 * delta tolerance number that two numbers must differ more than, 
 * or a minimum percentage change that the two number must exceed.
 * <p>
 * The comparison is only unequal if the difference between the two numbers
 * is greater than both tolerances. Both tolerances default to zero.
 * <p>
 * If either or both input number is null, the result of the compare is null.
 * 
 * @author Rob
 *
 */
public class NumericComparer implements Comparer<Number> {

	private double deltaTolerance;
	
	private String deltaFormat;
	
	private double percentageTolerance;
	
	private String percentageFormat;
	
	public NumericComparison compare(Number x, Number y) {

		if (x == null || y == null) {
			return null;
		}
		
		double doubleX = x.doubleValue();
		double doubleY = y.doubleValue();

		double delta = doubleY - doubleX;
		
		if (deltaTolerance > 0 && 
				Math.abs(delta) < deltaTolerance) {
			return new NumericComparison();
		}

		if (doubleX == 0) {
			return new NumericComparison(
					new Double(delta), deltaFormat, null, null);
		}
		
		double percentage = delta /doubleX * 100; 
			
		if (percentageTolerance > 0 && 
				Math.abs(percentage) < percentageTolerance) {
			return new NumericComparison();
		}
		
		if (delta == 0) {
			return new NumericComparison();
		}
		else {
			return new NumericComparison(
					new Double(delta),deltaFormat, 
					percentage, percentageFormat);
		}
	}
	
	@Override
	public Class<Number> getType() {
		return Number.class;
	}
	
	public double getDeltaTolerance() {
		return deltaTolerance;
	}

	public void setDeltaTolerance(double deltaTolerance) {
		this.deltaTolerance = deltaTolerance;
	}

	public double getPercentageTolerance() {
		return percentageTolerance;
	}

	public void setPercentageTolerance(double percentageTolerance) {
		this.percentageTolerance = percentageTolerance;
	}
	
	public String getDeltaFormat() {
		return deltaFormat;
	}

	public void setDeltaFormat(String deltaFormat) {
		this.deltaFormat = deltaFormat;
	}

	public String getPercentageFormat() {
		return percentageFormat;
	}

	public void setPercentageFormat(String percentageFormat) {
		this.percentageFormat = percentageFormat;
	}
	
	public String toString() {
		return getClass().getName() + 
			(deltaTolerance > 0 ? ", +/-" + deltaTolerance : "" ) +
			(percentageTolerance > 0 ? ", " + percentageTolerance + "%" : ""); 
	}

}
