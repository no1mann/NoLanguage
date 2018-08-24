package no1mann.language.cfg.token;

import java.io.Serializable;

/*
 * Token instance for each section of code
 */
public class Token implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//Type of token
	private TokenType type;
	//Raw string that was parsed
	private String value = null;
	private int pointer = 0;
	
	public Token(TokenType type){
		this.type = type;
	}
	
	public Token(TokenType type, String value){
		this(type);
		this.value = value;
	}
	
	public Token(TokenType type, int value){
		this(type);
		this.pointer = value;
		this.value = value + "";
	}
	
	public TokenType getTokenType(){
		return type;
	}
	
	public String getValue(){
		return value;
	}
	
	public int getPointer(){
		return pointer;
	}
	
	public boolean equals(Object token){
		if(token instanceof TokenType)
			return token.equals(type);
		else if(token instanceof Token)
			return ((Token) token).getTokenType().equals(type) && ((Token) token).getValue().equals(value);
		return false;
	}
	
	public String toString(){
		return type.name() + ": " + value;
	}
	
}
