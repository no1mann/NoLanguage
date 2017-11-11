package no1mann.language;

import java.util.List;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.InvalidInputException;
import no1mann.language.cfg.parser.ASTree;
import no1mann.language.cfg.parser.Parser;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.Tokenize;

public class Main {

	public static void main(String[] args) {
		try {
			
			
			SourceFile file = new SourceFile("C:\\Users\\Trevor\\Google Drive\\Workspaces\\Eclipse\\NoLanguage\\src\\no1mann\\language\\test.txt");
			
			ASTree<Token> tokens = Parser.parse(Tokenize.tokenize(file));
			System.out.println(printTree(tokens,0));

			
			
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
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
