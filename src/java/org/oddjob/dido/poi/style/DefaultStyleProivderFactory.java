package org.oddjob.dido.poi.style;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

public class DefaultStyleProivderFactory implements StyleProviderFactory {

	public static String HEADING_STYLE = "heading";
	
	public static String DATE_STYLE = "date";
	
	public static String BEANCMPR_DIFF_STYLE = "beancmpr-difference";
	
	public static String BEANCMPR_MATCH_STYLE = "beancmpr-match";
	
	public static String BEANCMPR_KEY_STYLE = "beancmpr-key";
	
	public static String BEANCMPR_MISSING_STYLE = "beancmpr-missing";
	
	private final StyleFactoryRegistry factory = new StyleFactoryRegistry();
	
	public DefaultStyleProivderFactory() {
		
		StyleBean heading = new StyleBean();
		heading.setBold(true);
		
		StyleBean date = new StyleBean();
		date.setFormat("d/m/yyyy");
		
		StyleBean beanCmprDiff = new StyleBean();
		beanCmprDiff.setFillForegroundColour(IndexedColors.RED);
		beanCmprDiff.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		StyleBean beanCmprMatch = new StyleBean();
		beanCmprMatch.setFillForegroundColour(IndexedColors.BRIGHT_GREEN);
		beanCmprMatch.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		StyleBean beanCmprKey = new StyleBean();
		beanCmprKey.setFillForegroundColour(IndexedColors.YELLOW);
		beanCmprKey.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		StyleBean beanCmprMissing= new StyleBean();
		beanCmprMissing.setFillForegroundColour(IndexedColors.TURQUOISE);
		beanCmprMissing.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		factory.registerStyle(HEADING_STYLE, heading);
		factory.registerStyle(DATE_STYLE, date);
		factory.registerStyle(BEANCMPR_DIFF_STYLE, beanCmprDiff);
		factory.registerStyle(BEANCMPR_MATCH_STYLE, beanCmprMatch);
		factory.registerStyle(BEANCMPR_KEY_STYLE, beanCmprKey);
		factory.registerStyle(BEANCMPR_MISSING_STYLE, beanCmprMissing);
	}
	
	@Override
	public StyleProvider providerFor(Workbook workbook) {
		return factory.providerFor(workbook);
	}
}
