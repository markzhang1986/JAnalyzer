package utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocUtils {
	
	/**
	 * Get the string from scalar:string
	 * @param stringNode
	 * @return
	 * @throws Exception 
	 */
	static public String GetStringFromNode(Node stringNode) throws Exception {
		
		if (!stringNode.getNodeName().equals("scalar:string")) {
			
			throw new Exception("[ERROR]DocUtils.GetStringFromNode doesn't take " + stringNode.getNodeName());
			
		} 
		
		else {

			return stringNode.getTextContent();
			
		}
		
	}

	static public Node GetFirstChildWithName(Node targetNode, String targetString) throws Exception {
		
		NodeList ChildNodeList = targetNode.getChildNodes();
		
		for (int i = 0; i < ChildNodeList.getLength(); i++) {
			
			if (ChildNodeList.item(i).getNodeName().equals(targetString)) {
				
				return ChildNodeList.item(i);
				
			}
			
		}
		
		throw new Exception("Target node doesn't contain target string");
		
	}
	
	static public List<Node> GetListofChildrenWithName(Node targetNode, String targetString) {
		
		List<Node> retNodeList = new ArrayList<Node>();
		NodeList ChildNodeList = targetNode.getChildNodes();
		
		for (int i = 0; i < ChildNodeList.getLength(); i++) {
			
			if (ChildNodeList.item(i).getNodeName().equals(targetString)) {
				
				retNodeList.add(ChildNodeList.item(i));
				
			}
			
		}
		
		return retNodeList;
		
	}
	
	static public Node GetFirstExprChild(Node targetNode) throws Exception {
		
		NodeList ChildNodeList = targetNode.getChildNodes();
		
		for (int i = 0; i < ChildNodeList.getLength(); i++) {
			
			Node currentNode = ChildNodeList.item(i);
			
			// Search for expression node
			if (currentNode.getNodeName().equals("node:Scalar_String") ||
					currentNode.getNodeName().equals("node:Expr_Variable") ||
					currentNode.getNodeName().equals("node:Expr_BinaryOp_Equal") ||
					currentNode.getNodeName().equals("node:Expr_ArrayDimFetch")) {
				
				return currentNode;
				
			}
			
		}
		
		throw new Exception("Target node doesn't contain expr");
		
	}

}
