package dido.poi.layouts;

import dido.poi.columns.FormulaColumn;
import dido.poi.columns.TextColumn;
import dido.poi.columns.TextFormulaColumn;

/**
 * @oddjob.description Define a Text Formula Column.
 *
 * @author rob
 */
public class TextFormulaCell extends FormulaCell {

	private final TextFormulaColumn.Settings settings = TextFormulaColumn.with();

	@Override
	protected FormulaColumn.FormulaSettings<?> settings() {
		return settings;
	}

	@Override
	public Class<String> getType() {
		return TextColumn.TYPE;
	}

}
