package dido.poi.layouts;

import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.conversion.DidoConversionProvider;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public abstract class FormulaCell extends AbstractDataCell {

	private String formula;

	@Override
	public CellType getCellType() {
		return CellType.FORMULA;
	}

	@Override
	protected Injector injectorFor(SchemaField ignored1,
								   FieldGetter ignored2,
								   DidoConversionProvider ignored3) {

		return (cell, data) -> {

            try {
                cell.setCellFormula(formula);
            } catch (FormulaParseException e) {
                throw new IllegalArgumentException("Failed setting formula: " + formula, e);
            }

            FormulaEvaluator evaluator = cell.getRow().getSheet(
            ).getWorkbook().getCreationHelper().createFormulaEvaluator();

            evaluator.evaluateFormulaCell(cell);
        };
	}


	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
}
