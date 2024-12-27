package dido.poi.layouts;

import dido.poi.columns.FormulaColumn;
import org.apache.poi.ss.usermodel.CellType;

public abstract class FormulaCell extends AbstractDataCell {

	@Override
	protected abstract FormulaColumn.FormulaSettings<?> settings();

	@Override
	public CellType getCellType() {
		return CellType.FORMULA;
	}

	public String getFormula() {
		return settings().formula();
	}

	public void setFormula(String formula) {
		this.settings().formula(formula);
	}
}
