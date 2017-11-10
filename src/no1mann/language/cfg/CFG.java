package no1mann.language.cfg;

import java.util.regex.Pattern;

public class CFG {
	
	public static final String WHITE_SPACE = "\\s+";
	
	/*EXPRESSIONS*/
	
	//Variables
	public static final Pattern INT_VAL = Pattern.compile("^(\\d+)(.*)");
	public static final Pattern BOOL_VAL = Pattern.compile("^(true|false)(.*)");
	
	//Math Operators
	public static final Pattern PLUS = Pattern.compile("^(\\+)+(.*)");
	public static final Pattern SUB = Pattern.compile("^(-)+(.*)");
	public static final Pattern MULT = Pattern.compile("^(\\*)+(.*)");
	public static final Pattern DIV = Pattern.compile("^(/)+(.*)");
	public static final Pattern POW = Pattern.compile("^(\\^)+(.*)");
	public static final Pattern MOD = Pattern.compile("^(%)+(.*)");
	
	//Boolean Operators
	public static final Pattern AND = Pattern.compile("^(&&)+(.*)");
	public static final Pattern OR = Pattern.compile("^(\\|\\|)+(.*)");
	public static final Pattern NOT = Pattern.compile("^(\\!)+(.*)");
	public static final Pattern EQUAL = Pattern.compile("^(==)+(.*)");
	public static final Pattern NOT_EQUAL = Pattern.compile("^(\\!=)+(.*)");
	public static final Pattern GREATER = Pattern.compile("^(>)+(.*)");
	public static final Pattern GREATER_EQUAL = Pattern.compile("^(>=)+(.*)");
	public static final Pattern LESS = Pattern.compile("^(<)+(.*)");
	public static final Pattern LESS_EQUAL = Pattern.compile("^(<=)+(.*)");
	
	
	/*Statement*/
	public static final Pattern INT_TYPE = Pattern.compile("^(int)(.*)");
	public static final Pattern BOOL_TYPE = Pattern.compile("^(bool)(.*)");
	public static final Pattern MAIN = Pattern.compile("^(main)(.*)");
	
	
}
