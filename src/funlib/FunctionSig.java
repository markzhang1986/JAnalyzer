package funlib;

import java.util.List;

import main.PhpExpr;
import main.PhpExprType;

public class FunctionSig {
	
	public String name;
	public boolean isStatic;
	public PhpExprType returnType;
	public PhpExprType[] paraTypes;
	
	// The template of the function contract
	public PhpExpr functionContract;
	public PhpExpr functionOutput;
	
	public FunctionSig(String new_name, boolean new_isStatic, PhpExprType new_returnType,
			PhpExprType[] new_paraTypes, PhpExpr new_functionContract, PhpExpr new_functionOutput) {
		
		if (new_name == null || new_returnType == null || new_paraTypes == null || new_functionContract == null) {
			
			System.out.println("[ERROR] FunctionSig constructor doesn't take null");
			
		}
		
		name = new String(new_name);
		isStatic = new_isStatic;
		paraTypes = new_paraTypes.clone();
		
		functionContract = new PhpExpr(new_functionContract);
		
		if (new_functionOutput == null) functionOutput = null;
		else functionOutput = new PhpExpr(new_functionOutput);
		
	}
	
	public PhpExpr GenerateConcreteContract(String retVariable, List<PhpExpr> paras) {
		
		PhpExpr concreteContract = new PhpExpr(functionContract);
		
		concreteContract.renameVars("ret", retVariable);
		for(int i = 0; i < paras.size(); i++) {
			
			concreteContract.instantiateVars("para" + i, paras.get(i));
			
		}
		
		return concreteContract;

	}
	
	public PhpExpr GenerateConcreteOutput(List<PhpExpr> paras) {
		
		PhpExpr concreteOutput = new PhpExpr(functionOutput);
		
		for(int i = 0; i < paras.size(); i++) {
			
			concreteOutput.instantiateVars("para" + i, paras.get(i));
			
		}
		
		return concreteOutput;
		
	}
}
