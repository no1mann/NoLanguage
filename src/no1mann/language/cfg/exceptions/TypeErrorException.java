package no1mann.language.cfg.exceptions;

/*
 * A TypeErrorException is throw when a variable is retrieved that is not the correct type
 * Ex. 1+true
 */
public class TypeErrorException extends Exception{

	private static final long serialVersionUID = 1L;

	public TypeErrorException(String str){
		super(str);
	}
}
