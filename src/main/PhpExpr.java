package main;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.DocUtils;

public class PhpExpr {
	
	private String top;
	private List<PhpExpr> subExprs;
	private int position;

	public PhpExprType exprType;
	public PhpExprKind exprKind;
	
	// When assigning a variable(assignedVar), the position of the variable is its parent statement's startLine
	// When using a variable(!assignedVar), the position is the same with its last assignment
	public Stmt parentStmt;
	public boolean assignedVar;
	
	public PhpExpr(Node exprNode, Stmt pt, boolean av) throws Exception {
		
		subExprs = new ArrayList<PhpExpr>();
		SetPosition(-1);
		
		parentStmt = pt;
		assignedVar = av;
		
		ReadExprFromNode(exprNode);
		
	}
	
	// Best not to use it
	/*
	public Expr(Expr expr) throws Exception{
		
		if (expr == null) {
			
			throw new Exception("Can't duplicate a null expression");
			
		}
		
		else {
			
			top = expr.top;
			subExprs = new ArrayList<Expr>();
			for (int i = 0; i < expr.subExprs.size(); i++) {
				
				Expr newSubExpr = new Expr(expr.subExprs.get(i));
				subExprs.add(newSubExpr);
				
			}
			SetPosition(expr.GetPosition());
			
			exprType = expr.exprType;
			exprKind = expr.exprKind;
			
			parentStmt = expr.parentStmt;
			assignedVar = expr.assignedVar;
			
			
		}
	}
	*/
	
	public String GetString(boolean withPosition) {
		
		String retString = "";
		
		// Case variable
		if (exprKind == PhpExprKind.VAR) {
			
			if (top == "@") {
				
				if (withPosition) {
					
					retString = subExprs.get(0).top + "@" + subExprs.get(1).top + "*" + String.valueOf(subExprs.get(0).GetPosition());
					
				}
				
				else {
					
					retString =  subExprs.get(0).top +  "@" + subExprs.get(1).top;
				}
				
			}
			
			else {
				
				if (withPosition) {
					
					retString = top + "*" + String.valueOf(GetPosition());
					
				}
				
				else {
					
					retString = top;
					
				}
					
				
			}
			
		}
		
		// Case constant
		else if (exprKind == PhpExprKind.CONS) {
			
			if (exprType == PhpExprType.STR) {
				retString = "\"" + top + "\"";
			}
			
			else {
				
				retString = top;
				
			}
			
		} 
		
		// Case composite
		else if (exprKind == PhpExprKind.COMP) {
			
			retString = "(";
			retString += top;
			retString += " ";
			
			for(int i = 0; i < subExprs.size(); i++) {
				
				retString += subExprs.get(i).GetString(true);
				retString += " ";
				
			}
			
			retString += ")";
			
			
		}
		
		else if (exprKind == PhpExprKind.FUN) {
			
			retString = top;
			retString += "(";
			
			for (int i = 0; i < subExprs.size(); i++) {
				
				if (i != 0) {
					
					retString += ",";
					
				}
				
				retString += subExprs.get(i).GetString(true);
				
			}
			
			retString += ")";
			
		}
		
		// Case unhandled
		else {
			
			System.out.println("[ERROR]Unknown kind of expression");
			
		}
		
		return retString;
		
	}
	
	
	public String GetTop() {
		
		return top;
		
	}
	
	public List<PhpExpr> GetSubExprs() {
		
		return subExprs;
		
	}
	
	public PhpExprType GetExprType() {
		
		return exprType;
		
	}
	
	public PhpExprKind GetExprKind() {
		
		return exprKind;
		
	}
	
	public void SetTop(String newOperator) {
		
		if (newOperator == null) {
			
			top = "";
			
		} else {
			
			top = newOperator;
			
		}
		
	}
	
	public void SetSubExprs(List<PhpExpr> newSubExprs) {
		
		if (newSubExprs == null) {
			
			subExprs = null;
			
		} else {
			
			subExprs = newSubExprs;
		}
	}
	
	
	public void SetExprType(PhpExprType newExprType) {
		
		if (newExprType == null) {
			
			exprType = PhpExprType.UNKOWN;
			
		} else {
			
			exprType = newExprType;
		}
		
	}
	
	
	public void SetExprKind(PhpExprKind newExprKind) {
		
		if (newExprKind == null) {
			
			exprKind = PhpExprKind.UNKOWN;
			
		} else {
			
			exprKind = newExprKind;
		}
		
	}
	
	public int GetPosition() {
		
		return position;
		
	}
	
	public void SetPosition(int newPosition) {
		
		position = newPosition;
		
	}
	
	
	
	private void ReadExprFromNode(Node exprNode) throws Exception{
		
		// Case String
		if (exprNode.getNodeName().equals("node:Scalar_String")) {
			
			SetExprType(PhpExprType.STR);
			SetExprKind(PhpExprKind.CONS);
			
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
		
		// Case LNumber. No, I don't know what it means either.
		// [FIXME] Only handling integer at the moment
		else if (exprNode.getNodeName().equals("node:Scalar_LNumber")) {
			
			SetExprType(PhpExprType.INT);
			SetExprKind(PhpExprKind.CONS);
			
			Node numValueNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:value");
			Node valueNode = DocUtils.GetFirstChildWithName(numValueNode, "scalar:int");
			
			int num = DocUtils.GetIntFromNode(valueNode);
			SetTop(String.format("%d", num));
			
		}
		
		// Case Variable
		else if (exprNode.getNodeName().equals("node:Expr_Variable")) {
			
			SetExprType(PhpExprType.UNKOWN);
			SetExprKind(PhpExprKind.VAR);
			
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
			
			if (JAnalyzer.DEBUG_MODE >= 10 ) {
				System.out.print("For variable " + varName + ", parentStmt is " + parentStmt.startLine);
			}
			
			if (assignedVar) {
				
				SetPosition(parentStmt.startLine);
				if (JAnalyzer.DEBUG_MODE >= 10 ) {
					System.out.println(", it's assigned, and the position is " + GetPosition());
				}
				
			}
			
			else {
				
				if (parentStmt.assignMap.containsKey(varName)) {
					
					SetPosition(parentStmt.assignMap.get(varName));
					
				}
				
				else {
					
					SetPosition(0);
					
				}
				
				if (JAnalyzer.DEBUG_MODE >= 10 ) {
					System.out.println(", it's used, and the position is " + GetPosition());
				}
				
			}
			
		} 
		
		// Case Binary Equation
		else if (exprNode.getNodeName().equals("node:Expr_BinaryOp_Equal")) {
			
			SetTop("=");
			SetExprType(PhpExprType.BOOL);
			SetExprKind(PhpExprKind.COMP);
			
			Node exprLeftNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:left");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprLeftNode), parentStmt, assignedVar));
			
			Node exprRightNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:right");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprRightNode), parentStmt, assignedVar));
			
		}
		
		// Case Binary Smaller-Than
		else if (exprNode.getNodeName().equals("node:Expr_BinaryOp_Smaller")) {
					
			SetTop("<");
			SetExprType(PhpExprType.BOOL);
			SetExprKind(PhpExprKind.COMP);
					
			Node exprLeftNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:left");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprLeftNode), parentStmt, assignedVar));
					
			Node exprRightNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:right");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprRightNode), parentStmt, assignedVar));
					
		}
		
		// Case array_dim
		else if (exprNode.getNodeName().equals("node:Expr_ArrayDimFetch")) {
			
			// A@B get the item B in array A
			SetTop("@");
			SetExprType(PhpExprType.UNKOWN);
			SetExprKind(PhpExprKind.VAR);
			
			Node exprVarNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:var");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprVarNode), parentStmt, assignedVar));
			
			Node exprDimNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:dim");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprDimNode), parentStmt, assignedVar));	
			
		}
		
		// Case Function-Call
		else if (exprNode.getNodeName().equals("node:Expr_FuncCall")) {
			
			SetExprType(PhpExprType.UNKOWN);
			SetExprKind(PhpExprKind.FUN);
			
			// Get the function name
			// [NOTE] For some reason, the name of the function is an array, I'm just looking at the first of the array for the moment
			Node exprNamesArrayNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(exprNode, "subNode:name"), 
					"node:Name"), "subNode:parts"), "scalar:array");
			
			Node exprNameStrNode =  DocUtils.GetFirstChildWithName(exprNamesArrayNode, "scalar:string");		
			SetTop(DocUtils.GetStringFromNode(exprNameStrNode));
			
			
			// Get the list of arguments
			Node exprArgsArrayNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(exprNode, "subNode:args"), 
					"scalar:array");
			
			List<Node> argsNodeArray = DocUtils.GetListofChildrenWithName(exprArgsArrayNode, "node:Arg");
			for (int i = 0; i < argsNodeArray.size(); i++) {
				
				Node argNode = DocUtils.GetFirstChildWithName(argsNodeArray.get(i),"subNode:value");
				
				//[ASSUMPTION] subNode:value only have one child
				Node argExprNode = DocUtils.GetFirstExprChild(argNode);
				subExprs.add(new PhpExpr(argExprNode, parentStmt, false));
				
			}
			
			System.out.println(this.top);
			
		}
		
		// Unhandled Expressions
		else {
			
			System.out.println("\nUnhandled node: " + exprNode.getNodeName());
			
		}
		
		
	}
	
	public List<String> GetVarsFromExpr(boolean withPosition) {
		
		List<String> varList = new ArrayList<String>();
		
		
		// Add nothing if the expression is a constant
		if (exprKind == PhpExprKind.CONS) {
			
		}
		
		// Add the variable if the expression is a variable
		else if (exprKind == PhpExprKind.VAR) {
			
			varList.add(this.GetString(withPosition));
		}
		
		// Add the variable if the expression is composite
		else if (exprKind == PhpExprKind.COMP) {
			
			for (int i = 0; i < subExprs.size(); i++) {
				
				varList.addAll(subExprs.get(i).GetVarsFromExpr(withPosition));
				
			}
			
		}
		
		return varList;
		
	}
	
	// Get the position from a variable
	public int GetPositionFromVar(String var) {
		
		String[] elements = var.split("\\*");
		int useVarPosition = Integer.parseInt(elements[1]);
		
		return useVarPosition;
		
	}
}
