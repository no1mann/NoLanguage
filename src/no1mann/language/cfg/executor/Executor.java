package no1mann.language.cfg.executor;

import java.util.HashMap;

import no1mann.language.cfg.exceptions.DeclerationException;
import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.parser.ASTree;
import no1mann.language.cfg.parser.Compiler;
import no1mann.language.cfg.parser.Parser;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

public class Executor {

	private static HashMap<String, EnvironmentValue> environment;
	
	public static void execute(ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		environment = new HashMap<String, EnvironmentValue>();
		EnvironmentValue val = executeTree(tree);
		for(String key : environment.keySet()){
			//System.out.println(key + ": " + environment.get(key));
		}
	}
	
	private static EnvironmentValue executeTree(ASTree<Token> tree) throws TypeErrorException, DeclerationException{
		if(tree==null || tree.getValue().equals(TokenType.END_OF_STATEMENT))
			return null;
		Token tok = tree.getValue();
		//0 BRANCHES
		//Value
		if(tree.numberOfBranches()==0){
				if(tok.equals(TokenType.VAR_NAME)){
					//No assignment
					if(!environment.containsKey((String)tok.getValue()))
						throw new DeclerationException("Variable not declared");
					else{
						if(environment.get((String)tok.getValue()) == null)
							throw new DeclerationException("Variable not defined");
						else{
							return environment.get((String)tok.getValue());
						}
					}
				}
			
			if(tok.equals(TokenType.INT_VAL))
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER, Integer.parseInt((String)tok.getValue()));
			else if(tok.equals(TokenType.BOOL_VAL))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN, Boolean.parseBoolean((String)tok.getValue()));
			else
				throw new TypeErrorException("Invalid data type provided of type: " + tok.getTokenType());
		}
		for(TokenType type : Parser.VALUE_TYPES){
			if(tok.equals(type)){
				//No assignment
				if(!environment.containsKey((String)tok.getValue())){
					if(type == TokenType.INT_TYPE)
						environment.put((String)tok.getValue(), new Executor().new EnvironmentValue(EnvironmentType.INTEGER, 0));
					else if(type == TokenType.BOOL_TYPE)
						environment.put((String)tok.getValue(), new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN, false));
					return executeTree(tree.getBranch(0));
				}
				else
					throw new DeclerationException("Variable already defined");
			}
		}
		//1 BRANCHES
		EnvironmentValue left = executeTree(tree.getBranch(0));
		String leftValue = (String) (left.value+"");
		if (tok.equals(TokenType.PRINT)){
			System.out.println(leftValue);
			return executeTree(tree.getBranch(1));
		}
		else if (tok.equals(TokenType.VAR_NAME)){
			if(environment.containsKey((String)tok.getValue())){
				EnvironmentValue val = environment.get(tok.getValue());
				for(EnvironmentType type : EnvironmentType.values()){
					if(val.type == type)
						environment.put((String)tok.getValue(), new Executor().new EnvironmentValue(type, leftValue));
				}
				return executeTree(tree.getBranch(1));
			}
			else
				throw new DeclerationException("Variable \"" + (String)tok.getValue() + "\" not defined");
		}
		else if (tok.equals(TokenType.IF)){
			boolean result = Boolean.parseBoolean(leftValue);
			if(result){
				executeTree(tree.getBranch(1));
				if(tree.numberOfBranches()==4)
					executeTree(tree.getBranch(3));
				else
					executeTree(tree.getBranch(2));
			}
			else{
				executeTree(tree.getBranch(2));
				executeTree(tree.getBranch(3));
			}
			return null;
		}
		
		//2 BRANCHES
		EnvironmentValue right = executeTree(tree.getBranch(1));
		String rightValue = (String) (right.value+"");
		//INTEGER OPERATORS
		if (left.type == EnvironmentType.INTEGER && right.type == EnvironmentType.INTEGER && tree.numberOfBranches()==2) {
			//Plus
			if (tok.equals(TokenType.PLUS)) 
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) + Integer.parseInt(rightValue));
			//Subtract
			else if (tok.equals(TokenType.SUB)) 
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) - Integer.parseInt(rightValue));
			//Multiply
			else if (tok.equals(TokenType.MULT)) 
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) * Integer.parseInt(rightValue));
			//Divide
			else if (tok.equals(TokenType.DIV)) 
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) / Integer.parseInt(rightValue));
			//Power
			else if (tok.equals(TokenType.POW)) 
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						((Double)Math.pow(Integer.parseInt(leftValue), Integer.parseInt(rightValue))).intValue());
			//Modulus
			else if (tok.equals(TokenType.MOD)) 
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) % Integer.parseInt(rightValue));
			//Equal
			else if (tok.equals(TokenType.EQUAL)) 
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Integer.parseInt(leftValue) == Integer.parseInt(rightValue));
			//Not Equal
			else if (tok.equals(TokenType.NOT_EQUAL)) 
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Integer.parseInt(leftValue) != Integer.parseInt(rightValue));
			//Greater than or equal
			else if (tok.equals(TokenType.GREATER_EQUAL)) 
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Integer.parseInt(leftValue) >= Integer.parseInt(rightValue));
			//Greater than
			else if (tok.equals(TokenType.GREATER)) 
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Integer.parseInt(leftValue) > Integer.parseInt(rightValue));
			//Less equal
			else if (tok.equals(TokenType.LESS_EQUAL))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Integer.parseInt(leftValue) <= Integer.parseInt(rightValue));
			//Less
			else if (tok.equals(TokenType.LESS))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Integer.parseInt(leftValue) < Integer.parseInt(rightValue));
			//Error: Invalid operator
			else
				throw new TypeErrorException("Invalid operator provided of type: " + tok.getTokenType());
		}
		
		//BOOLEAN OPERATORS
		else if (left.type == EnvironmentType.BOOLEAN && right.type == EnvironmentType.BOOLEAN && tree.numberOfBranches()==2) {
			//And
			if (tok.equals(TokenType.AND))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Boolean.parseBoolean(leftValue) && Boolean.parseBoolean(rightValue));
			//Or
			else if (tok.equals(TokenType.OR))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Boolean.parseBoolean(leftValue) || Boolean.parseBoolean(rightValue));
			//Equal
			else if (tok.equals(TokenType.EQUAL))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Boolean.parseBoolean(leftValue) == Boolean.parseBoolean(rightValue));
			//Not Equal
			else if (tok.equals(TokenType.NOT_EQUAL))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN,
						Boolean.parseBoolean(leftValue) != Boolean.parseBoolean(rightValue));
			else
				throw new TypeErrorException("Invalid operator provided of type: " + tok.getTokenType());
		}
		
		//Type error
		throw new TypeErrorException("Invalid data type provided of type: " + tok.getTokenType());
	}
	
	private class EnvironmentValue{
		private EnvironmentType type;
		private Object value;
		public EnvironmentValue(EnvironmentType type, Object value){
			this.type = type;
			this.value = value;
		}
		public String toString(){
			if(value instanceof Integer){
				return (Integer)value + "";
			}
			else if(value instanceof Boolean){
				return (Boolean)value + "";
			}
			return (String)value;
		}
	}
	
	private enum EnvironmentType{
		INTEGER,
		BOOLEAN;
	}

}
