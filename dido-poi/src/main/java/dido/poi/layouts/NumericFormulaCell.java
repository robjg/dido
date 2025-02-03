package dido.poi.layouts;

import dido.poi.columns.FormulaColumn;
import dido.poi.columns.NumericFormulaColumn;

import java.lang.reflect.Type;

/**
 * @oddjob.description Define a Numeric Formula column.
 *
 * @author rob
 */
public class NumericFormulaCell extends FormulaCell {

	private final NumericFormulaColumn.Settings settings = NumericFormulaColumn.with();

	@Override
	protected FormulaColumn.FormulaSettings<?> settings() {
		return settings;
	}

	@Override
	public Type getType() {
		return NumericFormulaColumn.TYPE;
	}

}
