package no1mann.language.cfg.parser;

import java.util.ArrayList;
import java.util.List;

import no1mann.language.cfg.exceptions.ParseException;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

public class Parser {
	
	private static final TokenType[] OPERATORS = {
			TokenType.MOD, 
			TokenType.PLUS, 
			TokenType.SUB, 
			TokenType.MULT, 
			TokenType.DIV, 
			TokenType.POW,
			TokenType.OR, 
			TokenType.AND, 
			TokenType.EQUAL, 
			TokenType.GREATER_EQUAL, 
			TokenType.GREATER, 
			TokenType.LESS_EQUAL,
			TokenType.LESS};
	
	//Counter for parsing parenthesis
	private static int GLOBAL_COUNTER = 0;
	
	public static ASTree<Token> parse(List<Token> tokenList) throws ParseException{
		GLOBAL_COUNTER = 0;
		return parseExpression(tokenList);
	}
	
	private static ASTree<Token> parseExpression(List<Token> tokenList) throws ParseException{
		if(tokenList == null)
			return null;
		
		//Parenthesis parser
		ASTree<Token> paren = splitParenthesis(tokenList);
		if(paren != null)
			return paren;
		
		//Operator parser
		for(TokenType type : OPERATORS){
			SplitArray<Token> split = split(tokenList, new Token(type, ""));
			if(split!=null)
				return generateTree(split);
		}
		
		//Data type parser
		if(tokenList.size()==1)
			return new ASTree<Token>(tokenList.get(0));
		
		//Failure
		throw new ParseException("Failure to parse " + tokenList.toString());
	}
	
	//Converts SplitArray to a Tree
	private static ASTree<Token> generateTree(SplitArray<Token> split) throws ParseException{
		return new ASTree<Token>(split.splitValue)
			.addBranch(parseExpression(split.left))
			.addBranch(parseExpression(split.right));
	}
	
	private static ASTree<Token> splitParenthesis(List<Token> tokenList) throws ParseException {
		int index = 0;
		
		//Finds first parenthesis
		while (!tokenList.get(index).equals(TokenType.LEFT_PAREN) && index != tokenList.size() - 1)
			index++;
		
		//If no parenthesis, return null
		if (index == tokenList.size() - 1)
			return null;
		
		//Finds the ending parenthesis
		int last = index, find = 1, count = 0;
		while ((count != find) && last != (tokenList.size()-1)) {
			last++;
			//Incremental counters for finding matching parenthesis
			if (tokenList.get(last).equals(TokenType.LEFT_PAREN))
				find++;
			if (tokenList.get(last).equals(TokenType.RIGHT_PAREN))
				count++;
		}
		
		//If no matching parenthesis
		if ((last == tokenList.size() - 1) && count != find)
			throw new ParseException("Invalid parenthesis");
		
		int val = GLOBAL_COUNTER++;
		ASTree<Token> paren = parseExpression(new ArrayList<Token>(tokenList.subList(index + 1, last)));
		
		// Parenthesis around whole expression
		if (last == tokenList.size() - 1 && index == 0) 
			return paren;
		
		// Parenthesis at end of expression
		if (last == tokenList.size() - 1 && index != 0) {
			//New token list
			List<Token> temp = new ArrayList<Token>(tokenList.subList(0, index));
			temp.add(new Token(TokenType.TEMP, val));
			//Parse tree
			ASTree<Token> left = parseExpression(temp);
			left.replace(paren, new Token(TokenType.TEMP, val));
			return left;
		}
		
		// Parenthesis at start of expression
		if (last != tokenList.size() - 1 && index == 0) {
			//New token list
			List<Token> temp = new ArrayList<Token>();
			temp.add(new Token(TokenType.TEMP, val));
			temp.addAll(new ArrayList<Token>(tokenList.subList(last+1, tokenList.size())));
			//Parse tree
			ASTree<Token> right = parseExpression(temp);
			right.replace(paren, new Token(TokenType.TEMP, val));
			return right;
		}
		
		// Parenthesis in middle of expression
		List<Token> temp = new ArrayList<Token>(tokenList.subList(0, index));
		temp.add(new Token(TokenType.TEMP, val));
		temp.addAll(new ArrayList<Token>(tokenList.subList((last+1), tokenList.size())));
		//Parse tree
		ASTree<Token> parse = parseExpression(temp);
		parse.replace(paren, new Token(TokenType.TEMP, val));
		return parse;

	}
	
	private static SplitArray<Token> split(List<Token> tokenList, Token splitToken){
		int index = -1;
		//Finds index of token
		for (int i = 0; i < tokenList.size(); i++) {
			if (tokenList.get(i).equals(splitToken.getTokenType())) {
				index = i;
				break;
			}
		}
		//If no value
		if(index==-1)
			return null;
		return new Parser().new SplitArray<Token>(tokenList.subList(0, index), tokenList.subList(index+1, tokenList.size()), tokenList.get(index));
	}
	
	private class SplitArray<T>{
		private List<T> left, right;
		private T splitValue;
		public SplitArray(List<T> left, List<T> right, T splitValue){
			this.left = left;
			this.right = right;
			this.splitValue = splitValue;
		}
	}
}
