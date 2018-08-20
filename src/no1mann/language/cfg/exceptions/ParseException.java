package no1mann.language.cfg.exceptions;

/*
 * Main parsing exception for tokens that are missing
 */
public class ParseException extends Exception{

	private static final long serialVersionUID = 1L;
	public ParseException(String s){
		super(s);
	}
}
