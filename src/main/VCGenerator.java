package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class VCGenerator {
	
	private PhpFile inputFile;
	private String outputFileName;
	
	public VCGenerator(PhpFile newInputFile, String newOutputFileName) {
		
		inputFile = newInputFile;
		outputFileName = newOutputFileName;
		
	}
	
	public PhpFile getInputFile() {
		
		return inputFile;
		
	}
	
	public void setInputFile(PhpFile newInputFile) {
		
		inputFile = newInputFile;
		
	}
	
	public String getOutputFileName() {
		
		return outputFileName;
		
	}
	
	public void setOutputFileName(String newOutputFileName) {
		
		outputFileName = newOutputFileName;
		
	}
	
	public void checkLoopInvariants() throws Exception {
		
		List<Stmt> loopStmts = inputFile.getAllLoopStmts();
		for (Stmt loopStmt: loopStmts) {
			
			checkLoopInvariantForOneLoop(loopStmt);
			
		}
		
	}
	
	public void checkLoopInvariantForOneLoop(Stmt loopStmt) throws Exception {
		
		if (loopStmt.stmtType != StmtType.LOOP) {
			
			throw new Exception("Can't check loop invariant for non-loop.");
			
		}
		
		List<PhpExpr> invariants = loopStmt.loopInvariants;
		
		for (PhpExpr invariant: invariants) {
			
			if (!checkLoopInvariant(loopStmt, invariant)) {
				
				throw new Exception("Invariant does not hold:\n" + invariant.GetString(true) + "\n");
				
			}
			
		}
		
	}
	
	public boolean checkLoopInvariant(Stmt loopStmt, PhpExpr invariant) {
		
		/*
		if (!checkLoopInvariantForInitialState(loopStmt, invariant)) {
			
			System.out.println("[Error] Loop invariant doesn't hold for initial state: ");
			System.out.println("Line " + loopStmt.startLine + ":" + invariant.GetString(false));
			return false;
			
		}
		*/
		if (!checkLoopInvariantForInduction(loopStmt, invariant)) {
			
			System.out.println("[Error] Loop invariant is not inductive: ");
			System.out.println("Line " + loopStmt.startLine + ":" + invariant.GetString(false));
			return false;
			
		}
		
		// [DEBUG] not finished, have to check with cvc4
		return true;
		
		
	}
	
	public boolean checkLoopInvariantForInitialState(Stmt loopStmt, PhpExpr invariant) {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
			
			// head of the smt file
			writer.write("(set-logic QF_S)\n");
			writer.write("(set-option :strings-exp true)\n");
			writer.write("(set-option :produce-models true)\n");
			
			// generate the part of the program before the loop
			writer.write(inputFile.toSmtLib(-1, loopStmt.startLine - 1));
			writer.write("(assert ");
			writer.write(invariant.negateExpr().GetString(true));
			writer.write(")\n");
			
			// end of the smt file
			writer.write("\n(check-sat)\n");
			writer.write("(get-model)\n");
			
			writer.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// [DEBUG]
		return true;
	}
	
	public boolean checkLoopInvariantForInduction(Stmt loopStmt, PhpExpr invariant) {
		
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
			
			// head of the smt file
			writer.write("(set-logic QF_S)\n");
			writer.write("(set-option :strings-exp true)\n");
			writer.write("(set-option :produce-models true)\n");
			
			// generate the part of the program before the loop
			writer.write(inputFile.toSmtLib(-1, loopStmt.startLine - 1));
			writer.write("(assert ");
			writer.write(invariant.negateExpr().GetString(true));
			writer.write(")\n");
			
			// end of the smt file
			writer.write("\n(check-sat)\n");
			writer.write("(get-model)\n");
			
			writer.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// [DEBUG]
		return true;
	}
	
	public void checkProperty() throws Exception {
		
		// head of the smt file
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
		writer.write("(set-logic QF_S)\n");
		writer.write("(set-option :strings-exp true)\n");
		writer.write("(set-option :produce-models true)\n");
		
		// generate program
		
		writer.write(inputFile.toSmtLib(-1, Integer.MAX_VALUE));
		
		// end of the smt file
		writer.write("(check-sat)\n");
		writer.write("(get-model)\n");
		
		writer.close();
		
	}

}
