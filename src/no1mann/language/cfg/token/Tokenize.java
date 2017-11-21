package no1mann.language.cfg.token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.InvalidInputException;

public class Tokenize{
	
	private static final String WHITE_SPACE = "\\s+";
	
	public static List<Token> tokenize(SourceFile file) throws InvalidInputException{
		List<Token> tokenList = new ArrayList<Token>();
		
		//Splits tokens up
		String[] rawInput = file.getData().split(WHITE_SPACE);
		//Cycles through all tokens
		for(String input : rawInput){
			
			if(input.length()==0)
				continue;
			
			// Parse input
			TokenReturn tokenReturn = parseToken(input);
			tokenList.add(tokenReturn.token);

			// If input is not finished parsing
			while (tokenReturn.result.length() != 0) {
				tokenReturn = parseToken(tokenReturn.result);
				tokenList.add(tokenReturn.token);
			}
		}
		return tokenList;
	}
	
	private static TokenReturn parseToken(String input) throws InvalidInputException{
		//Cycles through token matcher to find correct token
		for(TokenType type : TokenType.values()){
			
			//If couldn't match with any token
			if(type==TokenType.EOF)
				break;
			
			//Try to match with input
			Matcher match = type.getPattern().matcher(input);
			if(match.find())
				return new Tokenize().new TokenReturn(new Token(type, match.group(1)), match.group(2));
		}
		//Couldn't find a compatible token
		throw new InvalidInputException("Failed to tokenize at " + input);
	}
	
	private class TokenReturn{
		public Token token;
		public String result;
		public TokenReturn(Token token, String result){
			this.token = token;
			this.result = result;
		}
	}
}
