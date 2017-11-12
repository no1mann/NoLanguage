package no1mann.language.cfg.parser;

import java.util.List;

import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

public class Parser {
	
	private static final TokenType[] MATH_OPERATORS = {
			TokenType.MOD, 
			TokenType.PLUS, 
			TokenType.SUB, 
			TokenType.MULT, 
			TokenType.DIV, 
			TokenType.POW};
	
	private static final TokenType[] BOOLEAN_OPERATORS = {
			TokenType.OR, 
			TokenType.AND, 
			TokenType.EQUAL, 
			TokenType.GREATER_EQUAL, 
			TokenType.GREATER, 
			TokenType.LESS_EQUAL,
			TokenType.LESS, 
			TokenType.NOT };
	
	public static ASTree<Token> parse(List<Token> tokenList){
		return parseExpression(tokenList);
	}
	
	private static ASTree<Token> parseExpression(List<Token> tokenList){
		for(TokenType type : MATH_OPERATORS){
			SplitArray<Token> split = split(tokenList, new Token(type, ""));
			if(split!=null){
				return generateTree(split);
			}
		}
		for(TokenType type : BOOLEAN_OPERATORS){
			SplitArray<Token> split = split(tokenList, new Token(type, ""));
			if(split!=null){
				return generateTree(split);
			}
		}
		if(tokenList.size()==1){
			Token token = tokenList.get(0);
			return new ASTree<Token>(token);
		}
		return null;
	}
	
	private static ASTree<Token> generateTree(SplitArray<Token> split){
		ASTree<Token> tree = new ASTree<Token>(split.splitValue);
		tree.addBranch(parseExpression(split.left));
		tree.addBranch(parseExpression(split.right));
		return tree;
	}
	
	private static SplitArray<Token> split(List<Token> tokenList, Token splitToken){
		int index = -1;
		for(int i = 0; i < tokenList.size(); i++){
			if(tokenList.get(i).equals(splitToken)){
				index = i;
				break;
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
