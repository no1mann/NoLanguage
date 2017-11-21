package no1mann.language.cfg.parser;

import java.util.ArrayList;
import java.util.Iterator;

public class ASTree<T> implements Iterable<ASTree<T>>{
	
	private ArrayList<ASTree<T>> branches = new ArrayList<ASTree<T>>();
	private T value;
	
	public ASTree(T token){
		this.value = token;
	}
	
	public void addBranch(T t){
		addBranch(new ASTree<T>(t));
	}
	
	public ASTree<T> addBranch(ASTree<T> branch){
		branches.add(branch);
		return this;
	}
	
	public void replace(ASTree<T> tree, T replace){
		for(int i = 0; i < branches.size(); i++){
			ASTree<T> branch= branches.get(i);
			if(branch.getValue().equals(replace)){
				branches.set(i, tree);
				return;
			}
			else
				branch.replace(tree, replace);
		}
	}
	
	public T getValue(){
		return value;
	}
	
	public ASTree<T> getBranch(int branchNumber){
		return branches.get(branchNumber);
	}
	
	public ArrayList<ASTree<T>> getBranches(){
		return branches;
	}
	
	public int numberOfBranches(){
		return branches.size();
	}

	@Override
	public Iterator<ASTree<T>> iterator() {
		return branches.iterator();
	}
	
}
