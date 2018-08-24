package no1mann.language;

import java.util.Scanner;

import no1mann.language.cfg.SourceFile;
import no1mann.language.cfg.parser.Compiler;

public class Main {

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		
		while(true){
			try {
				
				System.out.print("> ");
				String line = scanner.nextLine();
				String[] cmd = line.split("\\W");

				if (cmd[0].equalsIgnoreCase("compile")) {
					String file = line.substring("compile ".length(), line.length());
					Compiler compile = null;
					if (file.endsWith(".no"))
						compile = new Compiler(file);
					else{
						compile = new Compiler(new SourceFile(file));
						compile.compile();
					}

					System.out.println("Run or Export? (R/E)");
					String input = scanner.nextLine();

					if (input.equalsIgnoreCase("R"))
						compile.execute();
					else if (input.equalsIgnoreCase("E"))
						compile.saveCompiledCode();
					else
						throw new Exception("Invalid input...");
				} 
				else if(cmd[0].equalsIgnoreCase("execute")){
					String file = line.substring("execute ".length(), line.length());
					if (file.endsWith(".no")){
						Compiler compile = new Compiler(file);
						compile.execute();
					} else{
						throw new Exception("Error: File not compiled No code...");
					}
				}
				else if(cmd[0].equalsIgnoreCase("exit") || cmd[0].equalsIgnoreCase("quit"))
					System.exit(0);
				
				System.out.println();
				Thread.sleep(100);
			} catch (Exception e){
				e.printStackTrace(); 
			}
		}
		
	}
}
