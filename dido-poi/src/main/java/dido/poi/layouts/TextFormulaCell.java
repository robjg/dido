package dido.poi.layouts;

import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 * @oddjob.description Define a Text Formula Column.
 *
 * @author rob
 */
public class TextFormulaCell extends FormulaCell<String> {

	@Override
	public Class<String> getType() {
		return String.class;
	}

	@Override
	public CellIn<String> provideCellIn(int index,
										DidoConversionProvider conversionProvider) {

		return rowIn -> {

			Cell cell = rowIn.getCell(index);

			FormulaEvaluator evaluator = cell.getRow().getSheet(
			).getWorkbook().getCreationHelper().createFormulaEvaluator();

			CellValue cellValue = evaluator.evaluate(cell);

			return cellValue.getStringValue();
		};
	}
}
