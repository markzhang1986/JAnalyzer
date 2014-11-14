package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class PhpFile {
	private List<Instr> instrs;
	
	public PhpFile () {
		
		instrs = new ArrayList<Instr>();
		
	}
	
	public PhpFile (List<Instr> new_instrs) {
		
		instrs = new_instrs;
		
	}
	
	public PhpFile (Node root) throws Exception {
		
		instrs = new ArrayList<Instr>();
		ReadFileFromRoot(root);
		
	}
	
	public void AddInstr (Instr newInstr) throws Exception {
		
		if (newInstr == null)
		{
			throw new Exception("Can't add null as instruction.");
		}
		
		instrs.add(newInstr);
		
	}
	
	/**
	 * Top level function when you read in a php file and parse it into a list of instructions and store them in this PhpFile object.
	 * 
	 * @param root The root of the dom elements containing all the instruction information
	 * @throws Exception 
	 */
	private void ReadFileFromRoot (Node root) throws Exception {
		
		instrs = Instr.ReadInstructionFromArray(root);
		
	}
	
	public void printPhpFile() {
		
		
		//DEBUGGING
		System.out.println("size of phpfile: " + instrs.size());
		
		for(int i = 0; i < instrs.size(); i++) {
			
			instrs.get(i).printInstr();
			
		}
	}
	

}
