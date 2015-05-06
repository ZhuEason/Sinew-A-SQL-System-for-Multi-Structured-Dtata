package userVersion2;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import userLayer.JasonParser;

public class ParseByLineitem {
	private String str;
    protected Queue<String> key;
    protected Queue<Object> value;
    private int count;
    private int index;
    
	public ParseByLineitem(String str) {
		this.str = str;
		key = new LinkedList<String>();
		value = new LinkedList<Object>();
		index = 0;
		
		while (index <= str.length()-1) {
			scanner();
			count++; 
		}
	}
	
	public void scanner() {
		
		boolean judge = false;
		Integer intValue = 0;
		Double doubleValue = 0.0;
		String strKeyValue = "";
		
		char ch;
		while ((ch = str.charAt(index)) == ' ' || ch == ',' || ch == '{' ) {
			System.out.println("before:" + ch);
			index++;
		}
		
		strKeyValue = "";
		if (ch == '\"') {
		    ch = str.charAt(++index);
		    while (ch != '"') {
		    	strKeyValue += ch;
		    	ch = str.charAt(++index);
		    }
		    System.out.println("Key:" + strKeyValue);
		    key.add(strKeyValue);
		}
		index++;
		
		while ((ch = str.charAt(index)) == ' ' || ch == ':') {
			System.out.println("between:" + ch);
		    index++;
		}
		
		strKeyValue = "";
		if (ch == '\"') {
		    ch = str.charAt(++index);
		    while (ch != '\"') {
		    	strKeyValue += ch;
		    	ch = str.charAt(++index);
		    }
			index++;
		    System.out.println("value:" + strKeyValue);
		    value.add(strKeyValue);
		} else if (ch >= '0' && ch <= '9') {
		    while ((ch >= '0' && ch <= '9') || ch == '.') {
		    	strKeyValue += ch;
		    	if (ch == '.') {
		    		judge = true;
		    	}
		    	if (++index < str.length()) {
		    		ch = str.charAt(index);
		    	} else {
		    		break;
		    	}
		    }
		    
		    if (judge == true) {
		    	doubleValue = Double.parseDouble(strKeyValue);
//				System.out.println(doubleValue);
		    	value.add(doubleValue);
		    } else {
		    	intValue = Integer.parseInt(strKeyValue);
//				System.out.println(intValue);
		    	value.add(intValue);
		    }
		}
		
		while ((ch = str.charAt(index)) == ' ' || ch == '}' || ch == ',') {
			System.out.println("after:" + ch);
			if (index <= str.length()-2) {
				index++;
			} else {
				index++;
				break;
			}
		}
	}
	
    public Queue<String> getKey() {
    	return key;
    }
    
    public Queue<Object> getValue() {
    	return value;
    }
    
    public int getCount() {
    	return count;
    }
    
    public String toString() {
    	return "the jason has been parsered";
    }
    
}
