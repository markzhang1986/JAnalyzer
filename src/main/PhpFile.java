package main;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

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
	
	/**
	 * Print out the php file
	 */
	public void printPhpFile() {
		
		//DEBUGGING
		System.out.println("size of phpfile: " + stmts.size());
		
		for(int i = 0; i < stmts.size(); i++) {
			
			stmts.get(i).printStmt();
			
		}
	}
	
	public List<Stmt> getAllAssignStmts() {
		
		List<Stmt> assignStmts = new ArrayList<Stmt>();
		
		for (int i = 0; i < stmts.size(); i++) {
			
			assignStmts.addAll(stmts.get(i).GetAllAssignStmt());
			
		}
		
		return assignStmts;
		
	}
	
	public List<String> GetAllVars(boolean withPosition) {
		
		List<String> allVars = new ArrayList<String>();
		
		for (int i = 0; i < stmts.size(); i++) {
			
			allVars.addAll(stmts.get(i).GetDefVarsRecur(withPosition));
			allVars.addAll(stmts.get(i).GetUseVarsRecur(withPosition));
			
		}
		
		return allVars;
		
	}
	
	public String toSmtLib() {
		
		String retString = "";
		
		List<String> vars = this.GetAllVars(true);
		
		for (int i = 0; i < vars.size(); i++) {
			
			retString = retString.concat("(declare-fun " + vars.get(i) + " () String)\n");
			
		}
		
		retString = retString.concat("\n");
		
		for (int i = 0; i < this.stmts.size(); i++) {
			
			Stmt stmt = stmts.get(i);
			if (stmt.stmtType != StmtType.SKIP) {
				
				retString = retString.concat("(assert ");
				retString = retString.concat(stmt.toFormula());
				retString = retString.concat(" )\n");
			
			}		
		}
		
		retString = retString.concat("\n");
		
		return retString;
	}
	

}
