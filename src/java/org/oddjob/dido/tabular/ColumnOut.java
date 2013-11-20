package org.oddjob.dido.tabular;

import org.oddjob.dido.field.FieldOut;

/**
 * Representation of a column that data can be written to. 
 * 
 * @see TabularDataOut
 * 
 * @author rob
 *
 * @param <T> The type of data.
 */
public interface ColumnOut<T> extends FieldOut<T>, ColumnData {

}
