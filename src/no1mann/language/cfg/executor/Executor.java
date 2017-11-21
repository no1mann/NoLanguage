package no1mann.language.cfg.executor;

import no1mann.language.cfg.exceptions.DeclerationException;
import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.parser.ASTree;
import no1mann.language.cfg.parser.Parser;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

public class Executor {
	
	public static void execute(ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		executeFunction(new Environment(), tree);
	}
	
	private static void executeFunction(Environment env, ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		for(ASTree<Token> branch  : tree){
			executeStatement(env, branch);
		}
	}
	
	private static void executeStatement(Environment env, ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		if(tree==null || tree.getValue().equals(TokenType.END_OF_STATEMENT))
			return;
		Token tok = tree.getValue();
		for(TokenType type : Parser.VALUE_TYPES){
			if(tok.equals(type)){
				executeInstantiate(tok, env);
			}
		}
		if (tok.equals(TokenType.PRINT))
			executePrint(tree.getBranch(0), env);
		else if (tok.equals(TokenType.VAR_NAME))
			executeAssignment(tok.getValue(), tree.getBranch(0), env);
		else if (tok.equals(TokenType.IF)){
			if(tree.numberOfBranches()==4){
				executeIf(tree.getBranch(0), tree.getBranch(1), tree.getBranch(2), env);
			}
			else{
				executeIf(tree.getBranch(0), tree.getBranch(1), null, env);
			}
		}
		else if (tok.equals(TokenType.WHILE)){
			executeWhile(tree.getBranch(0), tree.getBranch(1), env);
		}
	}
	
	private static void executeInstantiate(Token tok, Environment env) throws DeclerationException{
		if(!env.isInstantiated(tok.getValue())){
			for(EnvironmentType envType : EnvironmentType.values()){
				if(tok.getTokenType() == envType.getMatchingToken())
					env.instantiate(tok.getValue(), envType);
			}
		}
		else
			throw new DeclerationException("Variable already defined");
	}
	
	private static void executePrint(ASTree<Token> expression, Environment env) throws TypeErrorException, DeclerationException{
		System.out.println(executeExpression(env, expression).toString());
	}
	
	private static void executeAssignment(String var, ASTree<Token> expression, Environment env) throws TypeErrorException, DeclerationException{
		if(env.isInstantiated(var)){
			env.set(var, executeExpression(env, expression));
		}
		else
			throw new DeclerationException("Variable \"" + var+ "\" not defined");
	}
	
	private static void executeIf(ASTree<Token> expression, ASTree<Token> trueState, ASTree<Token> falseState, Environment env) throws TypeErrorException, DeclerationException{
		boolean result = (boolean)executeExpression(env, expression);
		if(result){
			executeFunction(env, trueState);
		}
		else if(falseState != null){
			executeFunction(env, falseState);
		}
	}
	
	private static void executeWhile(ASTree<Token> expression, ASTree<Token> statement, Environment env) throws TypeErrorException, DeclerationException{
		boolean state = (boolean)executeExpression(env, expression);
		while(state){
			executeFunction(env, statement);
			state = (boolean)executeExpression(env, expression);
		}
	}
	
	private static Object executeExpression(Environment env, ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		Token tok = tree.getValue();
		
		if(tree.numberOfBranches()==0){
			//Integer
			if(tok.equals(TokenType.INT_VAL))
				return Integer.parseInt(tok.getValue());
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
		else if(tree.numberOfBranches()==2){
			Object left = executeExpression(env, tree.getBranch(0));
			Object right = executeExpression(env, tree.getBranch(1));
			if (left instanceof Integer && right instanceof Integer && tree.numberOfBranches()==2) {
				//Plus
				if (tok.equals(TokenType.PLUS)) 
					return (Integer)left + (Integer)right;
				//Subtract
				else if (tok.equals(TokenType.SUB)) 
					return (Integer)left - (Integer)right;
				//Multiply
				else if (tok.equals(TokenType.MULT)) 
					return (Integer)left * (Integer)right;
				//Divide
				else if (tok.equals(TokenType.DIV)) 
					return (Integer)left / (Integer)right;
				//Power
				else if (tok.equals(TokenType.POW)) 
					return ((Double)Math.pow((Integer)left, (Integer)right)).intValue();
				//Modulus
				else if (tok.equals(TokenType.MOD)) 
					return (Integer)left % (Integer)right;
				//Equal
				else if (tok.equals(TokenType.EQUAL)) 
					return (Integer)left == (Integer)right;
				//Not Equal
				else if (tok.equals(TokenType.NOT_EQUAL)) 
					return (Integer)left != (Integer)right;
				//Greater equal
				else if (tok.equals(TokenType.GREATER_EQUAL)) 
					return (Integer)left >= (Integer)right;
				//Greater than
				else if (tok.equals(TokenType.GREATER)) 
					return (Integer)left > (Integer)right;
				//Less equal
				else if (tok.equals(TokenType.LESS_EQUAL))
					return (Integer)left <= (Integer)right;
				//Less
				else if (tok.equals(TokenType.LESS))
					return (Integer)left < (Integer)right;
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
