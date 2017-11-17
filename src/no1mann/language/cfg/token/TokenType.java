package no1mann.language.cfg.token;

import java.util.regex.Pattern;

public enum TokenType {
	//DATA VALUES
	INT_VAL(Pattern.compile("^(\\d+)(.*)")),
	BOOL_VAL(Pattern.compile("^(true|false)(.*)")),
	
	//MATH OPERATOR
	PLUS(Pattern.compile("^(\\+)(.*)")),
	SUB(Pattern.compile("^(-)(.*)")),
	MULT(Pattern.compile("^(\\*)(.*)")),
	DIV(Pattern.compile("^(/)(.*)")),
	POW(Pattern.compile("^(\\^)(.*)")),
	MOD(Pattern.compile("^(%)(.*)")),
	
	//BOOLEAN OPERATOR
	AND(Pattern.compile("^(&&)(.*)")),
	OR(Pattern.compile("^(\\|\\|)(.*)")),
	EQUAL(Pattern.compile("^(==)(.*)")),
	NOT_EQUAL(Pattern.compile("^(\\!=)(.*)")),
	GREATER_EQUAL(Pattern.compile("^(>=)(.*)")),
	GREATER(Pattern.compile("^(>)(.*)")),
	LESS_EQUAL(Pattern.compile("^(<=)(.*)")),
	LESS(Pattern.compile("^(<)(.*)")),
	
	//BRACKETS and OTHER
	LEFT_PAREN(Pattern.compile("^(\\()(.*)")),
	RIGHT_PAREN(Pattern.compile("^(\\))(.*)")),
	LEFT_BRACKET(Pattern.compile("^(\\{)(.*)")),
	RIGHT_BRACKET(Pattern.compile("^(\\})(.*)")),
	SEMICOLON(Pattern.compile("^(;)(.*)")),
	
	//STATEMENTS
	MAIN(Pattern.compile("^(main)(.*)")),
	IF(Pattern.compile("^(if)(.*)")),
	ELSEIF(Pattern.compile("^(elseif)(.*)")),
	ELSE(Pattern.compile("^(else)(.*)")),
	WHILE(Pattern.compile("^(while)(.*)")),
	PRINT(Pattern.compile("^(print)(.*)")),
	INT_TYPE(Pattern.compile("^(int)(.*)")),
	BOOL_TYPE(Pattern.compile("^(bool)(.*)")),
	
	//OTHER
	ASSIGN(Pattern.compile("^(=)(.*)")),
	VAR_NAME(Pattern.compile("^([A-Za-z0-9]+)(.*)")),
	TEMP(Pattern.compile("a^")),
	EOF(Pattern.compile(".*")),
	END_OF_STATEMENT(Pattern.compile(".*"));
	
	private Pattern pattern;
	TokenType(Pattern pat){
		pattern = pat;
	}
	public Pattern getPattern(){
		return pattern;
	}
}
