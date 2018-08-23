package no1mann.language.cfg.executor;

import no1mann.language.cfg.exceptions.TypeErrorException;

/*
 * The Environment class keeps track of variable values in the virtual environment
 * Newly instantiated variables are given a default value
 * A HashMap keeps track of all variables in the virtual environment 
 */
public class Environment {

	private EnvironmentValue[] environment = new EnvironmentValue[10000];
	
	public Environment(){
		environment = new EnvironmentValue[10000];
	}
	
	/*
	 * Instantiates a new variable of the given type
	 * String var : variable name
	 * EnvironmentType type : variable type (integer, boolean, etc.)
	 */
	public void instantiate(int pointer, EnvironmentType type){
		environment[pointer] = new EnvironmentValue(type, type.getDefaultValue());
	}
	
	/*
	 * Checks if a variable is instantiated
	 */
	public boolean isInstantiated(int pointer){
		return environment[pointer]!=null;
	}
	
	/*
	 * Updates the value of a variable
	 */
	public void set(int pointer, Object val){
		environment[pointer].value = val;
	}
	
	/*
	 * Gets the current value of a variable
	 */
	public Object getVariable(int pointer){
		return environment[pointer].value;
	}
	
	/*
	 * Gets the boolean value of a variable
	 * A TypeErrorException is thrown if the variable is not a boolean
	 */
	public boolean getBool(int pointer) throws TypeErrorException{
		EnvironmentValue val = environment[pointer];
		if(val.type == EnvironmentType.BOOLEAN)
			return (boolean)val.value;
		throw new TypeErrorException("Variable is not a boolean");
	}
	
	/*
	 * Gets the integer value of a variable
	 * A TypeErrorException is thrown if the variable is not an integer
	 */
	public long getInt(int pointer) throws TypeErrorException{
		EnvironmentValue val = environment[pointer];
		if(val.type == EnvironmentType.INTEGER)
			return (long)val.value;
		throw new TypeErrorException("Variable is not a integer");
	}
	
	/*
	 * The EnvironmentValue class stores the value of a single variable
	 */
	private class EnvironmentValue{
		
		private EnvironmentType type;
		private Object value;
		
		/*
		 * EnvironmentType type : variable type (integer, boolean, etc.)
		 * Object value : value of variable
		 */
		public EnvironmentValue(EnvironmentType type, Object value){
			this.type = type;
			this.value = value;
		}
		
		public String toString(){
			if(value instanceof Long)
				return (Long)value + "";
			else if(value instanceof Boolean)
				return (Boolean)value + "";
			return (String)value;
		}
	}
}
