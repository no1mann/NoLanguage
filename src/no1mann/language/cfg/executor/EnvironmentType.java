package no1mann.language.cfg.executor;

import no1mann.language.cfg.token.TokenType;

/*
 * List of all declarable variables
 */
public enum EnvironmentType {
	INTEGER(0L, TokenType.INT_TYPE),
	BOOLEAN(false, TokenType.BOOL_TYPE);
	
	private Object defaultValue;
	private TokenType matchingToken;
	
	EnvironmentType(Object defaultVal, TokenType type){
		defaultValue = defaultVal;
		matchingToken = type;
	}
	
	public Object getDefaultValue(){
		return defaultValue;
	}
	
	public TokenType getMatchingToken(){
		return matchingToken;
	}
}
