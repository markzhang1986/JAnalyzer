package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.DocUtils;
import utils.GeneralUtils;

public class Stmt {
	
	private List<PhpExpr> exprs;
	private List<Stmt> body1;
	private List<Stmt> body2;
	
	public StmtType stmtType;
	public PhpFile phpFile;
	
	// parent statement and the immediate previous and next sibling
	public Stmt parentStmt;
	public Stmt preStmt;
	public Stmt nextStmt;
	
	public int startLine;
	public int endLine;
	public Map<String, Integer> assignMap;
	
	public List<Integer> sliceTags;
	
	public Stmt(Node stmtNode, PhpFile file, Stmt newParentStmt, Stmt newPreStmt) throws Exception {
		
		exprs = new ArrayList<PhpExpr>();
		body1 = new ArrayList<Stmt>();
		body1 = new ArrayList<Stmt>();
		
		stmtType = StmtType.SKIP;
		phpFile = file;
		
		startLine = -1;
		endLine = -1;
		
		preStmt = newPreStmt;
		parentStmt = newParentStmt;
		
		assignMap = new HashMap<String, Integer>();
		
		sliceTags = new ArrayList<Integer>();
		ReadStmtFromNode(stmtNode);
		
	}
	
	public List<PhpExpr> GetExprs() {
		
		return exprs;
		
	}
	
	public void SetExprs(List<PhpExpr> newExprs) throws Exception {
		
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
				
				if (preStmt != null) {
					
					preStmt.nextStmt = newBody.get(i);
					
				}
				
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
				
				if (preStmt != null) {
					
					preStmt.nextStmt = newBody.get(i);
					
				}
				
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
		
		/*
		// Print out the closest assignments
		// [REMOVE]
		
		System.out.print("For line" + startLine);
		
		if (preStmt == null) {
			
			System.out.println(" preStmt is null");
			
		}
		
		else {
			
			System.out.println(" preStmt is line" + preStmt.startLine);
			
		}
		
		if (assignMap.isEmpty())
			return;
		
		System.out.print("closest assignment:");
			
		for (String defVar: assignMap.keySet()) {
				
			System.out.print(defVar + "*" + assignMap.get(defVar) + ",");
				
		}
		System.out.println("\n");
		*/
		
	}
	
	/**
	 * Read an statement from an statement node
	 * 
	 * @param stmtNode The node containing the statement information
	 * @throws Exception 
	 */
	private void ReadStmtFromNode (Node stmtNode) throws Exception {
		
		// Skipping lines
		if (stmtNode.getNodeName().equals("#text")) {
			
			stmtType = StmtType.SKIP;
			return;
		}
		
		Node slNode = DocUtils.GetFirstChildWithName(
				DocUtils.GetFirstChildWithName(stmtNode, "attribute:startLine"), "scalar:int");
		Node elNode = DocUtils.GetFirstChildWithName(
				DocUtils.GetFirstChildWithName(stmtNode, "attribute:endLine"), "scalar:int");
		
		startLine = DocUtils.GetIntFromNode(slNode);
		endLine = DocUtils.GetIntFromNode(elNode);
		
		Stmt preStmtInterLayer = phpFile.GetPreStmtInterLayer(this);
		
		if (preStmtInterLayer != null) {
			
			GeneralUtils.CopySIHashMap(preStmtInterLayer.assignMap, assignMap);
			
		}
		
		// In case of assignment
		if (stmtNode.getNodeName().equals("node:Expr_Assign")) {
			
			Node varNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(stmtNode, "subNode:var"),
					"node:Expr_Variable");
			
			PhpExpr targetVar = new PhpExpr(varNode, this, true);
			
			if (targetVar.exprKind != PhpExprKind.VAR) {
				
				throw new Exception("The LHS of an assignment must be a variable.");
				
			}
			
			Node exprNode = DocUtils.GetFirstExprChild(
					DocUtils.GetFirstChildWithName(stmtNode, "subNode:expr"));
			
			PhpExpr targetExpr = new PhpExpr(exprNode, this, false);
			
			// Return an statement with type ASSIGN and two Exprs
			List<PhpExpr> exprList = new ArrayList<PhpExpr>();
			exprList.add(targetVar);
			exprList.add(targetExpr);
			
			stmtType = StmtType.ASSIGN;
			SetExprs(exprList);
			assignMap.put(targetVar.GetString(false), startLine);
				
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
			
			PhpExpr ifConditionExpr = new PhpExpr(ifConditionExprNode, this, false);
			List<PhpExpr> ifConditionExprList = new ArrayList<PhpExpr>();
			ifConditionExprList.add(ifConditionExpr);
			
			//Get "then" statements
			Node ifThenNode = DocUtils.GetFirstChildWithName(stmtNode, "subNode:stmts");
			Node ifThenStmtsNode = DocUtils.GetFirstChildWithName(ifThenNode, "scalar:array");
			List<Stmt> ifThenStmts = ReadStmtsFromArray(ifThenStmtsNode, phpFile, this);
			
			//Get "elseif" list
			Node ifElseIfNode = DocUtils.GetFirstChildWithName(stmtNode, "subNode:elseifs");
			Node ifElseIfStmtsListNode = DocUtils.GetFirstChildWithName(ifElseIfNode, "scalar:array");
			
			if (ifElseIfStmtsListNode.getChildNodes().getLength() > 0) {
			
				System.out.println("[ERROR]Unhandled case: non-empty subNode:elseifs");
				
			}
			
			// Get "else" statements
			// ASSUMPTION: else have two levels subNode:else and Stmt_else
			Node ifElseNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(stmtNode, "subNode:else"), "node:Stmt_Else");
			
			Node ifElseStmtsNode = DocUtils.GetFirstChildWithName(ifElseNode, "subNode:stmts");
			Node ifElseStmtsListNode = DocUtils.GetFirstChildWithName(ifElseStmtsNode, "scalar:array");
			
			List<Stmt> ifElseStmts = ReadStmtsFromArray(ifElseStmtsListNode, phpFile, this);
			
			
			// Load into the class
			stmtType = StmtType.ITE;
			SetExprs(ifConditionExprList);
			SetBody1(ifThenStmts);
			SetBody2(ifElseStmts);
			
			// Create new variables to store the new values for each variable re-assigned in the ITE, the new variable will be evaluated in "toFormula"
			Set<String> defVars = new HashSet<String>();
			
			for (int i = 0; i < body1.size(); i++) {
				
				defVars.addAll(body1.get(i).GetDefVarsRecur(false));
				
			}
			
			for (int i = 0; i < body2.size(); i++) {
				
				defVars.addAll(body2.get(i).GetDefVarsRecur(false));
				
			}
			
			if (!defVars.isEmpty()) {
				for (String defVar: defVars) {
					
					assignMap.put(defVar, (-1) * startLine);
					phpFile.shadowVars.add(defVar + "*" + (-1) * startLine);
				
				}
			}
			
			
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

	}
	
	
	/**
	 * Given an array node of statements, parse it into a list of statements
	 * @param array An array node of statements 
	 * @return
	 * @throws Exception
	 */
	public static List<Stmt> ReadStmtsFromArray (Node array, PhpFile file, Stmt parent) throws Exception {
		
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
				
				// Skipping "#text"
				
				if (childList.item(i).getNodeName().equals("#text")) {
					
					continue;
					
				}
				
				Stmt preStmt = null;
				
				if (retStmts.size() >= 1) {
					
					preStmt = retStmts.get(retStmts.size() - 1);
					
				}
				
				Stmt subStmt = new Stmt(childList.item(i), file, parent, preStmt);
				
				if (retStmts.size() >= 1) {
					
					retStmts.get(retStmts.size() - 1).nextStmt = subStmt;
					
				}
				
				if ( subStmt != null) {
					
					retStmts.add(subStmt);
					
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
	
	/**
	 * Generate a formula from this statement
	 * @return the formula of this statement
	 */
	public String toFormula() {
		
		String retString = "";
		
		if (stmtType == StmtType.SKIP) {
			
			retString = " true";
			
		}
		
		// In case of assignment
		else if (stmtType == StmtType.ASSIGN) {
			
			retString = "(= " + exprs.get(0).GetString(true) + "  " + exprs.get(1).GetString(true) + " )\n";
			
			//[REMOVE]
			phpFile.StmtOfInterest = this;
			//[\REMOVE]
			
		}
		
		// In case of function
		// [FIXME] Skipping function for the moment
		else if (stmtType == StmtType.FUNC) {
			
			retString = " true";
			
		}
		
		// In case of ITE
		else if (stmtType == StmtType.ITE) {
			
			// Prepare for the shadow variables
			
			// Find all the variables defined in the branches recursively
			Set<String> thenDefVars = new HashSet<String>();
			
			for(Stmt stmt : body1) {
				
				List<String> defVars = stmt.GetDefVarsRecur(false);
				thenDefVars.addAll(defVars);
				
			}
			
			Set<String> elseDefVars = new HashSet<String>();
			for(Stmt stmt: body2) {
				
				List<String> defVars = stmt.GetDefVarsRecur(false);
				elseDefVars.addAll(defVars);
				
			}
			
			Set<String> defVars = new HashSet<String>();
			defVars.addAll(thenDefVars);
			defVars.addAll(elseDefVars);
			
			// Find the last stmt before this ITE
			Stmt lastPreStmt = phpFile.GetPreStmtInterLayer(this);
			Stmt lastThenStmt = body1.get(body1.size() - 1);
			Stmt lastElseStmt = body2.get(body2.size() - 1);
			
			// Finished preparing for the shadow variables

			
			// Add the head of ITE
			retString = retString.concat("\n\t(ite ");
			
			// Add the guard
			retString = retString.concat(exprs.get(0).GetString(true) + "\n");
			
			// Add then
			retString = retString.concat("\t\t");
			
			if (body1.size() == 1 && defVars.isEmpty()) {
				
				retString = retString.concat(body1.get(0).toFormula());
				
			}
			
			else {
				
				retString = retString.concat(" (and ");
				
				// Add the program itself
				
				for (int i = 0; i < body1.size(); i++) {
					
					retString = retString.concat(body1.get(i).toFormula());
					
				}
				
				for (String defVar : defVars) {
					
					// Give the shadow variable a new name
					String shadowVar = defVar + "*" + (-1) * this.startLine;
					
					// If "then" branch reassigned the variable, make the connection with the state of the last statement in this branch
					if (thenDefVars.contains(defVar)) {
						
						String concreteVar = defVar + "*" + lastThenStmt.assignMap.get(defVar);
						String newLine = " (= " + shadowVar + " " + concreteVar + ") ";
						retString = retString.concat(newLine);
						
					}
					
					// If "then" branch didn't reassign the variable, make the connection with the state of closest previous statement of ITE
					else {
						
						if (lastPreStmt != null) {
							
							if (lastPreStmt.assignMap.containsKey(defVar)) {
								
								String concreteVar = defVar + "*" + lastPreStmt.assignMap.get(defVar);
								String newLine = " (= " + shadowVar + " " + concreteVar + ") ";
								retString = retString.concat(newLine);
							}
						}
						
					}
					
				}
				
				retString = retString.concat(")\n");
				
			}

			retString = retString.concat("\n");
			
			// Add else
			retString = retString.concat("\t\t");
			
			if (body2.size() == 1 && defVars.isEmpty()) {
				
				retString = retString.concat(body2.get(0).toFormula());
				
			}
			
			else {
				
				retString = retString.concat(" (and ");
				
				for (int i = 0; i < body2.size(); i++) {
					
					retString = retString.concat(body2.get(i).toFormula());
					
				}
				
				// Add the evaluation of shadow variables
				
				for (String defVar : defVars) {
					
					// Give the shadow variable a new name
					String shadowVar = defVar + "*" + (-1) * this.startLine;
					
					// If "else" branch reassigned the variable, make the connection with the state of the last statement in this branch
					if (elseDefVars.contains(defVar)) {
						
						String concreteVar = defVar + "*" + lastElseStmt.assignMap.get(defVar);
						String newLine = " (= " + shadowVar + " " + concreteVar + ") ";
						retString = retString.concat(newLine);
						
					}
					
					// If "else" branch didn't reassign the variable, make the connection with the state of closest previous statement of ITE
					else {
						
						if (lastPreStmt != null) {
							
							if (lastPreStmt.assignMap.containsKey(defVar)) {
								
								String concreteVar = defVar + "*" + lastPreStmt.assignMap.get(defVar);
								String newLine = " (= " + shadowVar + " " + concreteVar + ") ";
								retString = retString.concat(newLine);
							}
						}
						
					}
					
				}
				
				retString = retString.concat(")\n");
				
			}

			retString = retString.concat("\n");
			
			// Add the end of ITE
			retString = retString.concat(")\n");
			
			
			
		}
		
		// In case of loop
		else if (stmtType == StmtType.WHILE) {
			
			retString = " true";
			
		}
		
		return retString;
		
	}
	
	/**
	 * Get the path condition it has to satisfy to reach this statement
	 * @return The path condition
	 */
	public String GetPathConditionString() {
		
		List<String> pcList = GetPathConditionStringList();
		if (pcList.isEmpty()) {
			
			return "true";
			
		}
		
		else {
			
			String retString = "(and ";
			for (String pc: pcList) {
				
				retString += pc + " ";
				
			}
			retString += ")";
			return retString;
			
		}
		
		
	}
	
	/**
	 * Get the list of path condition it has to satisfy to reach this statement
	 * @return the path condition list
	 */
	public List<String> GetPathConditionStringList() {
		
		if (this.parentStmt == null) {
			
			return new ArrayList<String>();
			
		}
		
		else if (parentStmt.stmtType != StmtType.ITE) {
			
			return parentStmt.GetPathConditionStringList();
			
		}
		
		// If parent statement is not null and it's an ITE
		else {
			
			List<String> retList = parentStmt.GetPathConditionStringList();
			retList.add(parentStmt.exprs.get(0).GetString(true));
			return retList;
			
		}
		
		
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
	/*
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
	*/
	
	
	
}


