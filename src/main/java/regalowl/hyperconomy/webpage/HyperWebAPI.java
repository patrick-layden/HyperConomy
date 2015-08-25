package regalowl.hyperconomy.webpage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import regalowl.hyperconomy.api.API;



public class HyperWebAPI extends HttpServlet {
	

	private static final long serialVersionUID = 1467231980254743516L;
	private static Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	private String apiKey;

	public HyperWebAPI(String apiKey) {
		this.apiKey = apiKey;
		classes.put("Main", API.class);
	}


    public void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException, ServletException {
        pResponse.setContentType("text/html;charset=utf-8");
        pResponse.setStatus(HttpServletResponse.SC_OK);
    	String lUri = pRequest.getRequestURI();
    	String lReturn  = "";
    	try {
    		lReturn = getObjects(lUri);
    	} catch (Throwable e) {
    		while (e != null) {
	    		Writer lWriter = new StringWriter();
	    		PrintWriter lPrintWriter = new PrintWriter(lWriter);
	    		e.printStackTrace(lPrintWriter);
	    		lReturn += lWriter.toString();
	    		e = e.getCause();
    		}
    	}
        pResponse.getWriter().println(lReturn);
    }
    
    /**
     * Execute an treatement
     * @param pUri Uri source
     * @return The text that must be send to the client
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @throws IllegalArgumentException IllegalArgumentException
     */
    private String getObjects(String pUri) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	//Find all parts of the URI
    	String[] lParts = pUri.split("/");
    	List<String> lPartList = new ArrayList<String>();
    	boolean lPassAPI = false;
    	for (String lObj : lParts) {
    		if (lObj.trim().length() > 0) {
    			if (!lPassAPI) {
    				if(lObj.trim().equals(apiKey)) {
    					lPassAPI = true;
    				}
    			} else {
    				lPartList.add(lObj.trim());
    			}
    		}
    	}
    	
    	//Use cases
    	if (lPartList.size() == 0) {
    		//No object
    		return listClasses();
    	} else if (lPartList.size() == 1) {
    		//Only the object
    		return listMethods(lPartList.get(0));
    	} else {
    		//Object + method
    		String lObjectName = lPartList.get(0);
    		String lMethodName = lPartList.get(1);
    		lPartList.remove(0);
    		lPartList.remove(0);
    		return callMethod(lObjectName, lMethodName, lPartList);
    	}
    }
    
    /**
     * List all classes possible
     * @return List of classes possibles
     */
    private String listClasses() {
    	StringBuffer lRetour = new StringBuffer("Objects :<br />\r\n");
    	for (String key : classes.keySet()) {
    		lRetour.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + key + "<br />\r\n");
    	}
    	return lRetour.toString();
    }
    
    /**
     * List of methods possible, attached to the object pClass
     * @param pClass Class referenced
     * @return
     */
    private String listMethods(String pClass) {
    	//Get class
    	Class<?> lClass = classes.get(pClass);
    	StringBuffer lReturn = new StringBuffer("Methods of " + pClass + " : <br />\r\n");
    	
    	//For each methods of this class
    	for (Method lMethod : lClass.getMethods()) {
			String lDescription = "";
			String lName = lMethod.getName();
			lDescription += lName + "(";
			boolean addComma = false;
			
			//For each parameter of this method
			for (Class<?> lParameter : lMethod.getParameterTypes()) {
				if (addComma) {
					lDescription += ",";
				} else {
					addComma = true;
				}
				lDescription += lParameter.getCanonicalName();
			}
			lDescription += ")";
			lReturn.append(lDescription + "<br />\r\n");
		}
    	return lReturn.toString();
    }
    
    /**
     * Call a method (pMethod) in an instance of pClass, with parameters (pParams)
     * @param pClass Class to use
     * @param pMethod Method to call
     * @param pParams Parameters of the method
     * @return the result of the method (or an exception)
     * @throws IllegalAccessException IllegalAccessException
     * @throws InstantiationException InstantiationException
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalArgumentException IllegalArgumentException
     */
	@SuppressWarnings("rawtypes")
	private String callMethod(String pClass, String pMethod, List<String> pParams) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	String lReturn = "";
		
		//Get the class
    	Class<?> lClass = classes.get(pClass);
    	Method lMethod = null;
    	
    	//Search the method
    	for (Method lMethodTemp : lClass.getMethods()) {
    		if (lMethodTemp.getName().equalsIgnoreCase(pMethod)) {
    			lMethod = lMethodTemp;
    		}
    	}
    	
    	//Create the instance
    	Object lObject = lClass.newInstance();
    	
    	//Table to call the method
    	Object[] lArgs = new Object[lMethod.getParameterTypes().length];
    	int lIndex = 0;
    	
    	//For each parameter
    	for (Class<?> lType : lMethod.getParameterTypes()) {
    		
    		//It's a String
    		if (lType.isAssignableFrom(String.class)) {
    			lArgs[lIndex] = pParams.get(lIndex);
    		
    		} //It's a long
    		else if (lType.isAssignableFrom(long.class)) {
    			lArgs[lIndex] = Long.parseLong(pParams.get(lIndex));
    		} //It's an integer
    		else if (lType.isAssignableFrom(int.class)) {
    			lArgs[lIndex] = Integer.parseInt(pParams.get(lIndex));
    		} //It's a double
    		else if (lType.isAssignableFrom(double.class)) {
    			lArgs[lIndex] = Double.parseDouble(pParams.get(lIndex));
    		} //It's a byte
    		else if (lType.isAssignableFrom(byte.class)) {
    			lArgs[lIndex] = Byte.parseByte(pParams.get(lIndex));
    		} //It's a short
    		else if (lType.isAssignableFrom(short.class)) {
    			lArgs[lIndex] = Short.parseShort(pParams.get(lIndex));
    		} //It's a float
    		else if (lType.isAssignableFrom(float.class)) {
    			lArgs[lIndex] = Float.parseFloat(pParams.get(lIndex));
    		} //It's a boolean
    		else if (lType.isAssignableFrom(boolean.class)) {
    			lArgs[lIndex] = Boolean.parseBoolean(pParams.get(lIndex));
    		} //It's a char
    		else if (lType.isAssignableFrom(char.class)) {
    			lArgs[lIndex] = new Character(pParams.get(lIndex).charAt(0));
    		} //It's a player -> find by name
    		else if (lType.isAssignableFrom(Player.class)) {
    			lArgs[lIndex] = Bukkit.getPlayer(pParams.get(lIndex));
    		} //The type isn't autorized (you must add a new type at this list
    		else {
    			throw new IllegalArgumentException(pClass + "." + pMethod + "() : arg(" + lIndex + ") isn't autorized! (" + lType.getCanonicalName() + ")");
    		}
    		
    		//Next object
    		lIndex++;
    	}
    	
    	//Call the method
    	Object lToReturn = lMethod.invoke(lObject, lArgs);
    	
    	//Change the value to string
    	if (lToReturn != null) {
	    	if (lToReturn instanceof List) {
	    		lReturn = listToJSON((List)lToReturn).toJSONString();
	    	} else if (lToReturn instanceof Map) {
	    		lReturn = mapToJSON((Map)lToReturn).toJSONString();
	    	} else {
	    		lReturn = lToReturn.toString();
	    	}
    	}
    	return lReturn;
    }
    
    /**
     * Transform a map to JSONObject
     * @param pMap Map to transform
     * @return the JSONObject
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONObject mapToJSON(Map<?, ?> pMap) {
    	JSONObject lJsonObject = new JSONObject();
    	for (Object lKey : pMap.keySet()) {
    		Object lValue = pMap.get(lKey);
    		if (lValue instanceof Map) {
    			lValue = mapToJSON((Map)lValue);
    		} else if (lValue instanceof List) {
    			lValue = listToJSON((List)lValue);
    		}
    		lJsonObject.put(lKey.toString(), lValue);
    	}
    	return lJsonObject;
    }
    
    /**
     * Transform a list to JSONArray
     * @param pList list to transform
     * @return the JSONArray
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray listToJSON(List<?> pList) {
    	JSONArray lJsonArray = new JSONArray();
    	for (Object lValue : pList) {
    		if (lValue instanceof Map) {
    			lValue = mapToJSON((Map)lValue);
    		} else if (lValue instanceof List) {
    			lValue = listToJSON((List)lValue);
    		}
    		lJsonArray.add(lValue);
    	}
    	return lJsonArray;
    }
}