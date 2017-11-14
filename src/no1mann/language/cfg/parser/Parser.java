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
	private static int GLOBAL_COUNTER = 0;
	
	public static ASTree<Token> parse(List<Token> tokenList) throws ParseException{
		GLOBAL_COUNTER = 0;
		return parseExpression(tokenList);
	}
	
	private static ASTree<Token> parseExpression(List<Token> tokenList) throws ParseException{
		if(tokenList == null)
			return null;
		ASTree<Token> paren = splitParenthesis(tokenList);
		if(paren != null)
			return paren;
		for(TokenType type : OPERATORS){
			SplitArray<Token> split = split(tokenList, new Token(type, ""), true);
			if(split!=null)
				return generateTree(split);
		}
		if(tokenList.size()==1){
			Token token = tokenList.get(0);
			return new ASTree<Token>(token);
		}
		throw new ParseException("Failure to parse " + tokenList.toString());
	}
	
	private static ASTree<Token> generateTree(SplitArray<Token> split) throws ParseException{
		ASTree<Token> tree = new ASTree<Token>(split.splitValue);
		tree.addBranch(parseExpression(split.left));
		tree.addBranch(parseExpression(split.right));
		return tree;
	}
	
	private static ASTree<Token> splitParenthesis(List<Token> tokenList) throws ParseException {
		int index = 0;
		while (!tokenList.get(index).equals(TokenType.LEFT_PAREN) && index != tokenList.size() - 1)
			index++;
		
		if (index == tokenList.size() - 1)
			return null;
		int last = index, find = 1, count = 0;
		while ((count != find) && last != (tokenList.size()-1)) {
			last++;
			if (tokenList.get(last).equals(TokenType.LEFT_PAREN))
				find++;
			if (tokenList.get(last).equals(TokenType.RIGHT_PAREN))
				count++;
		}
		if ((last == tokenList.size() - 1) && count != find)
			throw new ParseException("Invalid parenthesis");
		
		int val = GLOBAL_COUNTER++;
		ASTree<Token> paren = parseExpression(new ArrayList<Token>(tokenList.subList(index + 1, last)));
		// Parenthesis around whole expression
		if (last == tokenList.size() - 1 && index == 0) {
			return paren;
		}
		// Parenthesis at end of expression
		if (last == tokenList.size() - 1 && index != 0) {
			List<Token> temp = new ArrayList<Token>(tokenList.subList(0, index));
			temp.add(new Token(TokenType.TEMP, val));
			ASTree<Token> left = parseExpression(temp);
			left.replace(paren, new Token(TokenType.TEMP, val));
			return left;
		}
		// Parenthesis at start of expression
		if (last != tokenList.size() - 1 && index == 0) {
			List<Token> temp = new ArrayList<Token>();
			temp.add(new Token(TokenType.TEMP, val));
			temp.addAll(new ArrayList<Token>(tokenList.subList(last+1, tokenList.size())));
			ASTree<Token> right = parseExpression(temp);
			right.replace(paren, new Token(TokenType.TEMP, val));
			return right;
		}
		// Parenthesis in middle of expression
		List<Token> temp = new ArrayList<Token>(tokenList.subList(0, index));
		temp.add(new Token(TokenType.TEMP, val));
		temp.addAll(new ArrayList<Token>(tokenList.subList((last+1), tokenList.size())));
		ASTree<Token> parse = parseExpression(temp);
		parse.replace(paren, new Token(TokenType.TEMP, val));
		return parse;
		
		/*ASTree<Token> left = parseExpression(tokenList.subList(0, index - 1));
		Token leftSign = tokenList.get(index - 1);
		Token rightSign = tokenList.get(last + 1);
		ASTree<Token> right = parseExpression(tokenList.subList(last + 2, tokenList.size()));

		TokenType highest = null;
		for (TokenType type : OPERATORS) {
			if (highest != null)
				break;
			if (type == rightSign.getTokenType()) {
				ASTree<Token> temp = new ASTree<Token>(leftSign).addBranch(left).addBranch(paren);
				ASTree<Token> returnTree = new ASTree<Token>(rightSign).addBranch(temp).addBranch(right);
				return returnTree;
			} else if (type == leftSign.getTokenType()) {
				ASTree<Token> temp = new ASTree<Token>(rightSign).addBranch(paren).addBranch(right);
				ASTree<Token> returnTree = new ASTree<Token>(leftSign).addBranch(left).addBranch(temp);
				return returnTree;
			}
		}
		throw new ParseException("Failure to parse " + tokenList.toString());*/
	}
	
	private static SplitArray<Token> split(List<Token> tokenList, Token splitToken, boolean first){
		int index = -1;
		if (first) {
			for (int i = 0; i < tokenList.size(); i++) {
				if (tokenList.get(i).equals(splitToken.getTokenType())) {
					index = i;
					break;
				}
			}
		}
		else{
			for (int i = tokenList.size()-1; i >=0; i--) {
				if (tokenList.get(i).equals(splitToken.getTokenType())) {
					index = i;
					break;
				}
			}
		}
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
