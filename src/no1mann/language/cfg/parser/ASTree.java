package no1mann.language.cfg.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Abstract Syntax Tree
 * Standard Tree data structure with any number of branches allowed
 */
public class ASTree<T> implements Iterable<ASTree<T>>, Serializable{
	
	private static final long serialVersionUID = 1L;

	//Each branch of the tree
	private ArrayList<ASTree<T>> branches = new ArrayList<ASTree<T>>();
	
	//Value stored in the tree
	private T value;
	
	public ASTree(T value){
		this.value = value;
	}
	
	//Adds new branch to the tree and returns this instance
	public ASTree<T> addBranch(T t){
		return addBranch(new ASTree<T>(t));
	}
	
	//Adds new branch to the tree and returns this instance
	public ASTree<T> addBranch(ASTree<T> branch){
		branches.add(branch);
		return this;
	}
	
	//Finds branch of tree with specific value and replaces that branch
	public void replace(ASTree<T> tree, T replace){
		//cycles through each branch of this tree
		for(int i = 0; i < branches.size(); i++){
			ASTree<T> branch= branches.get(i);
			//Checks if branch value equals replace value
			if(branch.getValue().equals(replace)){
				//Sets that branch value 
				branches.set(i, tree);
				return;
			}
			else
				//Recursive branch call
				branch.replace(tree, replace);
		}
	}
	
	public T getValue(){
		return value;
	}
	
	public ASTree<T> getBranch(int branchNumber){
		return branches.get(branchNumber);
	}
	
	public List<ASTree<T>> getBranches(){
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
