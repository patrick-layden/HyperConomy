package regalowl.hyperconomy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


public class HyperWebAPI extends AbstractHandler {
	
	/** Classes which must be called by the API */
	private static Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	/**
	 * Constructor
	 * @param hyc
	 * @param c
	 * @param hyws
	 */
	public HyperWebAPI(HyperConomy hyc, Calculation c, HyperWebStart hyws) {
		
		//Add classes for the API
		classes.put("HEcon", HyperEconAPI.class);
		classes.put("HObject", HyperObjectAPI.class);
	}

	/**
	 * A request must be treated
	 */
    public void handle(String pTarget, Request pBaseRequest, HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException, ServletException {
        pResponse.setContentType("text/html;charset=utf-8");
        pResponse.setStatus(HttpServletResponse.SC_OK);
        pBaseRequest.setHandled(true);

    	String lUri = pRequest.getRequestURI();
        pResponse.getWriter().println(getObjects(lUri));
    }
    
    /**
     * Execute an treatement
     * @param pUri Uri source
     * @return The text that must be send to the client
     */
    private String getObjects(String pUri) {
    	//Find all parts of the URI
    	String[] lParts = pUri.split("/");
    	List<String> lPartList = new ArrayList<String>();
    	for (String lObj : lParts) {
    		if (lObj.trim().length() > 0) {
    			lPartList.add(lObj.trim());
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
     */
    private String callMethod(String pClass, String pMethod, List<String> pParams) {
    	String lReturn = "";
    	try {
    		
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
	    		lReturn = lToReturn.toString();
	    	}
    	} catch (Exception e) {
    		//An exception has been thrown
    		StringWriter sw = new StringWriter();
    		PrintWriter psw = new PrintWriter(sw);
			e.printStackTrace(psw);
			lReturn = sw.toString();
		}
    	return lReturn;
    }
}