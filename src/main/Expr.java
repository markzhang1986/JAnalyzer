package main;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.DocUtils;

public class Expr {
	
	private String top;
	private List<Expr> subExprs;

	public ExprType exprType;
	public ExprKind exprKind;
	
	public Expr(Node exprNode) throws Exception {
		
		subExprs = new ArrayList<Expr>();
		ReadExprFromNode(exprNode);
		
	}
	
	public String toString() {
		
		// Case variable
		if (exprKind == ExprKind.VAR) {
			
			if (top == "@") {
				
				return "$" + subExprs.get(0).top + "[" + subExprs.get(1).top + "]";
				
			} else {
				
				return "$" + top;
				
			}
			
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
			
			Node stringValueNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(exprNode, "subNode:value"), "scalar:string");
			
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
			
			Node exprLeftNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:left");
			subExprs.add(new Expr(DocUtils.GetFirstExprChild(exprLeftNode)));
			
			Node exprRightNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:right");
			subExprs.add(new Expr(DocUtils.GetFirstExprChild(exprRightNode)));
			
		}
		
		// Case array_dim
		else if (exprNode.getNodeName().equals("node:Expr_ArrayDimFetch")) {
			
			// A@B get the item B in array A
			SetTop("@");
			SetExprType(ExprType.UNKOWN);
			SetExprKind(ExprKind.VAR);
			
			Node exprVarNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:var");
			subExprs.add(new Expr(DocUtils.GetFirstExprChild(exprVarNode)));
			
			Node exprDimNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:dim");
			subExprs.add(new Expr(DocUtils.GetFirstExprChild(exprDimNode)));	
			
		}
		
		// Unhandled Expressions
		else {
			
			System.out.println("\nUnhandled node: " + exprNode.getNodeName());
			
		}
		
		
	}
	
	public List<String> GetVarsFromExpr() {
		
		List<String> varList = new ArrayList<String>();
		
		
		// Add nothing if the expression is a constant
		if (exprKind == ExprKind.CONS) {
			
		}
		
		// Add the variable if the expression is a variable
		else if (exprKind == ExprKind.VAR) {
			
			// If it's an element from an array
			if (top.equals("@")) {
				
				String varName = subExprs.get(0).top;
				String idxName = subExprs.get(1).top;
				varList.add(varName + "[" + idxName + "]");
				
			} 
			
			// If it's an simple variable
			else {
				
				varList.add(top);
				
			}
		}
		
		// Add the variable if the expression is composite
		else if (exprKind == ExprKind.COMP) {
			
			for (int i = 0; i < subExprs.size(); i++) {
				
				varList.addAll(subExprs.get(i).GetVarsFromExpr());
				
			}
			
		}
		
		return varList;
		
	}
	
	

}
