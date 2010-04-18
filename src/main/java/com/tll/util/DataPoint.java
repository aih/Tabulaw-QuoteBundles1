/**
 * 
 */
package com.tll.util;


/**
 * An {@link FNum} with related statistical data.
 * @author jpk
 */
public class DataPoint extends FNum {
	/**
	 * The standard deviation with a display formatting directive.
	 */
	private FNum stdDev;
	
	public DataPoint() {
		super();
	}
	public DataPoint(Double value) {
		super(value);
	}
	public DataPoint(Double value, String numberFormat) {
		super(value, numberFormat);
	}
	public DataPoint(Double value, String valueFormat, Double stdDev, String stdDevFormat) {
		super(value, valueFormat);
		setStdDev( new FNum(stdDev, stdDevFormat) );
	}
	public DataPoint(Double value, String valueFormat, FNum fStdDev) {
		super(value, valueFormat);
		setStdDev( fStdDev );
	}
	
	public FNum getStdDev() {
		return stdDev;
	}
	public void setStdDev(FNum stdDev) {
		this.stdDev = stdDev;
	}
}
