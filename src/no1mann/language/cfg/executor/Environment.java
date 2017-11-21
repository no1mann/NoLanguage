package no1mann.language.cfg.executor;

import java.util.HashMap;

import no1mann.language.cfg.exceptions.TypeErrorException;

public class Environment {

	private HashMap<String, EnvironmentValue> environment;
	
	public Environment(){
		environment = new HashMap<String, EnvironmentValue>();
	}
	
	public void instantiate(String var, EnvironmentType type){
		environment.put(var, new EnvironmentValue(type, type.getDefaultValue()));
	}
	
	public boolean isInstantiated(String var){
		return environment.containsKey(var);
	}
	
	public void set(String var, Object val) throws TypeErrorException{
		environment.get(var).value = val;
	}
	
	public Object getVariable(String var){
		return environment.get(var).value;
	}
	
	public boolean getBool(String var) throws TypeErrorException{
		EnvironmentValue val = environment.get(var);
		if(val.type == EnvironmentType.BOOLEAN)
			return (boolean)val.value;
		throw new TypeErrorException("Variable " + var + " is not a boolean");
	}
	
	public int getInt(String var) throws TypeErrorException{
		EnvironmentValue val = environment.get(var);
		if(val.type == EnvironmentType.INTEGER)
			return (int)val.value;
		throw new TypeErrorException("Variable " + var + " is not a integer");
	}
	
	private class EnvironmentValue{
		private EnvironmentType type;
		private Object value;
		public EnvironmentValue(EnvironmentType type, Object value){
			this.type = type;
			this.value = value;
		}
		public String toString(){
			if(value instanceof Integer)
				return (Integer)value + "";
			else if(value instanceof Boolean)
				return (Boolean)value + "";
			return (String)value;
		}
	}
}
