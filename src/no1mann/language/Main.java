package no1mann.language;

import java.util.List;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.InvalidInputException;
import no1mann.language.cfg.token.Token;
import no1mann.language.cfg.token.Tokenize;

public class Main {

	public static void main(String[] args) {
		SourceFile file = new SourceFile("C:\\Users\\Trevor\\Google Drive\\Workspaces\\Eclipse\\NoLanguage\\src\\no1mann\\language\\test.txt");
		List<Token> tokens;
		try {
			tokens = Tokenize.tokenize(file);
			for(Token token : tokens){
				System.out.println(token);
			}
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
