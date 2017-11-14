package no1mann.language.cfg.executor;

import java.util.HashMap;

import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.parser.ASTree;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.TokenType;

public class Executor {

	private static HashMap<String, EnvironmentValue> environment;
	
	public static void execute(ASTree<Token> tree) throws TypeErrorException{
		environment = new HashMap<String, EnvironmentValue>();
		EnvironmentValue val = executeTree(tree);
		if(val.type==EnvironmentType.INTEGER)
			System.out.println((Integer)executeTree(tree).value);
		else if(val.type==EnvironmentType.BOOLEAN)
			System.out.println((Boolean)executeTree(tree).value);
		
	}
	
	private static EnvironmentValue executeTree(ASTree<Token> tree) throws TypeErrorException{
		Token tok = tree.getValue();
		//Value
		if(tree.numberOfBranches()==0){
			if(tok.equals(TokenType.INT_VAL))
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER, Integer.parseInt((String)tok.getValue()));
			else if(tok.equals(TokenType.BOOL_VAL))
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN, Boolean.parseBoolean((String)tok.getValue()));
			else
				throw new TypeErrorException("Invalid data type provided of type: " + tok.getTokenType());
		}
		//Values
		EnvironmentValue left = executeTree(tree.getBranch(0));
		String leftValue = (String) (left.value+"");
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
	}
	
	private enum EnvironmentType{
		INTEGER,
		BOOLEAN;
	}

}
