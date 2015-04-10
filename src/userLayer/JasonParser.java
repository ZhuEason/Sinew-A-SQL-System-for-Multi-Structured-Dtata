package userLayer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class JasonParser extends Object{
    protected Queue<String> key;
    protected Queue<Object> value;
    private int count;
    
    public JasonParser() throws IOException {
	
	count = 0;
	key = new LinkedList<String>();
	value = new LinkedList<Object>();
	
	String s;
	FileReader fr = new FileReader("/home/eason/workspace/SQLsystem/src/jsonTxt/jsonText");
	BufferedReader br = new BufferedReader(fr);
	while ((s = br.readLine()) !=null) {
//	    System.out.println(s);
	    if (! (s.equals("{") || s.equals("}"))) {
		scanner(s);
		count++;
	    }
	}
	br.close();
    }
    
    public void scanner(String str) {
	int index = 0;
	char ch;
	
	boolean judge = false;
	Integer intValue = 0;
	Double doubleValue = 0.0;
	String strKeyValue = "";
	
	
	while ((ch = str.charAt(index)) == ' ' || ch == '\t' ) {
	    index++;
	}
	strKeyValue = "";
	if (ch == '\"') {
	    ch = str.charAt(++index);
	    while (ch != '"') {
		strKeyValue += ch;
		ch = str.charAt(++index);
	    }
//	    System.out.println(strKeyValue);
	    key.add(strKeyValue);
	}
	index++;
	
	while ((ch = str.charAt(index)) == ' ' || ch == ':') {
	    index++;
	}
	strKeyValue = "";
	if (ch == '\"') {
	    ch = str.charAt(++index);
	    while (ch != '\"') {
		strKeyValue += ch;
		ch = str.charAt(++index);
	    }
//	    System.out.println(strKeyValue);
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
//		System.out.println(doubleValue);
		value.add(doubleValue);
	    } else {
		intValue = Integer.parseInt(strKeyValue);
//		System.out.println(intValue);
		value.add(intValue);
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
    
//    public static void main(String args[]) throws IOException {
//	JasonParser jasonParser = new JasonParser();
//	
//    }
 
}
