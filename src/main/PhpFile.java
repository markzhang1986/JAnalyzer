package main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;

import utils.GeneralUtils;

public class PhpFile {
	private List<Stmt> stmts;
	public Set<String> shadowVars;
	public Map<String, PhpExprType> varsType;
	
	public Stmt StmtOfInterest;
	
	public PhpFile () {
		
		stmts = new ArrayList<Stmt>();
		shadowVars = new HashSet<String>();
		varsType = new HashMap<String, PhpExprType>();
		
	}
	
	public PhpFile (Node root) throws Exception {
		
		stmts = new ArrayList<Stmt>();
		shadowVars = new HashSet<String>();
		varsType = new HashMap<String, PhpExprType>();
		ReadFileFromRoot(root);
		
	}
	
	public void AddStmt (Stmt newStmt) throws Exception {
		
		if (newStmt == null)
		{
			throw new Exception("Can't add null as statement.");
		}
		
		stmts.add(newStmt);
		
	}
	
	public PhpExprType getVarType(String var) throws Exception {
		
		return varsType.get(GeneralUtils.getNameFromVar(var));
		
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
			
			assignStmts.addAll(stmts.get(i).getAllAssignStmts());
			
		}
		
		return assignStmts;
		
	}
	
	public List<Stmt> getAllLoopStmts() {
		
		List<Stmt> loopStmts = new ArrayList<Stmt>();
		
		for (int i = 0; i < stmts.size(); i++) {
			
			loopStmts.addAll(stmts.get(i).getAllLoopStmts());
			
		}
		
		return loopStmts;
		
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
	
	// Output the php file into a smtlib format string, from line "start" to line "end".
	// We and counting only on the "startLine" for each statement, for example, if a loop starts
	// at line 10 and ends at line 20, we count this loop statement as at line 10. However, the 
	// statements in the loop body has their position as well. For the same example, if "end = 15",
	// The statements starts at 15 to 20 will not be output, even we know they belongs to the loop
	// at line 10.
	public String toSmtLib(int start, int end) throws Exception {
		
		String retString = "";
		
		// Declare variables
		
		Set<String> vars = this.GetAllVars(true);
		
		// Add the shadow variable
		vars.addAll(shadowVars);
		
		for (String var : vars) {
			
			switch(getVarType(var)) {
			case BOOL:
				retString = retString.concat("(declare-fun " + var + " () Bool)\n");
				break;
			case INT:
				retString = retString.concat("(declare-fun " + var + " () Int)\n");
				break;
			case STR:
				retString = retString.concat("(declare-fun " + var + " () String)\n");
				break;
			default:
				retString = retString.concat("(declare-fun " + var + " () String)\n");
				break;
			}
			
		}
		

		retString = retString.concat("\n");
		
		// Formulate the program
		
		for (int i = 0; i < this.stmts.size(); i++) {
			
			Stmt stmt = stmts.get(i);
			if (stmt.stmtType != StmtType.SKIP && stmt.startLine >= start && stmt.startLine <= end) {
				
				retString = retString.concat("(assert ");
				retString = retString.concat(stmt.toFormula(start, end));
				retString = retString.concat(" )\n");
			
			}		
		}
		
		retString = retString.concat("\n");
		
		// State the negation of the goal
		// [DEBUG] Disabling property check for the moment
		/*
		retString = retString.concat("(assert ");
		
		String pc = this.StmtOfInterest.GetPathConditionString();
		
		retString = retString.concat("(and ");
		
		retString = retString.concat(pc);
		retString = retString.concat("##PUT YOUR GOAL HERE##");
		
		retString = retString.concat(" )");
		retString = retString.concat(" )\n");
		*/
		
		return retString;
	}
	

}
