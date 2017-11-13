package no1mann.language.cfg.parser;

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
	public static ASTree<Token> parse(List<Token> tokenList) throws ParseException{
		return parseExpression(tokenList);
	}
	
	private static ASTree<Token> parseExpression(List<Token> tokenList) throws ParseException{
		SplitArray<Token> split = split(tokenList, new Token(TokenType.LEFT_PAREN, ""), true);
		if (split != null) {
			SplitArray<Token> splitSecond = split(split.right, new Token(TokenType.RIGHT_PAREN, ""), false);
			ASTree<Token> paren = parseExpression(splitSecond.left);
			ASTree<Token> left, right;
			Token leftSign, rightSign;
			// Parenthesis around whole expression
			if (splitSecond.right.isEmpty() && split.left.isEmpty()) {
				return paren;
			}
			// Parenthesis at end of expression
			else if (splitSecond.right.isEmpty() && !split.left.isEmpty()) {
				left = parseExpression(split.left.subList(0, split.left.size() - 1));
				leftSign = split.left.get(split.left.size() - 1);
				return new ASTree<Token>(leftSign).addBranch(left).addBranch(paren);
			}
			// Parenthesis at start of expression
			else if (!splitSecond.right.isEmpty() && split.left.isEmpty()) {
				right = parseExpression(splitSecond.right.subList(1, splitSecond.right.size()));
				rightSign = splitSecond.right.get(0);
				return new ASTree<Token>(rightSign).addBranch(paren).addBranch(right);
			} 
			// Parenthesis in middle of expression
			else {
				left = parseExpression(split.left.subList(0, split.left.size() - 1));
				leftSign = split.left.get(split.left.size() - 1);
				right = parseExpression(splitSecond.right.subList(1, splitSecond.right.size()));
				rightSign = splitSecond.right.get(0);
				
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
				throw new ParseException("Failure to parse " + tokenList.toString());
			}
		}
		for(TokenType type : OPERATORS){
			split = split(tokenList, new Token(type, ""), true);
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
