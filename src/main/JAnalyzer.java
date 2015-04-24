package main;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utils.DocUtils;

import java.io.File;

public class JAnalyzer {
	
	public static int DEBUG_MODE;

	public static void main(String[] args) {
		
		DEBUG_MODE = 9;
		
	    try {
	    	 
	    	// read the xml file
	    	File xmlFile = new File("login_1.xml");
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
	    	
	    	VCGenerator generator = new VCGenerator(phpFile, "JAnalyzer.smt2");
	    	generator.generate();
	    	
	    	
	        
	    } catch (Exception e) {
	    	
	    	e.printStackTrace();
	        
	    }
	}

}
