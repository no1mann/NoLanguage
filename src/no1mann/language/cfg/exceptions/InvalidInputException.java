package no1mann.language.cfg.exceptions;

/*
 * An InvalidInputException is thrown when the source code contains text that can't be tokenized
 */
public class InvalidInputException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidInputException(String exception){
		super(exception);
	}
	
}
