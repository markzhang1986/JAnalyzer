package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.DocUtils;

public class Instr {
	
	private InstrType instrType;
	private List<Expr> exprs;
	private List<Instr> body1;
	private List<Instr> body2;
	
	public Instr() {
		
		instrType = InstrType.SKIP;
		exprs = new ArrayList<Expr>();
		body1 = null;
		body2 = null;
		
	}
	
	public Instr(Node instrNode) throws Exception {
		
		exprs = new ArrayList<Expr>();
		body1 = new ArrayList<Instr>();
		body1 = new ArrayList<Instr>();
		
		ReadInstrFromNode(instrNode);
		
	}
	
	/*
	public Instr(int newType, List<Expr> newExprs) throws Exception {
		
		if (newType == 0)
		{
			instrType = InstrType.SKIP;
			exprs = new ArrayList<Expr>();
			body1 = null;
			body2 = null;
			
		} else if (newType == 1 || newType == 2) {
			
			SetInstrType(newType);
			
			exprs = new ArrayList<Expr>();
			SetExprs(newExprs);
			
			body1 = null;
			body2 = null;
			
		} else {
			
			throw new Exception("Instr(int newType, List<Expr> newExprs) only takes SKIP, ASSIGN and FUNC");
			
		}
		
	}
	
public Instr(int newType, List<Expr> newExprs, List<Instr> newBody) throws Exception {
		
		if (newType == 0)
		{
			instrType = InstrType.SKIP;	
			exprs = new ArrayList<Expr>();
			body1 = null;
			body2 = null;
			
		} else if (newType == 1 || newType == 2) {
			
			SetInstrType(newType);
			
			exprs = new ArrayList<Expr>();
			SetExprs(newExprs);
			
			body1 = null;
			body2 = null;
			
		} else if (newType == 4)  {
			
			SetInstrType(newType);
			
			exprs = new ArrayList<Expr>();
			SetExprs(newExprs);
			
			body1 = new ArrayList<Instr>();
			SetBody1(newBody);
			
			body2 = null;
			
		} else {
			
			throw new Exception("Instr(int newType, List<Expr> newExprs, List<Instr> newBody) only takes SKIP, ASSIGN, FUNC and WHILE");
			
		}
		
	}
	
	public Instr(int newType, List<Expr> newExprs, List<Instr> newBody1, List<Instr> newBody2) throws Exception {
		
		if (newType == 0)
		{
			instrType = InstrType.SKIP;	
			exprs = new ArrayList<Expr>();
			body1 = null;
			body2 = null;
			
		} else if (newType == 1 || newType == 2) {
			
			SetInstrType(newType);
			
			exprs = new ArrayList<Expr>();
			SetExprs(newExprs);
			
			body1 = null;
			body2 = null;
			
		}else if (newType == 3)  {
			
			SetInstrType(newType);
			
			exprs = new ArrayList<Expr>();
			SetExprs(newExprs);
			
			body1 = new ArrayList<Instr>();
			SetBody1(newBody1);
			
			body2 = new ArrayList<Instr>();
			SetBody1(newBody2);
			
		} else if (newType == 4)  {
			
			SetInstrType(newType);
			
			exprs = new ArrayList<Expr>();
			SetExprs(newExprs);
			
			body1 = new ArrayList<Instr>();
			SetBody1(newBody1);
			
			body2 = null;
			
		} else {
			
			throw new Exception("Invalid type code: " + newType);
			
		}
		
	}
	*/
	
	public int GetInstrType () {
		
		switch (instrType)
		{
		case SKIP:
			return 0;
			
		case ASSIGN:
			return 1;
			
		case FUNC:
			return 2;
			
		case ITE:
			return 3;
			
		case WHILE:
			return 4;
		
		default:
			return -1;
		}
	}
	
	
	public void SetInstrType (int newType) throws Exception {
		
		switch (newType)
		{
		case 0:
			instrType = InstrType.SKIP;
			break;
			
		case 1:
			instrType = InstrType.ASSIGN;
			break;
			
		case 2:
			instrType = InstrType.FUNC;
			break;
			
		case 3:
			instrType = InstrType.ITE;
			break;
			
		case 4:
			instrType = InstrType.WHILE;
			break;
		
		default:
			throw new Exception("unrecognized return code in SetInstrType");
		}
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
	
	public List<Instr> GetBody1() {
		
		return body1;
		
	}
	
	public void SetBody1(List<Instr> newBody) throws Exception {
		
		if (newBody == null)
			throw new Exception("Can't set body to null.");
		
		body1 = newBody;
		return;
	}
	
	public List<Instr> GetBody2() {
		
		return body2;
		
	}
	
	public void SetBody2(List<Instr> newBody) throws Exception {
		
		if (newBody == null)
			throw new Exception("Can't set body to null.");
		
		body2 = newBody;
		return;
	}
	
	public void printInstr() {
		
		switch (instrType)
		{
		case SKIP:
			//System.out.println("SKIP;");
			break;
			
		case ASSIGN:
			System.out.println(exprs.get(0).toString() + ":= " + exprs.get(1).toString() + ";");
			break;
			
		case FUNC:
			System.out.println("FUNC(" + exprs.get(0).toString() + ");");
			break;
			
		case ITE:
			
			System.out.println("if (" + exprs.get(0).toString() + ") {");
			System.out.println();
			
			for (int i = 0; i < body1.size(); i++){
				System.out.printf("  ");
				body1.get(i).printInstr();
			}		
			
			System.out.println("} else {");

			for (int i = 0; i < body2.size(); i++){
				System.out.printf("  ");
				body2.get(i).printInstr();
			}
			System.out.println("}");

			break;
			
		case WHILE:
			System.out.println("while (" + exprs.get(0).toString() + ") {");
			System.out.println();
			
			for (int i = 0; i < body1.size(); i++){
				System.out.printf("  ");
				body1.get(i).printInstr();
			}
			System.out.println("}");
			break;
		
		default:
		}
		
	}
	
	/**
	 * Read an instruction from an instruction node
	 * 
	 * @param instrNode The node containing the instruction information
	 * @throws Exception 
	 */
	private void ReadInstrFromNode (Node instrNode) throws Exception {
		
		
		// In case of assignment
		if (instrNode.getNodeName().equals("node:Expr_Assign")) {
			
			Node varNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(instrNode, "subNode:var"),
					"node:Expr_Variable");
			
			Expr targetVar = new Expr(varNode);
			
			Node exprNode = DocUtils.GetFirstExprChild(
					DocUtils.GetFirstChildWithName(instrNode, "subNode:expr"));
			
			Expr targetExpr = new Expr(exprNode);
			
			// Return an instruction with type ASSIGN and two Exprs
			List<Expr> exprList = new ArrayList<Expr>();
			exprList.add(targetVar);
			exprList.add(targetExpr);
			
			// Load into the class
			SetInstrType(1);
			SetExprs(exprList);
				
		}
		
		// In case of function
		// FIXME
		else if (instrNode.getNodeName().equals("FUNC")) {
					
			System.out.println("\n[DEBUG]Current instruction :" + instrNode.getNodeName());
			
			SetInstrType(0);
					
		} 
		
		// in case of ITE
		else if (instrNode.getNodeName().equals("node:Stmt_If")) {
			
			
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList printList = instrNode.getChildNodes();
				System.out.println("\n[DEBUG]Stmt_If");
				for (int i = 0; i < printList.getLength(); i++) {
					System.out.println("[DEBUG]   " + printList.item(i).getNodeName());
				}			
			}
			
			// Get "if" condition 
			Node ifConditionNode = DocUtils.GetFirstChildWithName(instrNode, "subNode:cond");
			Node ifConditionExprNode = DocUtils.GetFirstExprChild(ifConditionNode);
			
			Expr ifConditionExpr = new Expr(ifConditionExprNode);
			List<Expr> ifConditionExprList = new ArrayList<Expr>();
			ifConditionExprList.add(ifConditionExpr);
			
			//Get "then" statements
			Node ifThenNode = DocUtils.GetFirstChildWithName(instrNode, "subNode:stmts");
			Node ifThenStmtsNode = DocUtils.GetFirstChildWithName(ifThenNode, "scalar:array");
			List<Instr> ifThenStmts = ReadInstructionFromArray(ifThenStmtsNode);
			
			//Get "elseif" list
			Node ifElseIfNode = DocUtils.GetFirstChildWithName(instrNode, "subNode:elseifs");
			Node ifElseIfStmtsListNode = DocUtils.GetFirstChildWithName(ifElseIfNode, "scalar:array");
			
			if (ifElseIfStmtsListNode.getChildNodes().getLength() > 0) {
			
				System.out.println("[ERROR]Unhandled case: non-empty subNode:elseifs");
				
			}
			
			// Get "else" statements
			// ASSUMPTION: else have to levels subNode:else and Stmt_else
			Node ifElseNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(instrNode, "subNode:else"), "node:Stmt_Else");
			
			Node ifElseStmtsNode = DocUtils.GetFirstChildWithName(ifElseNode, "subNode:stmts");
			Node ifElseStmtsListNode = DocUtils.GetFirstChildWithName(ifElseStmtsNode, "scalar:array");
			
			List<Instr> ifElseStmts = ReadInstructionFromArray(ifElseStmtsListNode);
			
			
			// Load into the class
			SetInstrType(3);
			SetExprs(ifConditionExprList);
			SetBody1(ifThenStmts);
			SetBody2(ifElseStmts);
			
		}
		
		// in case of while loop
		// FIXME
		else if (instrNode.getNodeName().equals("node:Stmt_While")) {
			
			System.out.println("\n[DEBUG]Current instruction :" + instrNode.getNodeName());
			
			SetInstrType(0);
			
		}
		
		// Skipping statements
		else if (instrNode.getNodeName().equals("node:Stmt_Echo")) {
			
			SetInstrType(0);
		}
		
		// Skipping lines
		else if (instrNode.getNodeName().equals("#text")
				|| instrNode.getNodeName().equals("attribute:startLine")
				|| instrNode.getNodeName().equals("attribute:endLine")) {
			
			//System.out.println("\nSkipping: " + instrNode.getNodeName());
			
			SetInstrType(0);
		}
	}
	
	
	/**
	 * Given an array node of instructions, parse it into a list of instructions
	 * @param array An array node of instructions 
	 * @return
	 * @throws Exception
	 */
	public static List<Instr> ReadInstructionFromArray (Node array) throws Exception {
		
		if (!array.getNodeName().equals("scalar:array")) {
			
			throw new Exception("ReadInstructionFromArray expcets " + array.getNodeName());
			
		} else {
			
			List <Instr> retInstrs = new ArrayList<Instr>();
			
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
				
				Instr subInstrcution = new Instr(childList.item(i));
				
				if ( subInstrcution != null) {
					retInstrs.add(subInstrcution);
				}
			}
			
			return retInstrs;
			
		}
	}
	
	
	
}


