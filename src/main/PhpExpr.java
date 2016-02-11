package main;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.nyu.acsys.CVC4.CVC4String;
import edu.nyu.acsys.CVC4.Expr;
import edu.nyu.acsys.CVC4.ExprManager;
import edu.nyu.acsys.CVC4.Rational;
import funlib.FunctionSig;
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
	
	//Constructor with xml node, parent statement and if it's assignedVar
	public PhpExpr(Node exprNode, Stmt pt, boolean av) throws Exception {
		
		subExprs = new ArrayList<PhpExpr>();
		SetPosition(-1);
		
		parentStmt = pt;
		assignedVar = av;
		
		ReadExprFromNode(exprNode);
		
	}
	
	//Constructor to clone another PhpExpr
	public PhpExpr(PhpExpr new_phpExpr) {
		
		top = new_phpExpr.top;
		
		subExprs = new ArrayList<PhpExpr>();
		
		for (int i = 0; i < new_phpExpr.subExprs.size(); i++) {
			
			subExprs.add(new_phpExpr.GetSubExprs().get(i));
			
		}
		
		SetPosition(new_phpExpr.position);

		exprType = new_phpExpr.exprType;
		exprKind = new_phpExpr.exprKind;
		
		// When assigning a variable(assignedVar), the position of the variable is its parent statement's startLine
		// When using a variable(!assignedVar), the position is the same with its last assignment
		parentStmt = new_phpExpr.parentStmt;
		assignedVar = new_phpExpr.assignedVar;
		
	}
	
	//Constructor to create a clean PhpExpr
	public PhpExpr() {
		
		top = "";
		subExprs = new ArrayList<PhpExpr>();
		SetPosition(-1);
		
		exprType = PhpExprType.UNKNOWN;
		exprKind = PhpExprKind.UNKNOWN;
		
		parentStmt = null;
		assignedVar = false;
		
	}
	
	//Constructor only for create a constant or a variable
	public PhpExpr(PhpExprKind new_exprKind, PhpExprType new_exprType, String new_top) {
		
		if(exprKind == PhpExprKind.COMP) {
			
			System.out.println("[ERROR] Constructor for create a constant or a variable");
			
		}
		
		top = new_top;
		subExprs = new ArrayList<PhpExpr>();
		SetPosition(-1);
		
		exprKind = new_exprKind;
		exprType = new_exprType;
		
		parentStmt = null;
		assignedVar = false;
		
	}
	
	//Clone another PhpExpr
	public void clonePhpExpr(PhpExpr new_phpExpr) {
		
		top = new_phpExpr.top;
		subExprs = new ArrayList<PhpExpr>();
		
		for (int i = 0; i < new_phpExpr.subExprs.size(); i++) {
			
			subExprs.add(new_phpExpr.GetSubExprs().get(i));
			
		}
		
		SetPosition(new_phpExpr.position);

		exprType = new_phpExpr.exprType;
		exprKind = new_phpExpr.exprKind;
		
		// When assigning a variable(assignedVar), the position of the variable is its parent statement's startLine
		// When using a variable(!assignedVar), the position is the same with its last assignment
		parentStmt = new_phpExpr.parentStmt;
		assignedVar = new_phpExpr.assignedVar;
		
	}
	
	//[FIXME] under construction
	/*
	public Expr GetExpr(boolean withPosition) {
		
		ExprManager em = new ExprManager();
		
		//case variable
		if (exprKind == PhpExprKind.VAR) {
			
			String varString = "";
			
			if (top == "@") {
				
				if (withPosition) {
					
					varString = subExprs.get(0).top + "@" + subExprs.get(1).top + "*" + String.valueOf(subExprs.get(0).GetPosition());
					
				}
				
				else {
					
					varString =  subExprs.get(0).top +  "@" + subExprs.get(1).top;
				}
				
			}
			
			else {
				
				if (withPosition) {
					
					varString = top + "*" + String.valueOf(GetPosition());
					
				}
				
				else {
					
					varString = top;
					
				}
			}
			
			Expr expr = new Expr();
			
			switch(exprType) {
			case INT:
				expr = em.mkVar(varString, em.integerType());
				break;
			case BOOL:
				expr = em.mkVar(varString, em.booleanType());
				break;
			case STR:
				expr = em.mkVar(varString, em.stringType());
				break;
			case UNKNOWN:
			}
			
			return expr;
			
		}
		
		//case constant
		else if (exprKind == PhpExprKind.CONS) {
			
			Expr consExpr = new Expr();
			
			if (exprType == PhpExprType.STR) {
				
				consExpr = em.mkConst(new CVC4String(top));
				
			}
			
			else if (exprType == PhpExprType.BOOL) {
				
				if (top.compareTo("true") == 0) {
					consExpr = em.mkConst(true);
				}
				else {
					consExpr = em.mkConst(false);
				}
				
			}
			
			else if (exprType == PhpExprType.INT) {
				
				consExpr = em.mkConst(new Rational(Integer.valueOf(top)));
						
			}
			
			return consExpr;
			
		}
		
		//case composite
		else if (exprKind == PhpExprKind.COMP) {
			
			//[FIXME]case composite
			return new Expr();
			
		}
		
		//case function
		else if (exprKind == PhpExprKind.FUN) {
			
			//[FIXME]case composite
			return new Expr();
		}
		
		// Case unhandled
		else {
			
			System.out.println("[ERROR]Unknown kind of expression");
			return new Expr();
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
			
			else if (exprType == PhpExprType.BOOL) {
			
				retString = top;
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
				
				retString += subExprs.get(i).GetString(withPosition);
				retString += " ";
				
			}
			
			retString += ")";
			
			
		}
		
		else if (exprKind == PhpExprKind.FUN) {
			
			boolean hasOutput = false;
			FunctionSig funSig = JAnalyzer.funlib.getFunctionSig(top);
			if (funSig != null) {
				
				if (funSig.functionOutput != null) {
				
					hasOutput = true;
					
				}
			}
			
			if (hasOutput == false) {
				retString = top;
				retString += " ";
				
				for (int i = 0; i < subExprs.size(); i++) {
					
					if (i != 0) {
						
						retString += " ";
						
					}
					
					retString += subExprs.get(i).GetString(withPosition);
					
				}
			}
			
			else {
				
				retString = funSig.GenerateConcreteOutput(subExprs).GetString(true);
				
			}
			
		}
		
		else if (exprKind == PhpExprKind.UNKNOWN) {
			
			System.out.println("[WARNING]Unknown kind of expression:");
			retString = "(";
			retString += top;
			retString += " ";
			
			for(int i = 0; i < subExprs.size(); i++) {
				
				retString += subExprs.get(i).GetString(withPosition);
				retString += " ";
				
			}
			
			retString += ")";
		}
		
		// Case unhandled
		else {
			
			System.out.println("[ERROR]Unexpected kind of expression");
			
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
			
			exprType = PhpExprType.UNKNOWN;
			
		} else {
			
			exprType = newExprType;
		}
		
	}
	
	
	public void SetExprKind(PhpExprKind newExprKind) {
		
		if (newExprKind == null) {
			
			exprKind = PhpExprKind.UNKNOWN;
			
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
				
				System.out.println("\nnode:Scalar_String");
				for (int i = 0; i < exprChildList.getLength(); i++) {
					System.out.println(exprChildList.item(i).getNodeName());
				}			
			}
			
			Node stringValueNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(exprNode, "subNode:value"), "scalar:string");
			
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList exprChildList = stringValueNode.getChildNodes();
				
				System.out.println("\nsubNode:value");
				for (int i = 0; i < exprChildList.getLength(); i++) {
					System.out.println(exprChildList.item(i).getNodeName());
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
			
			// Set the type of the variable to be UNKNOWN for default
			SetExprType(PhpExprType.UNKNOWN);
			SetExprKind(PhpExprKind.VAR);
			
			// Debug information
			if (JAnalyzer.DEBUG_MODE >= 10) {
				NodeList exprChildList = exprNode.getChildNodes();
				
				System.out.println("\nnode:Expr_Variable");
				for (int i = 0; i < exprChildList.getLength(); i++) {
					System.out.println(exprChildList.item(i).getNodeName());
				}			
			}
			
			Node nameStringNode = DocUtils.GetFirstChildWithName(
					DocUtils.GetFirstChildWithName(exprNode, "subNode:name"), "scalar:string");
			
			// Get the string under scalar:string
			String varName = DocUtils.GetStringFromNode(nameStringNode);
			SetTop(varName);
			
			// Get the actual type of the variable
			if(parentStmt.phpFile.varsType.containsKey(varName)) {
				
				SetExprType(parentStmt.phpFile.varsType.get(varName));
				
			}
			
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
		
		// Case array_dim
		else if (exprNode.getNodeName().equals("node:Expr_ArrayDimFetch")) {
			
			// A@B get the item B in array A
			SetTop("@");
			SetExprType(PhpExprType.UNKNOWN);
			SetExprKind(PhpExprKind.VAR);
			
			Node exprVarNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:var");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprVarNode), parentStmt, assignedVar));
			
			Node exprDimNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:dim");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprDimNode), parentStmt, assignedVar));	
			
		}
		
		// Case Function-Call
		else if (exprNode.getNodeName().equals("node:Expr_FuncCall")) {
			
			SetExprType(PhpExprType.UNKNOWN);
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

		// Case Binary SmallerOrEqual
		else if (exprNode.getNodeName().equals("node:Expr_BinaryOp_SmallerOrEqual")) {

			SetTop("<=");
			SetExprType(PhpExprType.BOOL);
			SetExprKind(PhpExprKind.COMP);

			Node exprLeftNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:left");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprLeftNode), parentStmt, assignedVar));

			Node exprRightNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:right");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprRightNode), parentStmt, assignedVar));

		}
		
		// Case Binary Greater-Than
		else if (exprNode.getNodeName().equals("node:Expr_BinaryOp_Greater")) {

			SetTop(">");
			SetExprType(PhpExprType.BOOL);
			SetExprKind(PhpExprKind.COMP);

			Node exprLeftNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:left");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprLeftNode), parentStmt, assignedVar));

			Node exprRightNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:right");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprRightNode), parentStmt, assignedVar));

		}

		// Case Binary GreaterOrEqual
		else if (exprNode.getNodeName().equals("node:Expr_BinaryOp_GreaterOrEqual")) {

			SetTop(">=");
			SetExprType(PhpExprType.BOOL);
			SetExprKind(PhpExprKind.COMP);

			Node exprLeftNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:left");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprLeftNode), parentStmt, assignedVar));

			Node exprRightNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:right");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprRightNode), parentStmt, assignedVar));

		}
		
		// Case Binary Plus
		else if (exprNode.getNodeName().equals("node:Expr_BinaryOp_Plus")) {

			SetTop("+");
			SetExprType(PhpExprType.INT);
			SetExprKind(PhpExprKind.COMP);

			Node exprLeftNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:left");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprLeftNode), parentStmt, assignedVar));

			Node exprRightNode = DocUtils.GetFirstChildWithName(exprNode, "subNode:right");
			subExprs.add(new PhpExpr(DocUtils.GetFirstExprChild(exprRightNode), parentStmt, assignedVar));

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
	
	public void renameVars(String targetName, String newName) {
		
		if(exprKind == PhpExprKind.VAR) {
			
			// rename the variables
			top = (top.compareTo(targetName) == 0) ? newName : top;
			
		}
		
		else if (exprKind == PhpExprKind.CONS) {
			
			return;
			
		}
		
		else if (exprKind == PhpExprKind.COMP) {
			
			for (int i = 0; i <= subExprs.size() - 1; i++) {
				
				subExprs.get(i).renameVars(targetName, newName);
				
			}
		}
		
		else if (exprKind == PhpExprKind.FUN) {
			
			for (int i = 0; i <= subExprs.size() - 1; i++) {
				
				subExprs.get(i).renameVars(targetName, newName);
				
			}
		}
	}
	
	//Instantiate a variable with targetName into a PhpExpr
	public void instantiateVars(String targetName, PhpExpr srcExpr) {
		
		if(exprKind == PhpExprKind.VAR) {
			
			if (top.compareTo(targetName) == 0) {
				
				this.clonePhpExpr(srcExpr);
				
			}
			
		}
		
		else if (exprKind == PhpExprKind.CONS) {
			
			return;
			
		}
		
		else if (exprKind == PhpExprKind.COMP) {
			
			for (int i = 0; i <= subExprs.size() - 1; i++) {
				
				subExprs.get(i).instantiateVars(targetName, srcExpr);
				
			}
		}
		
		else if (exprKind == PhpExprKind.FUN) {
			
			for (int i = 0; i <= subExprs.size() - 1; i++) {
				
				subExprs.get(i).instantiateVars(targetName, srcExpr);
				
			}
		}
	}
	
	public PhpExpr negateExpr() {
		PhpExpr cloneExpr = new PhpExpr(this);
		PhpExpr retExpr = new PhpExpr();
		
		retExpr.SetTop("not");
		List<PhpExpr> newSubExprs = new ArrayList<PhpExpr>();
		newSubExprs.add(cloneExpr);
		retExpr.SetSubExprs(newSubExprs);
		retExpr.SetPosition(this.position);
		
		retExpr.SetExprType(PhpExprType.BOOL);
		retExpr.SetExprKind(PhpExprKind.COMP);
		
		retExpr.parentStmt = this.parentStmt;
		retExpr.assignedVar = this.assignedVar;
		
		return retExpr;
	}
}
