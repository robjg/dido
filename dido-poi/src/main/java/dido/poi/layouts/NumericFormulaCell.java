package dido.poi.layouts;

import dido.how.conversion.DidoConversionProvider;
import dido.poi.CellIn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 * @oddjob.description Define a Numeric Formula column.
 *
 * @author rob
 */
public class NumericFormulaCell extends FormulaCell<Double> {

	@Override
	public Class<Double> getType() {
		return Double.class;
	}

	@Override
	public CellIn<Double> provideCellIn(int index,
										DidoConversionProvider conversionProvider) {

		return rowIn -> {

			Cell cell = rowIn.getCell(index);

			FormulaEvaluator evaluator = cell.getRow().getSheet(
			).getWorkbook().getCreationHelper().createFormulaEvaluator();

			CellValue cellValue = evaluator.evaluate(cell);

			return cellValue.getNumberValue();
		};
	}

}
