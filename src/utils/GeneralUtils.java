package utils;

import java.util.Map;

public class GeneralUtils {
	
	public static void CopySIHashMap(Map<String, Integer> src, Map<String, Integer> dst) throws Exception {
		
		if (src == null || dst == null) {
			
			throw new Exception("The maps can't be null");
			
		}
		
		for (String var : src.keySet()) {
			
			dst.put(var, src.get(var));
			
		}
		
		return;
		
	}

}
