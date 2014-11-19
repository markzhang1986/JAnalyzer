package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class PhpFile {
	private List<Stmt> stmts;
	
	public PhpFile () {
		
		stmts = new ArrayList<Stmt>();
		
	}
	
	public PhpFile (List<Stmt> new_stmts) {
		
		stmts = new_stmts;
		
	}
	
	public PhpFile (Node root) throws Exception {
		
		stmts = new ArrayList<Stmt>();
		ReadFileFromRoot(root);
		
	}
	
	public void AddStmt (Stmt newStmt) throws Exception {
		
		if (newStmt == null)
		{
			throw new Exception("Can't add null as statement.");
		}
		
		stmts.add(newStmt);
		
	}
	
	/**
	 * Top level function when you read in a php file and parse it into a list of statements and store them in this PhpFile object.
	 * 
	 * @param root The root of the dom elements containing all the statement information
	 * @throws Exception 
	 */
	private void ReadFileFromRoot (Node root) throws Exception {
		
		stmts = Stmt.ReadStmtsFromArray(root);
		
	}
	
	public void printPhpFile() {
		
		
		//DEBUGGING
		System.out.println("size of phpfile: " + stmts.size());
		
		for(int i = 0; i < stmts.size(); i++) {
			
			stmts.get(i).printStmt();
			
		}
	}
	

}
