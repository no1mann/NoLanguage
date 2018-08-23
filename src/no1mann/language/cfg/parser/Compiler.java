package no1mann.language.cfg.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.DeclerationException;
import no1mann.language.cfg.exceptions.InvalidInputException;
import no1mann.language.cfg.exceptions.ParseException;
import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.executor.Executor;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.Tokenize;

/*
 * Compiler class that takes source code and compiles it
 */
public class Compiler {

	//Logging Time
	private static final SimpleDateFormat LOG_TIME = new SimpleDateFormat("HH:mm:ss.SSS");
	
	//Source code file
	private SourceFile file;
	//Compiled code in abstract syntax tree
	private ASTree<Token> tree;
	
	public Compiler(SourceFile file){
		this.file = file;
	}
	
	//Compiles source code
	public void compile() throws ParseException, InvalidInputException{
		tree = Parser.parse(Tokenize.tokenize(file));
	}
	
	//Executes compiled code
	public void execute() throws TypeErrorException, DeclerationException{
		Executor.printToOutput("Executing code at " + LOG_TIME.format(new Date()));
		Executor.printToOutput("------------------------------------------------");
		long time = System.currentTimeMillis();
		Executor.execute(tree);
		long done = System.currentTimeMillis();
		try {
			Executor.waitForPrinting();
		} catch (InterruptedException e) { e.printStackTrace(); }
		System.out.println("------------------------------------------------");
		System.out.println("Finished execution at " + LOG_TIME.format(new Date()) + " (" + ((double)(done-time)/1000.0) + "s)");
	}
	
	//Prints the compiled abstract syntax tree
	public void printTree(){
		System.out.println(printTree(tree, 0));
	}
	
	/*
	 * Prints the abstract syntax tree for debugging
	 */
	public static String printTree(ASTree<Token> sTree, int count){
		String s = "";
		if(sTree==null)
			return s;
		for(int i = 0; i < count; i++)
			s+="\t";
		s += (sTree.getValue() + "\n");
		for(ASTree<Token> newTree : sTree.getBranches())
			s += printTree(newTree, count+1);
		return s;
	}
	
}
