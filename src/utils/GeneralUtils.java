package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.PhpExpr;
import main.PhpExprKind;
import main.PhpExprType;
import main.Stmt;

public class GeneralUtils {
	
	public static void CopyHashMap(Map<String, Integer> src, Map<String, Integer> dst) throws Exception {
		
		if (src == null || dst == null) {
			
			throw new Exception("The maps can't be null");
			
		}
		
		for (String var : src.keySet()) {
			
			dst.put(var, src.get(var));
			
		}
		
		return;
		
	}
	
	public static List<String> parsePolishNotation(String string) {
		
		List<String> retList = new ArrayList<String>();
		
		// get rid of the spaces on the head and tail
		while(string.charAt(0) == ' ') string = string.substring(1);
		while(string.charAt(string.length() - 1) == ' ') string = string.substring(0, string.length() - 1);
		
		string = removeParenthesis(string);
		
		while(string.charAt(0) == ' ') string = string.substring(1);
		while(string.charAt(string.length() - 1) == ' ') string = string.substring(0, string.length() - 1);
		
		// get rid of the multiple spaces
		string = string.replace("  ", " ");
		
		// stack to keep in track of the parenthesis
		int pStack = 0;
		
		// keep in track of the start of the last segment
		int headOfCurrentSegment = 0;
		
		for (int i = 0; i < string.length(); i++ ) {
			
			if (string.charAt(i) == ' ' && pStack == 0) {
				
				retList.add(string.substring(headOfCurrentSegment, i));
				headOfCurrentSegment = i + 1;
				continue;
				
			}
			
			else if (string.charAt(i) == '(') pStack++;
			else if (string.charAt(i) == ')') pStack--;
			
		}
		
		retList.add(string.substring(headOfCurrentSegment, string.length()));
			
		return retList;
	}

	public static String removeParenthesis(String string) {
		
		if (string.charAt(0) == '(' && string.charAt(string.length() - 1) == ')' ) {
			
			string = string.substring(1, string.length() - 1);
			return removeParenthesis(string);
			
		} else {
		
			return string;
			
		}
		
	}
	
	public static boolean isVariable (String string) {
		
		return string.charAt(0) >= 'A' && string.charAt(0) <= 'z';
		
	}
	
	public static boolean isBool (String string) {
		
		return string == "true" || string == "false";
		
	}
	
	public static boolean isInt(String string) {
		
		if (string.charAt(0) == '-') string = string.substring(1);
		
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) < '0' || string.charAt(i) > '9')
				return false;
		}
		
		return true;
	}
	
	public static boolean isString (String string) {
		
		return (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"');
		
	}
	
	public static boolean isQuantifier (String string) {
		
		return (string.contains("forall") || string.contains("exists")) && string.contains(":");
		
	}
	
	public static PhpExpr parseStringToPhpExpr(String string, Stmt parentStmt) {
		
		PhpExpr retExpr = new PhpExpr();
		retExpr.parentStmt = parentStmt;
		
		if(parentStmt != null) {
			
			retExpr.SetPosition(parentStmt.startLine);
			
		}
		
		else {
			
			retExpr.SetPosition(-1);
			
		}
		
		String top = "";
		List<PhpExpr> subExprs = new ArrayList<PhpExpr>();
		PhpExprType exprType = PhpExprType.UNKNOWN;
		PhpExprKind exprKind = PhpExprKind.UNKNOWN;
		
		try {
			List<String> elements =parsePolishNotation(string);
			
			// case of atomic
			if(elements.size() == 1) {
				
				top = removeParenthesis(elements.get(0));
				
				if (isVariable(top)) {
					
					exprType = PhpExprType.UNKNOWN;
					exprKind = PhpExprKind.VAR;
					
					if (parentStmt.assignMap.containsKey(top)) {
						
						int pos = parentStmt.assignMap.get(top);
						retExpr.SetPosition(pos);
						
					}
					
					exprType = parentStmt.phpFile.getVarType(top);
					
				}
				
				else if (isBool(top)) {
					
					exprType = PhpExprType.BOOL;
					exprKind = PhpExprKind.CONS;
					
				}
				
				else if (isInt(top)) {
					exprType = PhpExprType.INT;
					exprKind = PhpExprKind.CONS;
				}
				
				else if (isString(top)) {
					exprType = PhpExprType.STR;
					exprKind = PhpExprKind.CONS;
				}
				
				else {
					
					throw new Exception("Can't parse \"" + string + "\" to atomic expression" );
					
				}
				
			}
			
			// Unary operations
			else if (elements.size() == 2) {
				
				top = elements.get(0);
				top = removeParenthesis(top);
				exprKind = PhpExprKind.COMP;
				
				if (top == "not") {
					
					exprType = PhpExprType.BOOL;
					
				}
				
				else if (isQuantifier(top)) {
					
					exprType = PhpExprType.UNKNOWN;
					
				}
					
				else {

					throw new Exception("Can't parse \"" + string + "\" to a unary operation expression");

				}
				
				subExprs.add(parseStringToPhpExpr(elements.get(1), parentStmt));
				
			} 
			
			// Binary operations
			else if (elements.size() == 3) {
				
				top = elements.get(0);
				top = removeParenthesis(top);
				exprKind = PhpExprKind.COMP;
				
				if (top.compareTo("&&") == 0  ||
						top.compareTo("||") == 0 ||
						top.compareTo("==") == 0 ||
						top.compareTo(">=") == 0 ||
						top.compareTo("<=") == 0 ||
						top.compareTo(">") == 0 ||
						top.compareTo("<") == 0) {
					
					exprType = PhpExprType.BOOL;
				}
				
				else if (top.compareTo("+") == 0 ||
						top.compareTo("-") == 0 ||
						top.compareTo("*") == 0 ||
						top.compareTo("/") == 0) {
					
					exprType = PhpExprType.INT;
					
				}
				
				else {
					
					throw new Exception("size 3 Can't parse \"" + string + "\" to binary operation expression" );
					
				}
				
				subExprs.add(parseStringToPhpExpr(elements.get(1), parentStmt));
				subExprs.add(parseStringToPhpExpr(elements.get(2), parentStmt));
				
			}
			
			// Under construction
			// Reserved for special operators such as addition of several numbers
			// Case of operations taking more than 2 parameters.
			// FIXME
			else {
				
				throw new Exception("Expression \"" + string + "\" has more than two parameters, not handled at the moment" );
				
			}
			
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		retExpr.SetTop(top);
		retExpr.SetSubExprs(subExprs);
		
		retExpr.SetExprType(exprType);
		retExpr.SetExprKind(exprKind);
		
		retExpr.parentStmt = parentStmt;
		retExpr.assignedVar = false;
		
		return retExpr;
	}
	
	// Get the position from a variable
	public static int GetPositionFromVar(String var) {
		
		String[] elements = var.split("\\*");
		int useVarPosition = Integer.parseInt(elements[1]);
		
		return useVarPosition;
		
	}
	
	// Remove the position, return only variable name
	public static String getNameFromVar(String var) {
		
		String[] elements = var.split("\\*");
		return elements[0];
		
	}

}
