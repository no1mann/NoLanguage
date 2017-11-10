package no1mann.language.cfg.token;

public class Token {

	private TokenType type;
	private Object value = null;
	
	public Token(TokenType type){
		this.type = type;
	}
	
	public Token(TokenType type, Object value){
		this(type);
		this.value = value;
	}
	
	public TokenType getTokenType(){
		return type;
	}
	
	public Object getValue(){
		return value;
	}
	
	public boolean equals(Token token){
		return token.getTokenType()==type;
	}
	
	public String toString(){
		return type.name() + ": " + value;
	}
	
}
