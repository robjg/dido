package dido.poi.layouts;

import dido.poi.columns.FormulaColumn;
import dido.poi.columns.NumericFormulaColumn;

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
	public Class<?> getType() {
		return settings.type();
	}

	/**
	 * @oddjob.description The type of Number.
	 * @oddjob.required No. Defaults to Double.
	 */
	public void setType(Class<?> type) {
		this.settings.type(type);
	}

}
