package main;

import java.util.List;

public class UDChain {
	
	private PhpFile file;
	private Stmt targetStmt;
	
	public UDChain(PhpFile newFile, Stmt newTarget) throws Exception {
		
		SetPhpFile(newFile);
		SetTarget(newTarget);
		BuildUDChain();
		
	}
	
	public void SetPhpFile(PhpFile newFile) throws Exception {
		
		if (newFile == null) {
			
			throw new Exception("Can't set file of UDChain to null");
		}
		
		else {
			
			file = newFile;
			
		}
		
	}
	
	public PhpFile GetPhpFile() {
		
		return file;
		
	}
	
	public void SetTargetStmt(Stmt newTarget) throws Exception {
		
		if (newTarget == null) {
			
			throw new Exception("Can't set file of UDChain to null");
		}
		
		else {
			
			targetStmt = newTarget;
			
		}
		
	}
	
	public Stmt GetTargetStmt() {
		
		return targetStmt;
		
	}
	
	private void BuildUDChain() {
		
		List<String> varList = GetTargetStmt().GetUseVars();
		
		
	}

}
