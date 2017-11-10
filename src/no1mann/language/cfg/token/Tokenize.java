package no1mann.language.cfg.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no1mann.language.cfg.CFG;
import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.InvalidInputException;

public class Tokenize{
	
	private static HashMap<TokenType, Pattern> tokenMatcher;
	
	static{
		//Maps TokeTypes to CFG Regular Expressions
		tokenMatcher = new HashMap<TokenType, Pattern>();
		tokenMatcher.put(TokenType.INT_VAL, CFG.INT_VAL);
		tokenMatcher.put(TokenType.BOOL_VAL, CFG.BOOL_VAL);
		tokenMatcher.put(TokenType.PLUS, CFG.PLUS);
		tokenMatcher.put(TokenType.SUB, CFG.SUB);
		tokenMatcher.put(TokenType.MULT, CFG.MULT);
		tokenMatcher.put(TokenType.DIV, CFG.DIV);
		tokenMatcher.put(TokenType.POW, CFG.POW);
		tokenMatcher.put(TokenType.MOD, CFG.MOD);
		tokenMatcher.put(TokenType.AND, CFG.AND);
		tokenMatcher.put(TokenType.OR, CFG.OR);
		tokenMatcher.put(TokenType.NOT, CFG.NOT);
		tokenMatcher.put(TokenType.EQUAL, CFG.EQUAL);
		tokenMatcher.put(TokenType.NOT_EQUAL, CFG.NOT_EQUAL);
		tokenMatcher.put(TokenType.GREATER, CFG.GREATER);
		tokenMatcher.put(TokenType.GREATER_EQUAL, CFG.GREATER_EQUAL);
		tokenMatcher.put(TokenType.LESS, CFG.LESS);
		tokenMatcher.put(TokenType.LESS_EQUAL, CFG.LESS_EQUAL);
		tokenMatcher.put(TokenType.MAIN, CFG.MAIN);
		tokenMatcher.put(TokenType.INT_TYPE, CFG.INT_TYPE);
		tokenMatcher.put(TokenType.BOOL_TYPE, CFG.BOOL_TYPE);
	}
	
	public static List<Token> tokenize(SourceFile file) throws InvalidInputException{
		List<Token> tokenList = new ArrayList<Token>();
		
		//Splits tokens up
		String[] rawInput = file.getData().split(CFG.WHITE_SPACE);
		
		//Cycles through all tokens
		for(String input : rawInput){
			// Parse input
			TokenReturn tokenReturn = parseToken(input);
			tokenList.add(tokenReturn.token);

			// If input is not finished parsing
			while (tokenReturn.result.length() != 0) {
				tokenReturn = parseToken(tokenReturn.result);
				tokenList.add(tokenReturn.token);
			}
		}
		tokenList.add(new Token(TokenType.EOF, null));
		return tokenList;
	}
	
	private static TokenReturn parseToken(String input) throws InvalidInputException{
		//Cycles through token matcher to find correct token
		for(TokenType type : tokenMatcher.keySet()){
			Matcher match = tokenMatcher.get(type).matcher(input);
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
