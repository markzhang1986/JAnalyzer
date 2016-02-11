package funlib;

import java.util.ArrayList;
import java.util.List;

import main.PhpExpr;
import main.PhpExprKind;
import main.PhpExprType;

public class FunctionLibrary {
	private List<FunctionSig> functionLib;
	
	public FunctionLibrary() {
		
		functionLib = new ArrayList<FunctionSig>();
		
		// sub-expression list instantiate on each use
		List<PhpExpr> globalSubExprList;
		
		PhpExpr strlenRetVar = new PhpExpr(PhpExprKind.VAR, PhpExprType.INT, "ret");
		PhpExpr strlenPara1Var = new PhpExpr(PhpExprKind.VAR, PhpExprType.STR, "para0");
		
		// (str.len para1)
		PhpExpr strlenSubExpr1 = new PhpExpr();
		strlenSubExpr1.SetExprKind(PhpExprKind.FUN);
		strlenSubExpr1.SetExprType(PhpExprType.INT);
		
		strlenSubExpr1.SetTop("str.len");
		
		globalSubExprList = new ArrayList<PhpExpr>();
		globalSubExprList.add(strlenPara1Var);
		strlenSubExpr1.SetSubExprs(globalSubExprList);
		
		//= ret (str.len para1)
		PhpExpr strlenExpr = new PhpExpr();
		strlenExpr.SetExprKind(PhpExprKind.COMP);
		strlenExpr.SetExprType(PhpExprType.BOOL);
		
		strlenExpr.SetTop("=");
		
		globalSubExprList = new ArrayList<PhpExpr>();
		globalSubExprList.add(strlenRetVar);
		globalSubExprList.add(strlenSubExpr1);
		strlenExpr.SetSubExprs(globalSubExprList);
		
		functionLib.add(
				new FunctionSig
				("strlen", 
					true, 
					PhpExprType.INT,
					new PhpExprType[]{PhpExprType.STR},
					strlenExpr,
					strlenSubExpr1));
		
	}
	
	public List<FunctionSig> getFunctionLib() {
		
		return functionLib;
		
	}
	
	public FunctionSig getFunctionSig(String funName) {
		
		for (int i = 0; i <= functionLib.size() - 1; i++) {
			
			if (functionLib.get(i).name.compareTo(funName) == 0)
				return functionLib.get(i);
			
		}
		
		return null;
		
	}

}
