package dido.poi.columns;

import dido.data.DidoData;
import dido.data.FieldGetter;
import dido.data.SchemaField;
import dido.how.DataException;
import dido.how.conversion.DidoConversionProvider;
import dido.how.conversion.RequiringConversion;
import dido.how.util.Primitives;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import java.util.Objects;
import java.util.function.DoubleFunction;

/**
 * @oddjob.description Define a Numeric Formula column.
 *
 * @author rob
 */
public class NumericFormulaColumn extends FormulaColumn {

	/**
	 * The type of Number. Defaults to Double.
	 */
	private final Class<?> type;

	protected NumericFormulaColumn(Settings settings) {
		super(settings);
		this.type = settings.type();
	}

	public static class Settings extends FormulaColumn.FormulaSettings<Settings> {

		private Class<?> type;

		@Override
		protected Settings self() {
			return this;
		}

		public Settings type(Class<?> type) {
			if (type == null) {
				this.type = null;
			} else {
				type = Primitives.wrap(type);
				if (Number.class.isAssignableFrom(type)) {
					this.type = type;
				} else {
					throw new IllegalArgumentException("Type must be a Number");
				}
			}
			return this;
		}

		public Class<?> type() {
			return Objects.requireNonNullElse(this.type, Double.class);
		}

		public NumericFormulaColumn make() {
			return new NumericFormulaColumn(this);
		}
	}

	public static Settings with() {
		return new Settings();
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	protected FieldGetter getterFor(SchemaField schemaField, DidoConversionProvider conversionProvider) {

		Class<?> type = Primitives.wrap(schemaField.getType());
		if (type.isAssignableFrom(Double.class)) {
			return new DoubleGetter(schemaField);
		} else if (type.isAssignableFrom(Integer.class)) {
			return new IntGetter(schemaField);
		} else if (type.isAssignableFrom(Long.class)) {
			return new LongGetter(schemaField);
		} else if (type.isAssignableFrom(Short.class)) {
			return new ShortGetter(schemaField);
		} else if (type.isAssignableFrom(Byte.class)) {
			return new ByteGetter(schemaField);
		} else if (type.isAssignableFrom(Float.class)) {
			return new FloatGetter(schemaField);
		} else {
			return new DoubleCellGetterWithConversion<>(schemaField,
					RequiringConversion.with(conversionProvider).fromDoubleTo(type));
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

		private final DoubleFunction<R> conversion;

		DoubleCellGetterWithConversion(SchemaField schemaField,
									   DoubleFunction<R> conversion) {
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