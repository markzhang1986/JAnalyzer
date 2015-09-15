package main;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

public class PhpFile {
	private List<Stmt> stmts;
	public Set<String> shadowVars;
	
	public Stmt StmtOfInterest;
	
	public PhpFile () {
		
		stmts = new ArrayList<Stmt>();
		shadowVars = new HashSet<String>();
		
	}
	
	public PhpFile (List<Stmt> new_stmts) {
		
		stmts = new_stmts;
		shadowVars = new HashSet<String>();
		
	}
	
	public PhpFile (Node root) throws Exception {
		
		stmts = new ArrayList<Stmt>();
		shadowVars = new HashSet<String>();
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
		
		stmts = Stmt.ReadStmtsFromArray(root, this, null);
		
	}
	
	/**
	 * Print out the php file
	 */
	public void printPhpFile() {
		
		if (JAnalyzer.DEBUG_MODE >= 10) {
			System.out.println("size of phpfile: " + stmts.size());
		}
		
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
	
	public Set<String> GetAllVars(boolean withPosition) {
		
		Set<String> allVars = new HashSet<String>();
		
		for (int i = 0; i < stmts.size(); i++) {
			
			allVars.addAll(stmts.get(i).GetDefVarsRecur(withPosition));
			allVars.addAll(stmts.get(i).GetUseVarsRecur(withPosition));
			
		}
		
		return allVars;
		
	}
	
	public List<String> GetAllDefVars(boolean withPosition) {
		
		List<String> allVars = new ArrayList<String>();
		
		for (int i = 0; i < stmts.size(); i++) {
			
			allVars.addAll(stmts.get(i).GetDefVarsRecur(withPosition));
			
		}
		
		return allVars;
		
	}
	
	public List<String> GetAllUseVars(boolean withPosition) {
		
		List<String> allVars = new ArrayList<String>();
		
		for (int i = 0; i < stmts.size(); i++) {
			
			allVars.addAll(stmts.get(i).GetUseVarsRecur(withPosition));
			
		}
		
		return allVars;
		
	}
	
	public Stmt GetPreStmtInterLayer(Stmt targetStmt) {
		
		if (targetStmt.preStmt != null ) {
			
			return targetStmt.preStmt;
			
		}
		
		if (targetStmt.parentStmt != null) {
			
			return GetPreStmtInterLayer(targetStmt.parentStmt);
			
		}
		
		return null;
		
	}
	
	public String toSmtLib() {
		
		String retString = "";
		
		// Declare variables
		
		Set<String> vars = this.GetAllVars(true);
		
		// Add the shadow variable
		vars.addAll(shadowVars);
		
		for (String var : vars) {
			
			retString = retString.concat("(declare-fun " + var + " () String)\n");
			
		}
		

		retString = retString.concat("\n");
		
		// Formulate the program
		
		for (int i = 0; i < this.stmts.size(); i++) {
			
			Stmt stmt = stmts.get(i);
			if (stmt.stmtType != StmtType.SKIP) {
				
				retString = retString.concat("(assert ");
				retString = retString.concat(stmt.toFormula());
				retString = retString.concat(" )\n");
			
			}		
		}
		
		retString = retString.concat("\n");
		
		// State the negation of the goal
		
		retString = retString.concat("(assert ");
		
		String pc = this.StmtOfInterest.GetPathConditionString();
		
		retString = retString.concat("(and ");
		
		retString = retString.concat(pc);
		retString = retString.concat("##PUT YOUR GOAL HERE##");
		
		retString = retString.concat(" )");
		retString = retString.concat(" )\n");
		
		return retString;
	}
	

}
