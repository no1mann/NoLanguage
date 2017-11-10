package no1mann.language.cfg;

public class Tokenize {

	private SourceFile file;
	
	public Tokenize(SourceFile file){
		
	}
	
	private void tokenize(){
		//Splits tokens up
		String[] tokens = file.getData().split(CFG.WHITE_SPACE);
		
		//Cycles through all tokens
		for(String token : tokens){
			
		}
		
		//Clears source file contents
		file.clear();
	}

}
