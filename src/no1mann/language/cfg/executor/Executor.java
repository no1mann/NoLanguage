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
		System.out.println((Integer)executeTree(tree).value);
	}
	
	private static EnvironmentValue executeTree(ASTree<Token> tree) throws TypeErrorException{
		Token tok = tree.getValue();
		if(tree.numberOfBranches()==0){
			if(tok.equals(TokenType.INT_VAL)){
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER, tok.getValue());
			}
			else if(tok.equals(TokenType.BOOL_VAL)){
				return new Executor().new EnvironmentValue(EnvironmentType.BOOLEAN, tok.getValue());
			}
			else{
				throw new TypeErrorException("Invalid type provided of type: " + tok.getTokenType());
			}
		}
		EnvironmentValue left = executeTree(tree.getBranch(0));
		EnvironmentValue right = executeTree(tree.getBranch(1));
		String leftValue = (String) (left.value+""), rightValue = (String) (right.value+"");
		if (left.type == EnvironmentType.INTEGER && right.type == EnvironmentType.INTEGER) {
			if (tok.equals(TokenType.PLUS)) {
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) + Integer.parseInt(rightValue));
			} else if (tok.equals(TokenType.SUB)) {
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) - Integer.parseInt(rightValue));
			} else if (tok.equals(TokenType.MULT)) {
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) * Integer.parseInt(rightValue));
			} else if (tok.equals(TokenType.DIV)) {
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) / Integer.parseInt(rightValue));
			} else if (tok.equals(TokenType.POW)) {
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						((Double)Math.pow(Integer.parseInt(leftValue), Integer.parseInt(rightValue))).intValue());
			} else if (tok.equals(TokenType.MOD)) {
				return new Executor().new EnvironmentValue(EnvironmentType.INTEGER,
						Integer.parseInt(leftValue) % Integer.parseInt(rightValue));
			}
		}
		else{
			throw new TypeErrorException("Invalid type provided of type: " + tok.getTokenType());
		}
		return null;
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
