package no1mann.language.cfg.token;

/*
 * Token instance for each section of code
 */
public class Token {
	
	//Type of token
	private TokenType type;
	//Raw string that was parsed
	private String value = null;
	
	public Token(TokenType type){
		this.type = type;
	}
	
	public Token(TokenType type, String value){
		this(type);
		this.value = value;
	}
	
	public TokenType getTokenType(){
		return type;
	}
	
	public String getValue(){
		return value;
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
