package no1mann.language.cfg;

import java.util.regex.Pattern;

public class CFG {
	
	public static final String WHITE_SPACE = "\\s+";
	
	/*Expression*/
	public static final Pattern PLUS_TOKEN = Pattern.compile("\\+");
	public static final Pattern SUB_TOKEN = Pattern.compile("-");
	public static final Pattern MULT_TOKEN = Pattern.compile("\\*");
	public static final Pattern DIV_TOKEN = Pattern.compile("/");
	public static final Pattern POWER_TOKEN = Pattern.compile("\\^");
	
	/*Statement*/
	public static final Pattern MAIN_TOKEN = Pattern.compile("main");
	
	
}
