package no1mann.language.cfg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Raw source code file for compiling
 */
public class SourceFile extends File{

	private static final long serialVersionUID = 8344796844289554974L;
	
	//Line by line contents of source code file
	private StringBuilder data;
	
	/*
	 * String file: Location of source code file
	 */
	public SourceFile(String file){
		super(file);
		this.data = new StringBuilder();
		load();
	}
	
	/*
	 * Loads file contents into memory
	 */
	private void load(){
		try(Scanner scanner = new Scanner(this)) {
			//Load file contents
			while(scanner.hasNextLine())
				data.append(scanner.nextLine() + " ");
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getData(){
		return data.toString();
	}
	
}
