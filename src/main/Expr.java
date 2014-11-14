package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.DocUtils;

public class Expr {
	
	private String top;
	private List<Expr> subExprs;

	private ExprType exprType;
	private ExprKind exprKind;
	
	public Expr() {
		
		SetTop("");
		subExprs = new ArrayList<Expr>();
		
		SetExprType(ExprType.UNKOWN);
		
		
	}
	
	public Expr(Node exprNode) throws Exception {
		
		subExprs = new ArrayList<Expr>();
		ReadExprFromNode(exprNode);
		
	}
	
	public String toString() {
		
		// Case variable
		if (exprKind == ExprKind.VAR) {
			
			return "$" + top;
			
		}
		
		// Case constant
		else if (exprKind == ExprKind.CONS) {
			
			return top;
			
		} 
		
		// Case composite
		else if (exprKind == ExprKind.COMP) {
			
			String retString = "( ";
			retString += top;
			retString += " ";
			
			for(int i = 0; i < subExprs.size(); i++) {
				
				retString += subExprs.get(i).toString();
				retString += " ";
				
			}
			
			retString += ")";
			return retString;
			
			
		} 
		
		// Case unhandled
		else {
			
			System.out.println("[ERROR]Unknown kind of expression");
			return "";
			
		}
		
	}
	
	
	public String GetTop() {
		
		return top;
		
	}
	
	public List<Expr> GetSubExprs() {
		
		return subExprs;
		
	}
	
	public ExprType GetExprType() {
		
		return exprType;
		
	}
	
	public ExprKind GetExprKind() {
		
		return exprKind;
		
	}
	
	public void SetTop(String newOperator) {
		
		if (newOperator == null) {
			
			top = "";
			
		} else {
			
			top = newOperator;
			
		}
		
	}
	
	public void SetSubExprs(List<Expr> newSubExprs) {
		
		if (newSubExprs == null) {
			
			subExprs = null;
			
		} else {
			
			subExprs = newSubExprs;
		}
	}
	
	
	public void SetExprType(ExprType newExprType) {
		
		if (newExprType == null) {
			
			exprType = ExprType.UNKOWN;
			
		} else {
			
			exprType = newExprType;
		}
		
	}
	
	
	public void SetExprKind(ExprKind newExprKind) {
		
		if (newExprKind == null) {
			
			exprKind = ExprKind.UNKOWN;
			
		} else {
			
			exprKind = newExprKind;
		}
		
	}
	
	
	
	private void ReadExprFromNode(Node exprNode) throws Exception{
		
		
		// Case String
		if (exprNode.getNodeName().equals("node:Scalar_String")) {
			
			SetExprType(ExprType.STR);
			SetExprKind(ExprKind.CONS);
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList exprChildList = exprNode.getChildNodes();
				
				System.out.println("\n[DEBUG]node:Scalar_String");
				for (int i = 0; i < exprChildList.getLength(); i++) {
					System.out.println("[DEBUG]   " + exprChildList.item(i).getNodeName());
				}			
			}
			
			// ASSUMPTION: node:Scalar_String is always "text startline text endline text value text"
			// ASSUMPTION: node:subNode:name is always "text scalar:string text"
			Node stringValueNode = exprNode.getChildNodes().item(5).getChildNodes().item(1);
			
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList exprChildList = stringValueNode.getChildNodes();
				
				System.out.println("\n[DEBUG]subNode:value");
				for (int i = 0; i < exprChildList.getLength(); i++) {
					System.out.println("[DEBUG]   " + exprChildList.item(i).getNodeName());
				}			
			}
			
			String string = DocUtils.GetStringFromNode(stringValueNode);
			SetTop(string);
			
			
		}
		// Case Variable
		else if (exprNode.getNodeName().equals("node:Expr_Variable")) {
			
			SetExprType(ExprType.UNKOWN);
			SetExprKind(ExprKind.VAR);
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList exprChildList = exprNode.getChildNodes();
				
				System.out.println("\n[DEBUG]node:Expr_Variable");
				for (int i = 0; i < exprChildList.getLength(); i++) {
					System.out.println("[DEBUG]   " + exprChildList.item(i).getNodeName());
				}			
			}
			
			// ASSUMPTION: node:Expr_Variable is always "text startline text endline text name text"
			// ASSUMPTION: node:subNode:name is always "text scalar:string text"
			Node nameStringNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(exprNode, "subNode:name"), "scalar:string");
			
			// Get the string under scalar:string
			String varName = DocUtils.GetStringFromNode(nameStringNode);
			SetTop(varName);
			
			
		} 
		
		// Case Binary Equation
		else if (exprNode.getNodeName().equals("node:Expr_BinaryOp_Equal")) {
			
			SetTop("==");
			SetExprType(ExprType.BOOL);
			SetExprKind(ExprKind.COMP);
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList exprChildList = exprNode.getChildNodes();
				
				System.out.println("\n[DEBUG]node:Expr_BinaryOp_Equal");
				for (int i = 0; i < exprChildList.getLength(); i++) {
					System.out.println("[DEBUG]   " + exprChildList.item(i).getNodeName());
				}			
			}
			
			NodeList exprChildList = exprNode.getChildNodes();
			
			for (int i = 0; i < exprChildList.getLength(); i++) {
				
				Node ExprChildNode = exprChildList.item(i);
				
				
				// ASSUMPTION: left is always before right
				if (ExprChildNode.getNodeName().equals("subNode:left")) {
					
					// Debug information
					if (JAnalyzer.DEBUG_MODE >= 10) {
						NodeList printList = ExprChildNode.getChildNodes();
						
						System.out.println("\n[DEBUG]subNode:left");
						for (int j = 0; j < printList.getLength(); j++) {
							System.out.println("[DEBUG]   " + printList.item(j).getNodeName());
						}			
					}
					
					// ASSUMPTION: subNode:left is always "text, content, text"
					Expr leftExpr = new Expr(ExprChildNode.getChildNodes().item(1));
					subExprs.add(leftExpr);
					
	
					
				} else if (ExprChildNode.getNodeName().equals("subNode:right")) {
					
					subExprs.add(new Expr(ExprChildNode.getChildNodes().item(1)));	
					
				}
				
				// Skipping lines
				else if (ExprChildNode.getNodeName().equals("#text") ||
						ExprChildNode.getNodeName().equals("attribute:startLine") ||
						ExprChildNode.getNodeName().equals("attribute:endLine")) {
					
					//System.out.println("\nSkipping: " + ExprChildNode.getNodeName());
					
				}
					
			}
			
		}
		
		// Case array_dim
		else if (exprNode.getNodeName().equals("node:Expr_ArrayDimFetch")) {
			
			// A@B get the item B in array A
			SetTop("@");
			SetExprType(ExprType.UNKOWN);
			SetExprKind(ExprKind.COMP);
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList printList = exprNode.getChildNodes();
				
				System.out.println("\n[DEBUG]node:Expr_ArrayDimFetch");
				for (int j = 0; j < printList.getLength(); j++) {
					System.out.println("[DEBUG]   " + printList.item(j).getNodeName());
				}			
			}
			
			NodeList exprChildList = exprNode.getChildNodes();
			
			for (int i = 0; i < exprChildList.getLength(); i++) {
				
				Node ExprChildNode = exprChildList.item(i);
				
				
				// ASSUMPTION: left is always before right
				if (ExprChildNode.getNodeName().equals("subNode:var")) {
					
					// Debug information
					if (JAnalyzer.DEBUG_MODE >= 10) {
						NodeList printList = ExprChildNode.getChildNodes();
						
						System.out.println("\n[DEBUG]subNode:var");
						for (int j = 0; j < printList.getLength(); j++) {
							System.out.println("[DEBUG]   " + printList.item(j).getNodeName());
						}			
					}
					
					// ASSUMPTION: subNode:var is always "text, content, text"
					Expr varExpr = new Expr(ExprChildNode.getChildNodes().item(1));
					subExprs.add(varExpr);
					
	
					
				} else if (ExprChildNode.getNodeName().equals("subNode:dim")) {
					
					// Debug information
					if (JAnalyzer.DEBUG_MODE >= 10) {
						NodeList printList = ExprChildNode.getChildNodes();
						
						System.out.println("\n[DEBUG]subNode:dim");
						for (int j = 0; j < printList.getLength(); j++) {
							System.out.println("[DEBUG]   " + printList.item(j).getNodeName());
						}			
					}
					
					// ASSUMPTION: subNode:dim is always "text, content, text"
					Expr dimExpr = new Expr(ExprChildNode.getChildNodes().item(1));
					subExprs.add(dimExpr);
					
				}
				
				// Skipping lines
				else if (ExprChildNode.getNodeName().equals("#text") ||
						ExprChildNode.getNodeName().equals("attribute:startLine") ||
						ExprChildNode.getNodeName().equals("attribute:endLine")) {
					
					//System.out.println("\nSkipping: " + ExprChildNode.getNodeName());
					
				}
				
				else {
					
					System.out.println("\nUnhandled node: " + ExprChildNode.getNodeName());
					
				}
				
			}	
			
		}
		
		// Unhandled Expressions
		else {
			
			System.out.println("\nUnhandled node: " + exprNode.getNodeName());
			
		}
		
		
	}
	
	

}
