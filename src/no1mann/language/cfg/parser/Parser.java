package no1mann.language.cfg.parser;

import java.util.ArrayList;
import java.util.List;

import no1mann.language.cfg.exceptions.ParseException;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

/*
 * Main Parser class that holds all parsing functions
 */
public class Parser {
	
	/*
	 * TreeReturn class for tracking position in the tree
	 */
	private class TreeReturn{
		private ASTree<Token> tree;
		@SuppressWarnings("unused")
		private int indexStart, indexEnd;
		public TreeReturn(ASTree<Token> tree, int indexStart, int indexEnd){
			this.tree = tree;
			this.indexStart = indexStart;
			this.indexEnd = indexEnd;
		}
	}
	
	/*
	 * Bounds class for tracking 2 objects. Used for tracking an index
	 */
	private class Bounds<T>{
		public T left, right;
		public Bounds(T one, T two){
			left = one;
			right = two;
		}
	}
	
	/*
	 * Stores a split array with the split value
	 */
	private class SplitArray<T>{
		private List<T> left, right;
		private T splitValue;
		public SplitArray(List<T> left, List<T> right, T splitValue){
			this.left = left;
			this.right = right;
			this.splitValue = splitValue;
		}
	}
	
	//List of variable types
	public static final TokenType[] VALUE_TYPES = {
		TokenType.INT_TYPE,
		TokenType.BOOL_TYPE
	};
	
	/*
	 * List of all math operators
	 * Order of operations is the order of the indices (0 - lowest order, end - highest order)
	 */
	private static final TokenType[] OPERATORS = {
			TokenType.OR, 
			TokenType.AND, 
			TokenType.EQUAL,
			TokenType.NOT_EQUAL,
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
	private static int globalCounter = 0;
	//Global instance for class instantiating
	private static Parser parser = new Parser();
	
	
	/*
	 * 
	 * MAIN PARSING FUNCTIONS ====================================================================================================
	 * 
	 */
	
	
	
	/*
	 * Main Parse Function that returns Abstract Syntax Tree representing the parsed code
	 * List<Token> tokenList holds list of tokenized source code
	 * Throws a Parse Exception when a parse fails
	 */
	public static ASTree<Token> parse(List<Token> tokenList) throws ParseException{
		//Resets counter for parenthesis tracking
		globalCounter = 0;
		//Parses main function. Change this when functions are implemented
		return parseFunction(tokenList, 0, tokenList.size(), TokenType.MAIN);
	}
	
	/*
	 * Parses list of tokens for a function
	 * List<Token> tokenList: list of all tokens in the source code
	 * int startIndex: Index where the function starts for the tokenList
	 * int endIndex: Index where the function ends for the tokenList
	 * TokenType head: type of function. Used for future function implementations
	 */
	private static ASTree<Token> parseFunction(List<Token> tokenList, int startIndex, int endIndex, TokenType head) throws ParseException{
		
		//Parses first statement in the function and returns a tree with that parsed statement and starting index of the next statement 
		TreeReturn ret = parseStatement(tokenList, startIndex, endIndex);
		//Tree instance for entire function. Adds the return tree to the first branch of the tree
		ASTree<Token> tree = new ASTree<Token>(new Token(head)).addBranch(ret.tree);
		
		//Cycles through every statement in the function. Ends when no more statements exist
		while(ret.indexStart < endIndex)
			//Parses next statement and adds it to the next branch in the tree
			tree.addBranch((ret = parseStatement(tokenList, ret.indexStart, tokenList.size())).tree);
		
		return tree;
	}
	
	//Matches token with specific index
	private static boolean match(List<Token> tokenList, TokenType tok, int index){
		return tokenList.size()<=index ? false : tokenList.get(index).equals(tok);
	}
	
	/*
	 * Parses list of tokens for a statement
	 * List<Token> tokenList: list of all tokens in the source code
	 * int startIndex: Index where the statement starts for the tokenList
	 * int endIndex: Index where the function ends for the tokenList
	 */
	private static TreeReturn parseStatement(List<Token> tokenList, int indexStart, int indexEnd) throws ParseException{
		//Empty list or End of statement
		if(indexStart>=tokenList.size() || indexStart==indexEnd)
			return parser.new TreeReturn(new ASTree<Token>(new Token(TokenType.END_OF_STATEMENT)), indexStart, indexEnd);
		//Parses if statement
		if(match(tokenList, TokenType.IF, indexStart)){
			return parseIfStatement(tokenList, indexStart, indexEnd);
		}
		//Parses while statement
		else if(match(tokenList, TokenType.WHILE, indexStart)){
			return parseWhile(tokenList, indexStart, indexEnd);
		}
		//Parses variable assignment
		else if(match(tokenList, TokenType.VAR_NAME, indexStart) && match(tokenList, TokenType.ASSIGN, indexStart+1)){
			return parseAssignment(tokenList, indexStart, indexEnd);
		}
		//Parses print statement
		else if(match(tokenList, TokenType.PRINT, indexStart)){
			return parsePrint(tokenList, indexStart, indexEnd);
		}
		else {
		//Parses declaration of variable
			for(TokenType tok : VALUE_TYPES){
				if(match(tokenList, tok, indexStart) && match(tokenList, TokenType.VAR_NAME, indexStart+1) && match(tokenList, TokenType.SEMICOLON, indexStart+2))
					return parseDecleration(tokenList, indexStart, indexEnd, tok);
			}
		}
		
		return null;
	}
	
	/*
	 * Parses list of tokens for an if statement
	 * List<Token> tokenList: list of all tokens in the source code
	 * int startIndex: Index where the if statement starts for the tokenList
	 * int endIndex: Index where the function ends for the tokenList
	 */
	private static TreeReturn parseIfStatement(List<Token> tokenList, int indexStart, int indexEnd) throws ParseException {
		//If the first token in the list is not an if token, failed to parse
		if(!match(tokenList, TokenType.IF, indexStart))
			throw new ParseException("Error parsing if statement");
		
		//EXPRESSION ------
		
		//Stores the indices for the if statement expression that needs to be evaluated
		Bounds<Integer> expBounds = getParenBounds(tokenList, indexStart, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
		//Parses the tree in between those bounds
		ASTree<Token> exp = parseExpression(new ArrayList<Token>(tokenList.subList(expBounds.left, expBounds.right+1)));
		
		//TRUE STATEMENTS -------
		
		//Stores the indices for the true statement
		Bounds<Integer> trueBounds = getParenBounds(tokenList, (expBounds.right+1), TokenType.LEFT_BRACKET, TokenType.RIGHT_BRACKET);
		//Parses the true portion of the statement
		ASTree<Token> tree = parseFunction(tokenList, trueBounds.left+1, trueBounds.right, TokenType.TRUE_STATEMENT);
		
		//The return tree for the entire if statement
		ASTree<Token> ifStatement = (new ASTree<Token>(new Token(TokenType.IF)).addBranch(exp).addBranch(tree));
		int start = trueBounds.right+1;
		
		//FALSE STATEMENTS -------
		
		//Checks if an else statement exists
		if(	match(tokenList, TokenType.RIGHT_BRACKET, trueBounds.right) && match(tokenList, TokenType.ELSE, trueBounds.right+1)){
			//Stores the indices for the else statement
			Bounds<Integer> falseBounds = getParenBounds(tokenList, (trueBounds.right+2), TokenType.LEFT_BRACKET, TokenType.RIGHT_BRACKET);
			//Parses the else portion of the statement
			tree = parseFunction(tokenList, falseBounds.left+1, falseBounds.right, TokenType.FALSE_STATEMENT);
			//Adds the else statement to the tree
			ifStatement.addBranch(tree);
			start = falseBounds.right+1;
			//If a right bracket does not exist, throw an error
			if(!match(tokenList, TokenType.RIGHT_BRACKET, falseBounds.right))
				throw new ParseException("Failure to parse if statement: missing bracket in " + tokenList.toString());
		} 
		//If a right bracket does not exist for the true statement, throw an error
		else if(!match(tokenList, TokenType.RIGHT_BRACKET, trueBounds.right))
			throw new ParseException("Failure to parse if statement: missing bracket in " + tokenList.toString());
		//Returns the final tree
		return parser.new TreeReturn(ifStatement, start, tokenList.size());
	}
	
	/*
	 * Parses list of tokens for a while statement
	 * List<Token> tokenList: list of all tokens in the source code
	 * int startIndex: Index where the while statement starts for the tokenList
	 * int endIndex: Index where the function ends for the tokenList
	 */
	private static TreeReturn parseWhile(List<Token> tokenList, int indexStart, int indexEnd) throws ParseException {
		//If the first token in the list is not a while token, failed to parse
		if(!match(tokenList, TokenType.WHILE, indexStart))
			throw new ParseException("Error parsing while loop");
		
		//Stores the indices for the while statement expression that needs to be evaluated
		Bounds<Integer> expBounds = getParenBounds(tokenList, indexStart, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
		//Parses the tree in between those bounds
		ASTree<Token> exp = parseExpression(new ArrayList<Token>(tokenList.subList(expBounds.left, expBounds.right+1)));
		
		//Stores the indices for the statements in the while loop
		Bounds<Integer> stateBounds = getParenBounds(tokenList, (expBounds.right+1), TokenType.LEFT_BRACKET, TokenType.RIGHT_BRACKET);
		//Parses the tree in between those bounds
		ASTree<Token> tree = parseFunction(tokenList, stateBounds.left+1, stateBounds.right, TokenType.TRUE_STATEMENT);
		
		//If a right bracket doesn't appear at the end of the while loop, throw an error
		if(!match(tokenList, TokenType.RIGHT_BRACKET, stateBounds.right))
			throw new ParseException("Failure to parse if statement: missing bracket in " + tokenList.toString());
		//Returns the new tree
		return parser.new TreeReturn(new ASTree<Token>(new Token(TokenType.WHILE)).addBranch(exp).addBranch(tree), stateBounds.right+1, tokenList.size());
	}
	
	/*
	 * Parses list of tokens for an assignment statement
	 * List<Token> tokenList: list of all tokens in the source code
	 * int startIndex: Index where the assignment statement starts for the tokenList
	 * int endIndex: Index where the function ends for the tokenList
	 */
	private static TreeReturn parseAssignment(List<Token> tokenList, int indexStart, int indexEnd) throws ParseException {
		//If the first token in the list is not a variable name or equal token, failed to parse
		if(!match(tokenList, TokenType.VAR_NAME, indexStart) && match(tokenList, TokenType.ASSIGN, indexStart+1))
			throw new ParseException("Error parsing assignment");
		
		//Finds the index of the semicolon
		int index = findValue(tokenList, new Token(TokenType.SEMICOLON), indexStart);
		//If failed to find a semicolon, throw an error
		if(index==-1)
			throw new ParseException("Failure to parse, missing semicolon");
		//Parses the expression on the right hand side of the assignment
		ASTree<Token> expression = parseExpression(new ArrayList<Token>(tokenList.subList(indexStart+2, index)));
		//Returns the assignment abstract syntax tree
		return parser.new TreeReturn(new ASTree<Token>(new Token(TokenType.VAR_NAME, tokenList.get(indexStart).getValue())).addBranch(expression), index+1, tokenList.size());
	}
	
	/*
	 * Parses list of tokens for a print statement
	 * List<Token> tokenList: list of all tokens in the source code
	 * int startIndex: Index where the print statement starts for the tokenList
	 * int endIndex: Index where the function ends for the tokenList
	 */
	private static TreeReturn parsePrint(List<Token> tokenList, int indexStart, int indexEnd) throws ParseException {
		//If the first token in the list is not a print token, failed to parse
		if(!match(tokenList, TokenType.PRINT, indexStart))
			throw new ParseException("Error parsing print statement");
		
		//Finds the bounds of the expression in the print statement
		Bounds<Integer> expBounds = getParenBounds(tokenList, indexStart+1, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
		//Parses the expression in the print statement
		ASTree<Token> exp = parseExpression(new ArrayList<Token>(tokenList.subList(expBounds.left, expBounds.right+1)));
		
		//If failed to find a semicolon, throw an error
		if(!match(tokenList, TokenType.SEMICOLON, (expBounds.right+1)))
			throw new ParseException("Failure to parse, missing semicolon");
		
		//Returns the print statement for the abstract syntax tree
		return parser.new TreeReturn(new ASTree<Token>(new Token(TokenType.PRINT)).addBranch(exp), expBounds.right+2, tokenList.size());
	}
	
	/*
	 * Parses list of tokens for a print statement
	 * List<Token> tokenList: list of all tokens in the source code
	 * int startIndex: Index where the print statement starts for the tokenList
	 * int endIndex: Index where the function ends for the tokenList
	 * TokenType tok: Variable type
	 */
	private static TreeReturn parseDecleration(List<Token> tokenList, int indexStart, int indexEnd, TokenType tok) {
		//Returns the deceleration statement for the abstract syntax tree
		return parser.new TreeReturn( new ASTree<Token>(new Token(tok, tokenList.get(indexStart+1).getValue())), indexStart+3, tokenList.size());
	}
	
	/*
	 * Parses list of tokens for an expression
	 * List<Token> tokenList: list of all tokens for the expression
	 */
	private static ASTree<Token> parseExpression(List<Token> tokenList) throws ParseException{
		if(tokenList == null)
			return null;
		
		//Parenthesis parser
		ASTree<Token> paren = splitParenthesis(tokenList);
		//If parenthesis found, return the result of the parsing. Else, continue parsing
		if(paren != null)
			return paren;
		
		//Operator parser (order of loop is order of operations )
		for(TokenType type : OPERATORS){
			//Split the token list based on the operator
			SplitArray<Token> split = split(tokenList, new Token(type, ""));
			//If the split was successful, return the generated tree
			if(split!=null)
				return generateTree(split);
		}
		
		//Data type parser - retrieve value from variable or static input
		if(tokenList.size()==1)
			return new ASTree<Token>(tokenList.get(0));
		
		//Failure
		throw new ParseException("Failure to parse " + tokenList.toString());
	}
	
	
	
	/*
	 * 
	 * HELPER FUNCTIONS ====================================================================================================
	 * 
	 */
	
	
	
	//Generates an abstract syntax tree based on a split array
	private static ASTree<Token> generateTree(SplitArray<Token> split) throws ParseException{
		return new ASTree<Token>(split.splitValue)
			.addBranch(parseExpression(split.left))
			.addBranch(parseExpression(split.right));
	}
	
	//Splits a token list based on parenthesis
	private static ASTree<Token> splitParenthesis(List<Token> tokenList) throws ParseException {
		
		//Gets the bounds for the first parenthesis found and its matching right parenthesis
		Bounds<Integer> bounds = getParenBounds(tokenList, 0, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN);
		if(bounds==null)
			return null;
		
		int index = bounds.left, last = bounds.right;
		
		//Tracks how many parenthesis deep the parsing is in
		String val = (globalCounter++) + "";
		//Parses the expression in between the parenthesis
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
	
	/*
	 * Finds token inside a token list
	 * List<Token> tokenList : List of tokens
	 * Token token : Token to find
	 * int startIndex : index to start looking
	 */
	private static int findValue(List<Token> tokenList, Token token, int startIndex){
		int index = -1;
		//Finds index of token
		for (int i = startIndex; i < tokenList.size(); i++) {
			if (tokenList.get(i).equals(token.getTokenType())) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	/*
	 * Prints the list of tokens for debugging
	 */
	@SuppressWarnings("unused")
	private static void printList(List<Token> list){
		for(Token tok : list)
			System.out.print(tok + ", ");
		
		System.out.println();
	}
	
	/*
	 * Gets the parenthesis bounds for a list of tokens
	 */
	private static Bounds<Integer> getParenBounds(List<Token> tokenList, int start, TokenType left, TokenType right) throws ParseException{
		int index = start, size = tokenList.size()-1;
		
		//Finds first parenthesis
		while (!tokenList.get(index).equals(left) && index != size)
			index++;
		
		//If no parenthesis, return null
		if (index == size)
			return null;
		
		//Finds the ending parenthesis
		int last = index, find = 1, count = 0;
		while ((count != find) && last != (size)) {
			last++;
			//Incremental counters for finding matching parenthesis
			if (tokenList.get(last).equals(left))
				find++;
			if (tokenList.get(last).equals(right))
				count++;
		}
		
		//If no matching parenthesis
		if ((last == size) && count != find)
			throw new ParseException("Invalid matching between: " + left + " and " + right);
		
		return new Parser().new Bounds<Integer>(index, last);
	}
	
	/*
	 * Splits a list in half based on the splitToken
	 */
	private static SplitArray<Token> split(List<Token> tokenList, Token splitToken){
		int index = findValue(tokenList, splitToken, 0);
		return index==-1 ? null : new Parser().new SplitArray<Token>(tokenList.subList(0, index), tokenList.subList(index+1, tokenList.size()), tokenList.get(index));
	}
	
}
