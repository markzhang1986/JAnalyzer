package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import funlib.FunctionLibrary;
import funlib.FunctionSig;
import utils.DocUtils;
import utils.GeneralUtils;

public class JAnalyzer {
	
	public static final String phpParseBinDir = "~/PROJECTS/TOOLS/PHP-Parser/bin/";
	public static final String JAnalyzerDir = " ~/PROJECTS/JAnalyzer/";
	
	public static int DEBUG_MODE;
	public static FunctionLibrary funlib;

	public static void main(String[] args) throws Exception {
		
		DEBUG_MODE = 0;
		funlib = new FunctionLibrary();
		
		//Test section
		/*
		for (FunctionSig funsig: funlib.getFunctionLib()) {
			System.out.println(funsig.name + ":");
			System.out.println(funsig.functionContract.GetString(false));
		}
		
		FunctionSig funSig = funlib.getFunctionSig("strlen");
		if (funSig == null) System.out.println("null funsig");
		PhpExpr var = new PhpExpr(PhpExprKind.VAR, PhpExprType.STR, "string");
		List<PhpExpr> paraList = new ArrayList<PhpExpr>();
		paraList.add(var);
		System.out.println(funSig.GenerateConcreteOutput(paraList).GetString(false));
		
		PhpExpr expr = GeneralUtils.parseStringToPhpExpr(("> (+ $x3 7) (- (/ 32 $yx) 75)"), null);
		System.out.println(expr.GetString(false));
		*/
		
		
	    try {
	    	
	    	//FIXME
	    	String targetFileName = "target.php"; 
	    	
	    	String phpParseCmd = "php " + phpParseBinDir + "/php-parse.php --serialize-xml " + JAnalyzerDir + "/" + targetFileName + " > " + JAnalyzerDir + "/tmp_target.xml;";
	    	phpParseCmd += "tail -n +3 " + JAnalyzerDir + "/tmp_target.xml > " + JAnalyzerDir + "/target.xml;";
	    	phpParseCmd += "rm " + JAnalyzerDir + "/tmp_target.xml";
	    	//String phpParseCmd = "touch new";
	    	System.out.println(phpParseCmd);
	    	Process ps = Runtime.getRuntime().exec(new String[] { "/bin/sh" , "-c", phpParseCmd });
	    	ps.waitFor();
	    	
	    	// read the xml file 
	    	File xmlFile = new File("target.xml");
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(xmlFile);
	    	doc.getDocumentElement().normalize();
	    	
	    	Element root = doc.getDocumentElement();
	    	
	    	//System.out.println("Root element :" + root.getNodeName());    
	    	 	
	    	// Initialize the PhpFile
	    	// ASSUMPTION: root always has a direct child "scalar:array" which contains a list of statements
	    	PhpFile phpFile = new PhpFile(DocUtils.GetFirstChildWithName(root, "scalar:array"));
	    	phpFile.printPhpFile();
	    	
	    	// Initialize verification generation component
	    	VCGenerator generator = new VCGenerator(phpFile, "JAnalyzer.smt2");
	    	
	    	// Check for all the invariants in the file
	    	// [DEBUG]
	    	generator.checkLoopInvariants();
	    	
	    	// Check the verification conditions for the property
	    	// [DEBUG]
	    	//generator.checkProperty();
	    	
	    } catch (Exception e) {
	    	
	    	e.printStackTrace();
	        
	    }
	}

}
