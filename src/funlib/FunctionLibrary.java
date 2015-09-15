package funlib;

import java.util.ArrayList;
import java.util.List;

import main.PhpExprType;

public class FunctionLibrary {
	private List<FunctionSig> functionLib;
	
	public FunctionLibrary() throws Exception {
		
		functionLib = new ArrayList<FunctionSig>();
		
		StringBuilder strlen_sb = new StringBuilder("(= ret (str.len para1))");
		functionLib.add(new FunctionSig
				("strlen,", true, PhpExprType.INT, 
						new String[] {"para1"}, new PhpExprType[]{PhpExprType.STR}, strlen_sb));
		
		StringBuilder strconcat_sb = new StringBuilder("(= ret (str.++ para1, para2))");
		functionLib.add(new FunctionSig
				("concat,", true, PhpExprType.STR, 
						new String[] {"para1", "para2"}, new PhpExprType[]{PhpExprType.STR, PhpExprType.STR}, strconcat_sb));
		
		
		
	}
	
	public List<FunctionSig> getFunctionLib() {
		
		return functionLib;
		
	}

}
