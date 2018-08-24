package no1mann.language;

import java.io.IOException;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.exceptions.DeclerationException;
import no1mann.language.cfg.exceptions.InvalidInputException;
import no1mann.language.cfg.exceptions.ParseException;
import no1mann.language.cfg.exceptions.TypeErrorException;
import no1mann.language.cfg.parser.Compiler;

public class Main {

	public static void main(String[] args) {
		try {
			
			
			/*SourceFile file = new SourceFile("src\\no1mann\\language\\test.txt");
			Compiler compile = new Compiler(file);
			compile.compile();
			compile.saveCompiledCode();
			try {
				compile.execute();
			} catch (TypeErrorException | DeclerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			String file = "src\\no1mann\\language\\test.txt.no";
			Compiler compile = new Compiler(file);
			try {
				compile.execute();
			} catch (TypeErrorException | DeclerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
