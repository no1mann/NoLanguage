package no1mann.language.cfg.parser;

import java.util.ArrayList;
import java.util.List;

import no1mann.language.cfg.exceptions.ParseException;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

public class Parser {
	
	public static final TokenType[] VALUE_TYPES = {
		TokenType.INT_TYPE,
		TokenType.BOOL_TYPE
	};
	
	private static final TokenType[] OPERATORS = {
			TokenType.OR, 
			TokenType.AND, 
			TokenType.EQUAL, 
			TokenType.GREATER_EQUAL, 
			TokenType.GREATER, 
			TokenType.LESS_EQUAL,
			TokenType.LESS,
			TokenType.MOD, 
			TokenType.PLUS, 
			TokenType.SUB, 
			TokenType.MULT, 
			TokenType.DIV, 
			TokenType.POW};
	
	//Counter for parsing parenthesis
	private static int GLOBAL_COUNTER = 0;
	
	public static ASTree<Token> parse(List<Token> tokenList) throws ParseException{
		GLOBAL_COUNTER = 0;
		return parseStatement(tokenList);
	}
	
	private static boolean match(List<Token> tokenList, TokenType tok, int index){
		if(tokenList.size()<=index)
			return false;
		return tokenList.get(index).equals(tok);
	}
	
	private static boolean match(List<Token> tokenList, TokenType tok){
		return match(tokenList, tok, 0);
	}
	
	private static ASTree<Token> parseStatement(List<Token> tokenList) throws ParseException{
		if(tokenList.size()==0)
			return new ASTree<Token>(new Token(TokenType.END_OF_STATEMENT));
		//Parses if statement
		if(match(tokenList, TokenType.IF)){
			Bounds<Integer> expBounds = getParenBounds(tokenList, 0, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
			ASTree<Token> exp = parseExpression(new ArrayList<Token>(tokenList.subList(expBounds.left, expBounds.right+1)));
			Bounds<Integer> trueBounds = getParenBounds(tokenList, (expBounds.right+1), TokenType.LEFT_BRACKET, TokenType.RIGHT_BRACKET);
			ASTree<Token> trueStatement = parseStatement(new ArrayList<Token>(tokenList.subList(trueBounds.left+1, trueBounds.right)));
			ASTree<Token> ifStatement = (new ASTree<Token>(new Token(TokenType.IF)).addBranch(exp).addBranch(trueStatement));
			int start = trueBounds.right+1;
			if(	match(tokenList, TokenType.RIGHT_BRACKET, trueBounds.right) &&
				match(tokenList, TokenType.ELSE, trueBounds.right+1)){
				Bounds<Integer> falseBounds = getParenBounds(tokenList, (trueBounds.right+2), TokenType.LEFT_BRACKET, TokenType.RIGHT_BRACKET);
				ASTree<Token> falseStatment = parseStatement(new ArrayList<Token>(tokenList.subList(falseBounds.left+1, falseBounds.right)));
				ifStatement.addBranch(falseStatment);
				start = falseBounds.right+1;
			} 
			else if(!match(tokenList, TokenType.RIGHT_BRACKET, trueBounds.right))
				throw new ParseException("Failure to parse if statement: missing bracket in " + tokenList.toString());
			return ifStatement.addBranch(parseStatement(new ArrayList<Token>(tokenList.subList(start, tokenList.size()))));
		}
		//Parses while statement
		else if(match(tokenList, TokenType.WHILE)){
				Bounds<Integer> expBounds = getParenBounds(tokenList, 0, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
				ASTree<Token> exp = parseExpression(new ArrayList<Token>(tokenList.subList(expBounds.left, expBounds.right+1)));
				Bounds<Integer> stateBounds = getParenBounds(tokenList, (expBounds.right+1), TokenType.LEFT_BRACKET, TokenType.RIGHT_BRACKET);
				ASTree<Token> statement = parseStatement(new ArrayList<Token>(tokenList.subList(stateBounds.left+1, stateBounds.right)));
				if(!match(tokenList, TokenType.RIGHT_BRACKET, stateBounds.right))
					throw new ParseException("Failure to parse if statement: missing bracket in " + tokenList.toString());
				ASTree<Token> rest = parseStatement(new ArrayList<Token>(tokenList.subList(stateBounds.right+1, tokenList.size())));
				return new ASTree<Token>(new Token(TokenType.WHILE)).addBranch(exp).addBranch(statement).addBranch(rest);
			}
		//Parses assignment
		else if(match(tokenList, TokenType.VAR_NAME) && match(tokenList, TokenType.ASSIGN, 1)){
			int index = findValue(tokenList, new Token(TokenType.SEMICOLON));
			if(index==-1)
				throw new ParseException("Failure to parse, missing semicolon");
			ASTree<Token> expression = parseExpression(new ArrayList<Token>(tokenList.subList(2, index)));
			return new ASTree<Token>(new Token(TokenType.VAR_NAME, tokenList.get(0).getValue())).addBranch(expression).addBranch(parseStatement(new ArrayList<Token>(tokenList.subList(index+1, tokenList.size()))));
		}
		//Parses print
		else if(match(tokenList, TokenType.PRINT)){
			Bounds<Integer> expBounds = getParenBounds(tokenList, 1, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
			ASTree<Token> exp = parseExpression(new ArrayList<Token>(tokenList.subList(expBounds.left, expBounds.right+1)));
			if(match(tokenList, TokenType.SEMICOLON, (expBounds.right+1)))
				return new ASTree<Token>(new Token(TokenType.PRINT)).addBranch(exp).addBranch(parseStatement(new ArrayList<Token>(tokenList.subList(expBounds.right+2, tokenList.size()))));
			else
				throw new ParseException("Failure to parse, missing semicolon");
		}
		else {
		//Parses declaration
			for(TokenType tok : VALUE_TYPES){
				if(match(tokenList, tok) && match(tokenList, TokenType.VAR_NAME, 1) && match(tokenList, TokenType.SEMICOLON, 2))
					return new ASTree<Token>(new Token(tok, tokenList.get(1).getValue())).addBranch(parseStatement(tokenList.subList(3, tokenList.size())));
			}
		}
		
		return null;
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
		
		Bounds<Integer> bounds = getParenBounds(tokenList, 0, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
		if(bounds==null)
			return null;
		
		int index = bounds.left, last = bounds.right;
		
		String val = (GLOBAL_COUNTER++) + "";
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
	
	private static int findValue(List<Token> tokenList, Token token){
		int index = -1;
		//Finds index of token
		for (int i = 0; i < tokenList.size(); i++) {
			if (tokenList.get(i).equals(token.getTokenType())) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	private static void printList(List<Token> list){
		for(Token tok : list){
			System.out.print(tok + ", ");
		}
		System.out.println();
	}
	
	private static Bounds<Integer> getParenBounds(List<Token> tokenList, int start, TokenType left, TokenType right) throws ParseException{
		int index = start;
		
		//Finds first parenthesis
		while (!tokenList.get(index).equals(left) && index != tokenList.size() - 1)
			index++;
		
		//If no parenthesis, return null
		if (index == tokenList.size() - 1)
			return null;
		
		//Finds the ending parenthesis
		int last = index, find = 1, count = 0;
		while ((count != find) && last != (tokenList.size()-1)) {
			last++;
			//Incremental counters for finding matching parenthesis
			if (tokenList.get(last).equals(left))
				find++;
			if (tokenList.get(last).equals(right))
				count++;
		}
		
		//If no matching parenthesis
		if ((last == tokenList.size() - 1) && count != find)
			throw new ParseException("Invalid matching between: " + left + " and " + right);
		
		return new Parser().new Bounds<Integer>(index, last);
	}
	
	private class Bounds<T>{
		public T left, right;
		public Bounds(T one, T two){
			left = one;
			right = two;
		}
	}
	
	private static SplitArray<Token> split(List<Token> tokenList, Token splitToken){
		int index = findValue(tokenList, splitToken);
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
