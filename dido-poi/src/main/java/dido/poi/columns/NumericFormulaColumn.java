package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * @oddjob.description Define a Numeric Formula column.
 *
 * @author rob
 */
public class NumericFormulaColumn extends FormulaColumn {

	public static final Type TYPE = double.class;

	protected NumericFormulaColumn(Settings settings) {
		super(settings);
	}

	public static class Settings extends FormulaColumn.FormulaSettings<Settings> {

		private Class<?> type;

		@Override
		protected Settings self() {
			return this;
		}

		public NumericFormulaColumn make() {
			return new NumericFormulaColumn(this);
		}
	}

	public static Settings with() {
		return new Settings();
	}

	@Override
	public Type getType() {
		return TYPE;
	}

	@Override
	protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {

		Type type = schemaField.getType();

		if (type == double.class) {
			return new DoubleGetter(schemaField);
		} else if (type == int.class) {
			return new IntGetter(schemaField);
		} else if (type == long.class) {
			return new LongGetter(schemaField);
		} else if (type == short.class) {
			return new ShortGetter(schemaField);
		} else if (type == byte.class) {
			return new ByteGetter(schemaField);
		} else if (type == float.class) {
			return new FloatGetter(schemaField);
		} else {
			return new DoubleCellGetterWithConversion<>(schemaField,
					RequiringConversion.with(conversionProvider)
							.<Double>from(Double.class)
							.to(type));
		}
	}

	abstract static class NumberGetter extends AbstractCellGetter {

		NumberGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public byte getByte(DidoData data) {
			return (byte) getDouble(data);
		}

		@Override
		public short getShort(DidoData data) {
			return (short) getDouble(data);
		}

		@Override
		public int getInt(DidoData data) {
			return (int) getDouble(data);
		}

		@Override
		public long getLong(DidoData data) {
			return (long) getDouble(data);
		}

		@Override
		public float getFloat(DidoData data) {
			return (float) getDouble(data);
		}

		@Override
		public double getDouble(DidoData data) {
			Cell cell = getCell(data);

			FormulaEvaluator evaluator = cell.getRow().getSheet(
			).getWorkbook().getCreationHelper().createFormulaEvaluator();

			CellValue cellValue = evaluator.evaluate(cell);

			return cellValue.getNumberValue();
		}
	}

	static class ByteGetter extends NumberGetter {

		ByteGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public Object get(DidoData data) {
			return getByte(data);
		}
	}

	static class ShortGetter extends NumberGetter {

		ShortGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public Object get(DidoData data) {
			return getShort(data);
		}
	}

	static class IntGetter extends NumberGetter {

		IntGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public Object get(DidoData data) {
			return getInt(data);
		}
	}

	static class LongGetter extends NumberGetter {

		LongGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public Object get(DidoData data) {
			return getLong(data);
		}
	}

	static class FloatGetter extends NumberGetter {

		FloatGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public Object get(DidoData data) {
			return getFloat(data);
		}
	}

	static class DoubleGetter extends NumberGetter {

		DoubleGetter(SchemaField schemaField) {
			super(schemaField);
		}

		@Override
		public Object get(DidoData data) {
			return getDouble(data);
		}
	}

	static class DoubleCellGetterWithConversion<R> extends NumberGetter {

		private final Function<Double, R> conversion;

		DoubleCellGetterWithConversion(SchemaField schemaField,
									   Function<Double, R> conversion) {
			super(schemaField);
			this.conversion = conversion;
		}

		@Override
		public Object get(DidoData data) {
			try {
				return conversion.apply(super.getDouble(data));
			} catch (RuntimeException e) {
				throw DataException.of(String.format("Failed to get value for %s", this), e);
			}
		}
	}

}
