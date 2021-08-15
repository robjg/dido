package org.oddjob.dido.poi.style;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * A container for style attributes.
 * 
 * @author rob
 *
 */
public class StyleBean implements StyleFactory {

	private HorizontalAlignment alignment;
	
	private VerticalAlignment verticalAlignment;
	
	private String format;
	
	private IndexedColors colour;
	
	private IndexedColors fillForegroundColour;
	
	private IndexedColors fillBackgroundColour;
	
	private FillPatternType fillPattern;
	
	private boolean wrapped;
	
	private short size;
	
	private boolean bold;
	
	private boolean strikeout;
	
	private FontUnderline underline;
	
	private boolean italic;
	
	private String font;
	
	/**
	 * Create a style.
	 * 
	 * @param workbook. The workbook. Must not be null.
	 * 
	 * @return The style. Will not be null.
	 */
	@Override
	public CellStyle createStyle(Workbook workbook) {
		
		CellStyle style = workbook.createCellStyle();
		if (alignment != null) {
			style.setAlignment((short) alignment.ordinal());
		}
		if (verticalAlignment != null) {
			style.setVerticalAlignment((short) verticalAlignment.ordinal());
		}
		
		if (format != null) {
			DataFormat dataFormat = workbook.createDataFormat();			
			style.setDataFormat(dataFormat.getFormat(format));
			
		}
		
		if (colour != null) {
			style.setFillForegroundColor(colour.getIndex());
		}
		
		if (fillBackgroundColour != null) {
			style.setFillBackgroundColor(fillBackgroundColour.getIndex());
		}

		if (fillForegroundColour != null) {
			style.setFillForegroundColor(fillForegroundColour.getIndex());
		}
		
		if (fillPattern != null) {
			style.setFillPattern((short) fillPattern.ordinal());
		}
		
		style.setWrapText(wrapped);
		
		Font font = new FontProvider(workbook).createFont();
		if (font != null) {
			style.setFont(font);
		}
		return style;
	}
	
	/**
	 * Helper class to create fonts.
	 * 
	 */
	class FontProvider {
		
		Workbook workbook;
		
		Font ourFont;
		
		public FontProvider(Workbook workbook) {
			this.workbook = workbook;
		}
		
		private Font getOurFont() {
			if (ourFont == null) {
				ourFont = workbook.createFont();
			}
			return ourFont;
		}
		
		Font createFont() {
			
			if (bold) {
				getOurFont().setBoldweight(Font.BOLDWEIGHT_BOLD);
			}
			
			if (colour != null) {
				getOurFont().setColor(colour.getIndex());
			}

			if (size != 0) {
				getOurFont().setFontHeightInPoints(size);
			}
			
			if (underline != null) {
				getOurFont().setUnderline(underline.getByteValue());
			}
			
			if (strikeout) {
				getOurFont().setStrikeout(strikeout);
			}
			
			if (italic) {
				getOurFont().setItalic(italic);
			}
			
			if (font != null) {
				getOurFont().setFontName(font);
			}
			
			return ourFont;
		}
	}

	public HorizontalAlignment getAlignment() {
		return alignment;
	}

	public void setAlignment(HorizontalAlignment alignment) {
		this.alignment = alignment;
	}

	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public IndexedColors getColour() {
		return colour;
	}

	public void setColour(IndexedColors colour) {
		this.colour = colour;
	}

	public IndexedColors getFillForegroundColour() {
		return fillForegroundColour;
	}

	public void setFillForegroundColour(IndexedColors fillColour) {
		this.fillForegroundColour = fillColour;
	}

	public IndexedColors getFillBackgroundColour() {
		return fillBackgroundColour;
	}

	public void setFillBackgroundColour(IndexedColors backgroundColour) {
		this.fillBackgroundColour = backgroundColour;
	}

	public FillPatternType getFillPattern() {
		return fillPattern;
	}

	public void setFillPattern(FillPatternType fillPattern) {
		this.fillPattern = fillPattern;
	}

	public boolean isWrapped() {
		return wrapped;
	}

	public void setWrapped(boolean wrapped) {
		this.wrapped = wrapped;
	}

	public short getSize() {
		return size;
	}

	public void setSize(short size) {
		this.size = size;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isStrikeout() {
		return strikeout;
	}

	public void setStrikeout(boolean strikeout) {
		this.strikeout = strikeout;
	}

	public FontUnderline getUnderline() {
		return underline;
	}

	public void setUnderline(FontUnderline underline) {
		this.underline = underline;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}
}
