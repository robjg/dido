package dido.poi.style;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Provide the default styles.
 * 
 * @author rob
 *
 */
public class DefaultStyleProivderFactory implements StyleProviderFactory {

	/** The name of the style used for headings. */
	public static String HEADING_STYLE = "heading";
	
	/** The name of the style used for dates. */
	public static String DATE_STYLE = "date";

	public static String DATE_FORMAT = "yyyy-mm-dd";

	/** The name of the style used for a beancmpr difference. */
	public static String BEANCMPR_DIFF_STYLE = "beancmpr-difference";
	
	/** The name of the style used for a beancmpr key column for a matching 
	 * row. */
	public static String BEANCMPR_MATCH_STYLE = "beancmpr-match";
	
	/** The name of the style used for a beancmpr key column for a row
	 * that matches. */
	public static String BEANCMPR_KEY_MATCH_STYLE = "beancmpr-key-match";
	
	/** The name of the style used for a beancmpr key column for a row
	 * with a difference. */
	public static String BEANCMPR_KEY_DIFF_STYLE = "beancmpr-key-difference";

	/** The name of the style used for a beancmpr key column for a row
	 * with one side missing. */
	public static String BEANCMPR_MISSING_STYLE = "beancmpr-missing";
	
	private final StyleFactoryRegistry factory = new StyleFactoryRegistry();
	
	/**
	 * Create a new instance.
	 */
	public DefaultStyleProivderFactory() {
		
		StyleBean heading = new StyleBean();
		heading.setBold(true);
		
		StyleBean date = new StyleBean();
		date.setFormat(DATE_FORMAT);
		
		StyleBean beanCmprDiff = new StyleBean();
		beanCmprDiff.setFillForegroundColour(IndexedColors.RED);
		beanCmprDiff.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		StyleBean beanCmprMatch = new StyleBean();
		beanCmprMatch.setFillForegroundColour(IndexedColors.BRIGHT_GREEN);
		beanCmprMatch.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		StyleBean beanCmprKeyMatch = new StyleBean();
		beanCmprKeyMatch.setFillForegroundColour(IndexedColors.YELLOW);
		beanCmprKeyMatch.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		StyleBean beanCmprKeyDiff = new StyleBean();
		beanCmprKeyDiff.setFillForegroundColour(IndexedColors.PINK);
		beanCmprKeyDiff.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		StyleBean beanCmprMissing= new StyleBean();
		beanCmprMissing.setFillForegroundColour(IndexedColors.TURQUOISE);
		beanCmprMissing.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		factory.registerStyle(HEADING_STYLE, heading);
		factory.registerStyle(DATE_STYLE, date);
		factory.registerStyle(BEANCMPR_DIFF_STYLE, beanCmprDiff);
		factory.registerStyle(BEANCMPR_MATCH_STYLE, beanCmprMatch);
		factory.registerStyle(BEANCMPR_KEY_MATCH_STYLE, beanCmprKeyMatch);
		factory.registerStyle(BEANCMPR_KEY_DIFF_STYLE, beanCmprKeyDiff);
		factory.registerStyle(BEANCMPR_MISSING_STYLE, beanCmprMissing);
	}
	
	@Override
	public StyleProvider providerFor(Workbook workbook) {
		return factory.providerFor(workbook);
	}
}
