package org.oddjob.dido.match.comparers;

import java.text.DecimalFormat;

import org.oddjob.dido.match.Comparison;


/**
 * Represent a comparison between two numeric values.
 * 
 * @author Rob
 *
 */
public class NumericComparison implements Comparison {

	private final Double delta;
	
	private final Double percentage;
	
	private final String summaryText;
	
	/**
	 * Constructor.
	 * 
	 * @param delta The change in value.
	 * @param deltaFormat The display format for delta.
	 * @param percentage The percentage change in values.
	 * @param percentageFormat The display format for percentage, 
	 * excluding the percent sign.
	 */
	public NumericComparison(
			Double delta, String deltaFormat,
			Double percentage, String percentageFormat) {
		
		this.delta = delta;
		this.percentage = percentage;
		
		StringBuilder builder = new StringBuilder();
		if (deltaFormat == null) {
			builder.append(String.valueOf(delta.doubleValue()));
		}
		else {
			builder.append(new DecimalFormat(
					deltaFormat).format(delta.doubleValue()));
		}
		if (percentage != null) {
			builder.append(" (");
			if (percentageFormat == null) {
				builder.append(String.valueOf(percentage.doubleValue()));
			}
			else {
				builder.append(new DecimalFormat(
						percentageFormat).format(percentage.doubleValue()));
			}
			builder.append("%)");
		}
		this.summaryText = builder.toString();
	}
		
	/**
	 * Constructor for when two values are equal.
	 */
	public NumericComparison() {
		this.delta = null;
		this.percentage = null;
		this.summaryText = "";
	}
	
	
	@Override
	public boolean isEqual() {
		return this.delta == null;
	}
	
	public Double getDelta() {
		return delta;
	}
			
	public Double getPercentage() {
		return percentage;
	}
	
	public String getSummaryText() {
		return summaryText;
	}
	
}
