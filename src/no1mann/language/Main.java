package no1mann.language;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.InvalidInputException;
import no1mann.language.cfg.exceptions.ParseException;
import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.parser.Compiler;

public class Main {

	public static void main(String[] args) {
		try {
			
			
			SourceFile file = new SourceFile("C:\\Users\\Trevor\\Google Drive\\Workspaces\\Eclipse\\NoLanguage\\src\\no1mann\\language\\test.txt");
			Compiler compile = new Compiler(file);
			compile.compile();
			compile.printTree();
			compile.execute();

			
			
		} catch (InvalidInputException e) {
			e.printStackTrace();
		} catch (TypeErrorException e) {
			e.printStackTrace();
		} catch (ParseException e){
			e.printStackTrace();
		}
	}
}
