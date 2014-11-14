package main;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;

public class JAnalyzer {
	
	public static int DEBUG_MODE;

	public static void main(String[] args) {
		
		DEBUG_MODE = 9;
		
	    try {
	    	 
	    	// read the xml file
	    	File xmlFile = new File("test_login.xml");
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(xmlFile);
	    	doc.getDocumentElement().normalize();
	    	
	    	Element root = doc.getDocumentElement();
	    	
	    	System.out.println("Root element :" + root.getNodeName());
	    	
	    	NodeList topList = root.getChildNodes();	    	    
	    	 	
	    	// Initialize the PhpFile
	    	// ASSUMPTION: root always has "text content text"
	    	PhpFile phpFile = new PhpFile(topList.item(1));
	    	
	    	phpFile.printPhpFile();
	    	
	        
	    } catch (Exception e) {
	    	
	    	e.printStackTrace();
	        
	    }
	}

}
