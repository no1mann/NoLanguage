package no1mann.language.cfg.parser;

import java.util.ArrayList;

public class ASTree<T> {
	
	private ArrayList<ASTree<T>> branches = new ArrayList<ASTree<T>>();
	private T t;
	
	public ASTree(T token){
		this.t = token;
	}
	
	public void addBranch(ASTree<T> branch){
		branches.add(branch);
	}
	
	public T getValue(){
		return t;
	}
	
	public ASTree<T> getBranch(int branchNumber){
		return branches.get(branchNumber);
	}
	
	public ArrayList<ASTree<T>> getBranches(){
		return branches;
	}
	
}
