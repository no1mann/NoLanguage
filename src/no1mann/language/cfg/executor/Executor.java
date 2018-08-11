package no1mann.language.cfg.executor;

import no1mann.language.cfg.exceptions.DeclerationException;
import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.parser.ASTree;
import no1mann.language.cfg.parser.Parser;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

/*
 * Executes compiled code
 */
public class Executor {
	
	/*
	 * Executes compiled code stored in an Abstract Syntax Tree
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	public static void execute(ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		executeFunction(new Environment(), tree);
	}
	
	/*
	 * Executes compiled function stored in an Abstract Syntax Tree with a given Environment
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static void executeFunction(Environment env, ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		//Cycles through every statement in the function and executes it
		for(ASTree<Token> branch : tree)
			executeStatement(env, branch);
	}
	
	/*
	 * Executes compiled statement stored in an Abstract Syntax Tree with a given Environment
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static void executeStatement(Environment env, ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		//If the statement has finished being executed
		if(tree==null || tree.getValue().equals(TokenType.END_OF_STATEMENT))
			return;
		
		//Gets current token of root of tree
		Token tok = tree.getValue();
		//Checks if the token is variable that needs to be instantiated
		for(TokenType type : Parser.VALUE_TYPES){
			if(tok.equals(type))
				executeInstantiate(tok, env);
		}
		//Checks if the token is a print statement
		if (tok.equals(TokenType.PRINT))
			executePrint(tree.getBranch(0), env);
		//Checks if the token is a variable assignment
		else if (tok.equals(TokenType.VAR_NAME))
			executeAssignment(tok.getValue(), tree.getBranch(0), env);
		//Checks if the token is an if statement
		else if (tok.equals(TokenType.IF)){
			//If statement with else statement
			if(tree.numberOfBranches()==4)
				executeIf(tree.getBranch(0), tree.getBranch(1), tree.getBranch(2), env);
			//No else statement
			else
				executeIf(tree.getBranch(0), tree.getBranch(1), null, env);
		}
		//Checks if the token is a while loop
		else if (tok.equals(TokenType.WHILE))
			executeWhile(tree.getBranch(0), tree.getBranch(1), env);
	}
	
	/*
	 * Executes a variable instantiation for a given environment
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static void executeInstantiate(Token tok, Environment env) throws DeclerationException{
		//If variable is not instantiated
		if(!env.isInstantiated(tok.getValue())){
			for(EnvironmentType envType : EnvironmentType.values()){
				//Finds EnvironmentType that matches token
				if(tok.getTokenType() == envType.getMatchingToken())
					env.instantiate(tok.getValue(), envType);
			}
		//Variable already declared
		} else
			throw new DeclerationException("Variable already defined");
	}
	
	/*
	 * Executes compiled print statement with given environment
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static void executePrint(ASTree<Token> expression, Environment env) throws TypeErrorException, DeclerationException{
		System.out.println(executeExpression(env, expression).toString());
	}
	
	/*
	 * Executes compiled assignment statement
	 * String var: variable name to assign the new value
	 * ASTree expression : expression to execute, result is updated value
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static void executeAssignment(String var, ASTree<Token> expression, Environment env) throws TypeErrorException, DeclerationException{
		if(env.isInstantiated(var))
			env.set(var, executeExpression(env, expression));
		else
			throw new DeclerationException("Variable \"" + var+ "\" not defined");
	}
	
	/*
	 * Executes compiled if statement
	 * ASTree expression : expression to execute, result is the true/false statement value
	 * ASTree trueState : statement list if the expression is true
	 * ASTree falseState : statement list if the expression is false
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static void executeIf(ASTree<Token> expression, ASTree<Token> trueState, ASTree<Token> falseState, Environment env) throws TypeErrorException, DeclerationException{
		boolean result = (boolean)executeExpression(env, expression);
		if(result)
			executeFunction(env, trueState);
		else if(falseState != null)
			executeFunction(env, falseState);
	}
	
	/*
	 * Executes compiled while statement
	 * ASTree expression : expression to execute, result is the true/false statement value
	 * ASTree statement : statements to execute if result is true
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static void executeWhile(ASTree<Token> expression, ASTree<Token> statement, Environment env) throws TypeErrorException, DeclerationException{
		@SuppressWarnings("unused")
		boolean state;
		while(state = (boolean)executeExpression(env, expression))
			executeFunction(env, statement);
	}
	
	/*
	 * Executes compiled expression
	 * TypeErrorException is thrown if the variable is not a valid type
	 * DeclerationException is thrown if a variable is already defined
	 */
	private static Object executeExpression(Environment env, ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		
		//Gets head token value
		Token tok = tree.getValue();
		
		//If no branches
		if(tree.numberOfBranches()==0){
			//Integer
			if(tok.equals(TokenType.INT_VAL))
				return Long.parseLong(tok.getValue());
			//Boolean
			else if(tok.equals(TokenType.BOOL_VAL))
				return Boolean.parseBoolean(tok.getValue());
			//Variable
			else if(tok.equals(TokenType.VAR_NAME)){
				if(env.isInstantiated(tok.getValue()))
					return env.getVariable(tok.getValue());
				else
					throw new DeclerationException("Variable not declared");
			}
		} 
		//If 2 branches
		else if(tree.numberOfBranches()==2){
			//Gets left and right branches
			Object left = executeExpression(env, tree.getBranch(0));
			Object right = executeExpression(env, tree.getBranch(1));
			
			//INTEGER OPERATORS
			if (left instanceof Long && right instanceof Long && tree.numberOfBranches()==2) {
				//Plus
				if (tok.equals(TokenType.PLUS)) 
					return (Long)left + (Long)right;
				//Subtract
				else if (tok.equals(TokenType.SUB)) 
					return (Long)left - (Long)right;
				//Multiply
				else if (tok.equals(TokenType.MULT)) 
					return (Long)left * (Long)right;
				//Divide
				else if (tok.equals(TokenType.DIV)) 
					return (Long)left / (Long)right;
				//Power
				else if (tok.equals(TokenType.POW)) 
					return ((Double)Math.pow((Long)left, (Long)right)).longValue();
				//Modulus
				else if (tok.equals(TokenType.MOD)) 
					return (Long)left % (Long)right;
				//Equal
				else if (tok.equals(TokenType.EQUAL)) 
					return (Long)left == (Long)right;
				//Not Equal
				else if (tok.equals(TokenType.NOT_EQUAL)) 
					return (Long)left != (Long)right;
				//Greater equal
				else if (tok.equals(TokenType.GREATER_EQUAL)) 
					return (Long)left >= (Long)right;
				//Greater than
				else if (tok.equals(TokenType.GREATER)) 
					return (Long)left > (Long)right;
				//Less equal
				else if (tok.equals(TokenType.LESS_EQUAL))
					return (Long)left <= (Long)right;
				//Less
				else if (tok.equals(TokenType.LESS))
					return (Long)left < (Long)right;
				//Error: Invalid operator
				else
					throw new TypeErrorException("Invalid operator provided of type: " + tok.getTokenType());
			}
			
			//BOOLEAN OPERATORS
			else if (left instanceof Boolean && right instanceof Boolean  && tree.numberOfBranches()==2) {
				//And
				if (tok.equals(TokenType.AND))
					return (Boolean)left && (Boolean)right;
				//Or
				else if (tok.equals(TokenType.OR))
					return (Boolean)left || (Boolean)right;
				//Equal
				else if (tok.equals(TokenType.EQUAL))
					return (Boolean)left == (Boolean)right;
				//Not Equal
				else if (tok.equals(TokenType.NOT_EQUAL))
					return (Boolean)left != (Boolean)right;
				else
					throw new TypeErrorException("Invalid operator provided of type: " + tok.getTokenType());
			}
			
		}
		//Type error
		throw new TypeErrorException("Invalid data type provided of type: " + tok.getTokenType());
	}

}
