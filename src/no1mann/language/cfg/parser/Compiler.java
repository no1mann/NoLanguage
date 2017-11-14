package no1mann.language.cfg.parser;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.InvalidInputException;
import no1mann.language.cfg.exceptions.ParseException;
import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.executor.Executor;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.Tokenize;

public class Compiler {

	private SourceFile file;
	private ASTree<Token> tree;
	
	public Compiler(SourceFile file){
		this.file = file;
	}
	
	public void compile() throws ParseException, InvalidInputException{
		tree = Parser.parse(Tokenize.tokenize(file));
	}
	
	public void execute() throws TypeErrorException{
		Executor.execute(tree);
	}
	
	public void printTree(){
		printTree(tree, 0);
	}
	
	private static String printTree(ASTree<Token> tree, int count){
		String s = "";
		for(int i = 0; i < count; i++){
			s+="\t";
		}
		s += (tree.getValue() + "\n");
		for(ASTree<Token> newTree : tree.getBranches()){
			s += printTree(newTree, count+1);
		}
		return s;
	}
	
}
