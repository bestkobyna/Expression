package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	expr = expr+" ";
    	
    	//divided the string into tokens
    	StringTokenizer st = new StringTokenizer(expr, delims);
        while(st.hasMoreTokens()) {
            String temp = st.nextToken();
            //check digit
            if(Character.isDigit(temp.charAt(0))) {
                continue;
                
             //check array
            }else if(expr.charAt(expr.indexOf(temp)+temp.length())=='['){
            	boolean unique = false;
            	
            	//check if already have the vars in the arraylist
            	for(int i = 0; i<arrays.size(); i++) {
        			if(arrays.get(i).name.equals(temp)){
        				unique = true;
        				break;
        				
        			}
        		}
            	//create array
            	if(!unique) {
	            	Array array = new Array(temp);
	                arrays.add(array);
            	}
            
            //check variable 
            }else {
            	boolean unique = false;
            	
            	//check if already have the vars in the arraylist
            	for(int i = 0; i<vars.size(); i++) {
        			if(vars.get(i).name.equals(temp)){
        				unique = true;
        				break;
        			}
        		}
            	//create var
            	if(!unique) {
	            	Variable var = new Variable(temp);
	                vars.add(var);
            	}
            }
        }
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    
    	
    	//test only one variable here
    	if(expr.length() == 1 && Character.isDigit(expr.charAt(0))){
            return Float.parseFloat(expr);
    	}
    	
    	//clear all () case
    	int left = expr.indexOf("(");
    	
    	while (left!=-1) {
    		int right = 0;
    		int l = 1;
        	int r = 0;
    		for (int i = 0; i < expr.substring(left+1).length(); i++) {
    	        if (expr.substring(left+1).charAt(i) == '(') {
    	        	l = l+1;
    	        }else if(expr.substring(left+1).charAt(i) == ')'){
    	        	r = r+1;
    	        }
    	        if(l==r) {
    	        	right =  i + left +1;
    	        	break;
    	        }
    		}
	    	
	    	//recursive 
	    	float ans = evaluate(expr.substring(left+1,right), vars, arrays);
	    	if(right==expr.length()-1) {
	    		if(left==0) {
	    			expr = Float.toString(ans);
	    		}else {
	    			expr = expr.substring(0,left)+Float.toString(ans);
	    		}
	    	}else {
	    		if(left==0) {
	    			expr = Float.toString(ans)+expr.substring(right+1);
	    		}else {
	    			expr = expr.substring(0,left)+Float.toString(ans)+expr.substring(right+1);
	    		}
	    	}
	    	left = expr.indexOf("(");
    	}
    	//clear all [] case
    	left = expr.indexOf("[");
    	while (left!=-1) {
    		int right = 0;
    		int l = 1;
        	int r = 0;
    		for (int i = 0; i < expr.substring(left+1).length(); i++) {
    	        if (expr.substring(left+1).charAt(i) == '[') {
    	        	l = l+1;
    	        }else if(expr.substring(left+1).charAt(i) == ']'){
    	        	r = r+1;
    	        }
    	        if(l==r) {
    	        	right =  i + left +1;
    	        	break;
    	        }
    		}

    		
	    	//recursive 
	    	float ans = evaluate(expr.substring(left+1,right), vars, arrays);
	    	if(right==expr.length()-1) {
	    		expr = expr.substring(0,left)+"{"+Float.toString(ans)+"]";
	    	}else {
	    		expr = expr.substring(0,left)+"{"+Float.toString(ans)+expr.substring(right+1);
	    	}
	    	left = expr.indexOf("[");
    	}
    	
    	//create stock to load number and operator 
    	Stack<Float> num = new Stack<Float>();
        Stack<Character> ope = new Stack<Character>();
        float number = 0;
        expr = expr.replaceAll(" ", ""); 
        expr = expr+" ";  //for avoiding out of bound exception
        StringTokenizer st = new StringTokenizer(expr, delims+"{");
        String temp = st.nextToken();
        
        //load the first number 
        if(Character.isDigit(temp.charAt(0))) {
        	 num.push(Float.valueOf(temp));
    	}else if(expr.charAt(expr.indexOf(temp)+temp.length())=='{'){
    		for(int i = 0; i<arrays.size(); i++) {
    			if(arrays.get(i).name.equals(temp)){
    				num.push((float)arrays.get(i).values[(int)Float.parseFloat((st.nextToken()))]);
    			}
    		}
    	}else {
    		for(int i = 0; i<vars.size(); i++) {
    			if(vars.get(i).name.equals(temp)){
    				num.push((float)vars.get(i).value);
    			}
    		}
    	} 
        //remover the variable already add to avoiding indexOf error on two same vars
        expr = expr.substring(expr.indexOf(temp)+temp.length());
        
        while(st.hasMoreTokens()) {
        	temp = st.nextToken();
        	//check vars type
        	if(Character.isDigit(temp.charAt(0))) {
        		number = Float.valueOf(temp);
        	}else if(expr.charAt(expr.indexOf(temp)+temp.length())=='{'){
        		for(int i = 0; i<arrays.size(); i++) {
        			if(arrays.get(i).name.equals(temp)){
        				number = arrays.get(i).values[(int)Float.parseFloat((st.nextToken()))];
        			}
        		}
        	}else {
        		for(int i = 0; i<vars.size(); i++) {
        			if(vars.get(i).name.equals(temp)){
        				number = vars.get(i).value;
        			}
        		}
        	} 
        	
        	//iwhen it is + or -, do the previous math with + or - first from the stack, 
        	// if it is * or /, do the current math now
        	
        	if(expr.charAt(expr.indexOf(temp)-1) == '+' ||expr.charAt(expr.indexOf(temp)-1) == '-'  ) {
        		if(ope.isEmpty()){
       				ope.push(expr.charAt(expr.indexOf(temp)-1));
       				num.push(number);
       			}else if(ope.pop() == '+') {
       				num.push(num.pop()+num.pop());
       				num.push(number);
       				ope.push(expr.charAt(expr.indexOf(temp)-1));
       			}else {
       				num.push(-(num.pop()-num.pop()));
       				num.push(number);
       				ope.push(expr.charAt(expr.indexOf(temp)-1));        			
       			}
        	}else if(expr.charAt(expr.indexOf(temp)-1) == '*' ) {
        		num.push(num.pop()*number);
        	}else if(expr.charAt(expr.indexOf(temp)-1) == '/' ) {
        		num.push(num.pop()/number);
       		}
        	 expr = expr.substring(expr.indexOf(temp)+temp.length());
        }	
    	
        if(!ope.isEmpty()) {
        	if(ope.pop() == '+') {
   				num.push(num.pop()+num.pop());  				
   			}else {
   				num.push(-(num.pop()-num.pop()));    
   			}
        }
    	
    	// following line just a placeholder for compilation
    	return num.pop();
    }
}
