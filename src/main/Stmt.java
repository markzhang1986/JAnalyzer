package main;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.DocUtils;

public class Stmt {
	
	private List<Expr> exprs;
	private List<Stmt> body1;
	private List<Stmt> body2;
	
	public StmtType stmtType;
	
	// parent statement and the immediate previous sibling
	public Stmt parentStmt;
	public Stmt preStmt;
	
	public List<Integer> sliceTags;
	
	public Stmt(Node stmtNode) throws Exception {
		
		exprs = new ArrayList<Expr>();
		body1 = new ArrayList<Stmt>();
		body1 = new ArrayList<Stmt>();
		sliceTags = new ArrayList<Integer>();
		
		ReadStmtFromNode(stmtNode);
		
	}
	
	public List<Expr> GetExprs() {
		
		return exprs;
		
	}
	
	public void SetExprs(List<Expr> newExprs) throws Exception {
		
		if (newExprs == null)
			throw new Exception("Can't set expression to null.");
		
		exprs = newExprs;
		return;
	}
	
	public List<Stmt> GetBody1() {
		
		return body1;
		
	}
	
	public void SetBody1(List<Stmt> newBody) throws Exception {
		
		if (newBody == null)
			throw new Exception("Can't set body to null.");
		
		body1 = newBody;
		
		for (int i = 0; i < newBody.size(); i++) {
			
			newBody.get(i).parentStmt = this;
			
			if (i > 0) {
				
				newBody.get(i).preStmt = newBody.get(i - 1);
				
			}
			
		}
		
	}
	
	public List<Stmt> GetBody2() {
		
		return body2;
		
	}
	
	public void SetBody2(List<Stmt> newBody) throws Exception {
		
		if (newBody == null)
			throw new Exception("Can't set body to null.");
		
		body2 = newBody;
		
		for (int i = 0; i < newBody.size(); i++) {
			
			newBody.get(i).parentStmt = this;
			
			if (i > 0) {
				
				newBody.get(i).preStmt = newBody.get(i - 1);
				
			}
			
		}
		
	}
	
	public void printStmt() {
		
		switch (stmtType)
		{
		case SKIP:
			//System.out.println("SKIP;");
			break;
			
		case ASSIGN:
			System.out.println(exprs.get(0).GetString(true) + ":= " + exprs.get(1).GetString(true) + ";");
			break;
			
		case FUNC:
			System.out.println("FUNC(" + exprs.get(0).GetString(true) + ");");
			break;
			
		case ITE:
			
			System.out.println("if (" + exprs.get(0).GetString(true) + ") {");
			System.out.println();
			
			for (int i = 0; i < body1.size(); i++){
				System.out.printf("  ");
				body1.get(i).printStmt();
			}		
			
			System.out.println("} else {");

			for (int i = 0; i < body2.size(); i++){
				System.out.printf("  ");
				body2.get(i).printStmt();
			}
			System.out.println("}");

			break;
			
		case WHILE:
			System.out.println("while (" + exprs.get(0).GetString(true) + ") {");
			System.out.println();
			
			for (int i = 0; i < body1.size(); i++){
				System.out.printf("  ");
				body1.get(i).printStmt();
			}
			System.out.println("}");
			break;
		
		default:
		}
		
	}
	
	/**
	 * Read an statement from an statement node
	 * 
	 * @param stmtNode The node containing the statement information
	 * @throws Exception 
	 */
	private void ReadStmtFromNode (Node stmtNode) throws Exception {
		
		
		// In case of assignment
		if (stmtNode.getNodeName().equals("node:Expr_Assign")) {
			
			Node varNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(stmtNode, "subNode:var"),
					"node:Expr_Variable");
			
			Expr targetVar = new Expr(varNode);
			
			Node exprNode = DocUtils.GetFirstExprChild(
					DocUtils.GetFirstChildWithName(stmtNode, "subNode:expr"));
			
			Expr targetExpr = new Expr(exprNode);
			
			// Return an statement with type ASSIGN and two Exprs
			List<Expr> exprList = new ArrayList<Expr>();
			exprList.add(targetVar);
			exprList.add(targetExpr);
			
			// Load into the class
			stmtType = StmtType.ASSIGN;
			SetExprs(exprList);
				
		}
		
		// In case of function
		// FIXME
		else if (stmtNode.getNodeName().equals("FUNC")) {
					
			System.out.println("\n[DEBUG]Current statement :" + stmtNode.getNodeName());
			
			stmtType = StmtType.SKIP;
					
		} 
		
		// in case of ITE
		else if (stmtNode.getNodeName().equals("node:Stmt_If")) {
			
			
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList printList = stmtNode.getChildNodes();
				System.out.println("\n[DEBUG]Stmt_If");
				for (int i = 0; i < printList.getLength(); i++) {
					System.out.println("[DEBUG]   " + printList.item(i).getNodeName());
				}			
			}
			
			// Get "if" condition 
			Node ifConditionNode = DocUtils.GetFirstChildWithName(stmtNode, "subNode:cond");
			Node ifConditionExprNode = DocUtils.GetFirstExprChild(ifConditionNode);
			
			Expr ifConditionExpr = new Expr(ifConditionExprNode);
			List<Expr> ifConditionExprList = new ArrayList<Expr>();
			ifConditionExprList.add(ifConditionExpr);
			
			//Get "then" statements
			Node ifThenNode = DocUtils.GetFirstChildWithName(stmtNode, "subNode:stmts");
			Node ifThenStmtsNode = DocUtils.GetFirstChildWithName(ifThenNode, "scalar:array");
			List<Stmt> ifThenStmts = ReadStmtsFromArray(ifThenStmtsNode);
			
			//Get "elseif" list
			Node ifElseIfNode = DocUtils.GetFirstChildWithName(stmtNode, "subNode:elseifs");
			Node ifElseIfStmtsListNode = DocUtils.GetFirstChildWithName(ifElseIfNode, "scalar:array");
			
			if (ifElseIfStmtsListNode.getChildNodes().getLength() > 0) {
			
				System.out.println("[ERROR]Unhandled case: non-empty subNode:elseifs");
				
			}
			
			// Get "else" statements
			// ASSUMPTION: else have to levels subNode:else and Stmt_else
			Node ifElseNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(stmtNode, "subNode:else"), "node:Stmt_Else");
			
			Node ifElseStmtsNode = DocUtils.GetFirstChildWithName(ifElseNode, "subNode:stmts");
			Node ifElseStmtsListNode = DocUtils.GetFirstChildWithName(ifElseStmtsNode, "scalar:array");
			
			List<Stmt> ifElseStmts = ReadStmtsFromArray(ifElseStmtsListNode);
			
			
			// Load into the class
			stmtType = StmtType.ITE;
			SetExprs(ifConditionExprList);
			SetBody1(ifThenStmts);
			SetBody2(ifElseStmts);
			
		}
		
		// in case of while loop
		// FIXME
		else if (stmtNode.getNodeName().equals("node:Stmt_While")) {
			
			System.out.println("\n[DEBUG]Current statement :" + stmtNode.getNodeName());
			
			stmtType = StmtType.SKIP;
			
		}
		
		// Skipping statements
		else if (stmtNode.getNodeName().equals("node:Stmt_Echo")) {
			
			stmtType = StmtType.SKIP;
		}
		
		// Skipping lines
		else if (stmtNode.getNodeName().equals("#text")
				|| stmtNode.getNodeName().equals("attribute:startLine")
				|| stmtNode.getNodeName().equals("attribute:endLine")) {
			
			stmtType = StmtType.SKIP;
		}
	}
	
	
	/**
	 * Given an array node of statements, parse it into a list of statements
	 * @param array An array node of statements 
	 * @return
	 * @throws Exception
	 */
	public static List<Stmt> ReadStmtsFromArray (Node array) throws Exception {
		
		if (!array.getNodeName().equals("scalar:array")) {
			
			throw new Exception("ReadStmtsFromArray expcets " + array.getNodeName());
			
		} else {
			
			List <Stmt> retStmts = new ArrayList<Stmt>();
			
			NodeList childList = array.getChildNodes();
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				
				System.out.println("\n[DEBUG]scalar:array");
				for (int i = 0; i < childList.getLength(); i++) {
					System.out.println("[DEBUG]   " + childList.item(i).getNodeName());
				}
				System.out.println("");
			}

			for (int i = 0; i < childList.getLength(); i++) {
				
				Stmt subStmt = new Stmt(childList.item(i));
				
				if ( subStmt != null) {
					retStmts.add(subStmt);
					
					if (i > 0) {
						
						subStmt.preStmt = retStmts.get(i - 1);
						
					}
				}
			}
			
			return retStmts;
			
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//
	// Flow Analysis
	//
	
	/**
	 * Get the variables defined in this statement, not including the ones defined in sub statements.
	 * @return a list of variables defined in this statement
	 */
	public List<String> GetDefVars(boolean withPosition) {
		
		List<String> retVars = new ArrayList<String>();
		
		// If it's an assignment, add every variable on the left side of the equation
		if (stmtType == StmtType.ASSIGN) {
			
			retVars.addAll(exprs.get(0).GetVarsFromExpr(withPosition));
			
		}
		
		// Else return empty list
		else {
			
			// Add nothing
			
		}
		
		return retVars;
		
	}
	
	/**
	 * Get the variables defined in this statement recursively, including the ones defined in sub statements.
	 * @return a list of variables defined in this statement
	 */
	public List<String> GetDefVarsRecur(boolean withPosition) {
		
		List<String> retVars = new ArrayList<String>();
		
		// If it's an assignment, add every variable on the left side of the equation
		if (stmtType == StmtType.ASSIGN) {
			
			retVars = exprs.get(0).GetVarsFromExpr(withPosition);
			
		}
		
		else if (stmtType == StmtType.ITE) {
			
			for (int i = 0; i < body1.size(); i++) {
				
				retVars.addAll(body1.get(i).GetDefVarsRecur(withPosition));
				
			}
			
			for (int i = 0; i < body2.size(); i++) {
				
				retVars.addAll(body2.get(i).GetDefVarsRecur(withPosition));
				
			}
			
			
		}
		
		else if (stmtType == StmtType.WHILE) {
			
			for (int i = 0; i < body1.size(); i++) {
				
				retVars.addAll(body1.get(i).GetDefVarsRecur(withPosition));
				
			}
			
		}
		
		// Else return empty list
		else {
			
			// Add nothing
			
		}
		
		return retVars;
		
	}
	
	/**
	 * Get the variables used in this statement, without including sub statements
	 * @return a list of variables used in this statement
	 */
	// FIXME: What happens if variables are defined in statements inside ITE or WHILE
	public List<String> GetUseVars(boolean withPosition) {
		
		List<String> retVars = new ArrayList<String>();
		
		// If it's an assignment, add every variable on the right side of the equation
		if (stmtType == StmtType.ASSIGN) {
			
			retVars.addAll(exprs.get(1).GetVarsFromExpr(withPosition));
			
		}
		
		// If it's an function, add every variable used as parameters
		else if (stmtType == StmtType.FUNC) {
			
			for (int i = 0; i < exprs.size(); i++) {
				
				retVars.addAll(exprs.get(i).GetVarsFromExpr(withPosition));
				
			}
				
		}
		
		// If it's an ITE, add every variable used in the branching condition
		else if (stmtType == StmtType.ITE) {
			
			retVars.addAll(exprs.get(0).GetVarsFromExpr(withPosition));
			
		}
		
		// If it's an ITE, add every variable used in the looping condition
		else if (stmtType == StmtType.WHILE) {
			
			retVars.addAll(exprs.get(0).GetVarsFromExpr(withPosition));
			
		}
		
		// Else return empty list
		else {
			
			// Add nothing
			
		}
		
		return retVars;
		
	}
	
	/**
	 * Get the variables used in this statement, including sub statements
	 * @return a list of variables used in this statement
	 */
	// FIXME: What happens if variables are defined in statements inside ITE or WHILE
	public List<String> GetUseVarsRecur(boolean withPosition) {
		
		List<String> retVars = new ArrayList<String>();
		
		// If it's an assignment, add every variable on the right side of the equation
		if (stmtType == StmtType.ASSIGN) {
			
			retVars.addAll(exprs.get(1).GetVarsFromExpr(withPosition));
			
		}
		
		// If it's an function, add every variable used as parameters
		else if (stmtType == StmtType.FUNC) {
			
			for (int i = 0; i < exprs.size(); i++) {
				
				retVars.addAll(exprs.get(i).GetVarsFromExpr(withPosition));
				
			}
				
		}
		
		// If it's an ITE, add every variable used in the branching condition
		else if (stmtType == StmtType.ITE) {
			
			retVars.addAll(exprs.get(0).GetVarsFromExpr(withPosition));
			
			// Add every variable inside "then"
			for (int i = 0; i < body1.size(); i++) {
				
				retVars.addAll(body1.get(i).GetUseVarsRecur(withPosition));
				
			}
			
			// Add everything inside "else"
			for (int i = 0; i < body2.size(); i++) {
				
				retVars.addAll(body2.get(i).GetUseVarsRecur(withPosition));
				
			}
			
		}
		
		// If it's an ITE, add every variable used in the looping condition
		else if (stmtType == StmtType.WHILE) {
			
			retVars.addAll(exprs.get(0).GetVarsFromExpr(withPosition));
			
			// Add every variable inside the loop
			for (int i = 0; i < body1.size(); i++) {
				
				retVars.addAll(body1.get(i).GetUseVarsRecur(withPosition));
				
			}
			
		}
		
		// Else return empty list
		else {
			
			// Add nothing
			
		}
		
		return retVars;
		
	}
	
	
	public List<Stmt> GetAllAssignStmt() {
		
		List<Stmt> assignStmt = new ArrayList<Stmt>();
		
		// If this is a assignment statement;
		if (stmtType == StmtType.ASSIGN) {
			
			assignStmt.add(this);
			
		} 
		
		else if (stmtType == StmtType.ITE) {
			
			for (int i = 0; i < body1.size(); i++) {
				
				assignStmt.addAll(body1.get(i).GetAllAssignStmt());
				
			}
			
			for (int i = 0; i < body2.size(); i++) {
				
				assignStmt.addAll(body2.get(i).GetAllAssignStmt());
				
			}
			
			
		}
		
		else if (stmtType == StmtType.WHILE) {
			
			for (int i = 0; i < body1.size(); i++) {
				
				assignStmt.addAll(body1.get(i).GetAllAssignStmt());
				
			}
			
		}
		
		return assignStmt;
		
	}
	
	/////////////////////////////////////////
	//
	// Program Slicing
	//
	
	/**
	 * Slice the statement by tagging it and propagate backwards
	 * @param targetVars Target variables
	 * @param tag Target slice tag
	 * @throws Exception When targetVars is null
	 */
	
	// [DEBUG]
	// [FIXME] underconstruction
	public void BackwardSlice(List<String> targetVars, int tag) throws Exception {
		
		if (targetVars == null) {
			
			throw new Exception("Can't slice the program without target variables.");
			
		} 
		
		// If it's an assignment
		if (stmtType == StmtType.ASSIGN) {
			
			String assignedVar = exprs.get(0).GetVarsFromExpr(false).get(0);
			// If it assigns a variable in the target variable list
			if (targetVars.contains(assignedVar)) {
				
				// Include this statement
				sliceTags.add(tag);
				
				// Remove this variable
				targetVars.remove(assignedVar);
				
				// Add the variables used in the assignment
				targetVars.addAll(exprs.get(1).GetVarsFromExpr(false));
				
			}
			
			// If it has a immediate previous sibling, slice it
			if (preStmt != null) {
				
				preStmt.BackwardSlice(targetVars, tag);
				
			}
			
			// If it doesn't have a immediate previous sibling, but have a parent, slice it
			
			else if (parentStmt != null) {
				
				parentStmt.BackwardSlice(targetVars, tag);
				
			}
			
			// Else it's the beginning of the file
			
		}
		
		// If it's a function
		// FIXME: some functions may change the content of their parameters
		else if (stmtType == StmtType.FUNC) {
			
		}
		
		// If it's an ITE
		else if (stmtType == StmtType.ITE) {
			
			// Find all the target variables defined in the ITE
			List<String> definedVars = this.GetDefVarsRecur(false);
			List<String> definedTargeVars = new ArrayList<String>();
			
			
			for (int i = 0; i < definedVars.size(); i++) {
				
				if (targetVars.contains(definedVars.get(i))) {
					
					definedTargeVars.add(definedVars.get(i));
					
				}
				
			}
			
			// If target variables are assigned in the ITE, then slice it, or else skip it
			if (definedTargeVars.size() > 0) {
				
				sliceTags.add(tag);
				
			}
			
		}
		
		// If it's an loop
		// FIXME: skipping loop for the moment
		else if (stmtType == StmtType.WHILE) {
			
		}
		
	}
	
	/**
	 * Generate a formula from this statement
	 * @return the formula of this statement
	 */
	public String toFormula() {
		
		String retString = "";
		
		// In case of assignment
		if (stmtType == StmtType.ASSIGN) {
			
			retString = "( = ( " + exprs.get(0).GetString(true) + " ) ( " + exprs.get(1).GetString(true) + " ))\n";
			
		}
		
		// In case of function
		// [FIXME] Skipping function for the moment
		else if (stmtType == StmtType.FUNC) {
			
			
		}
		
		// In case of ITE
		else if (stmtType == StmtType.ITE) {
			
			// Add the head of ITE
			retString = retString.concat("( ite ");
			
			// Add the guard
			retString = retString.concat("( " + exprs.get(0).GetString(true) + " )");
			
			// Add then
			retString = retString.concat("( ");
			for (int i = 0; i < body1.size(); i++) {
				
				retString = retString.concat(body1.get(i).toFormula());
				
			}
			retString = retString.concat(" )");
			
			// Add else
			retString = retString.concat("( ");
			for (int i = 0; i < body2.size(); i++) {
				
				retString = retString.concat(body2.get(i).toFormula());
				
			}
			retString = retString.concat(" )");
			
			// Add the end of ITE
			retString = retString.concat(")\n");
			
		}
		
		// In case of loop
		// [FIXME] Skipping loop for the moment
		else if (stmtType == StmtType.WHILE) {
			
			
		}
		
		return retString;
		
	}
	
	
}


