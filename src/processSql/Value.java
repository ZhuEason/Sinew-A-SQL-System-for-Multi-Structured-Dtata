package processSql;

public class Value {
    	private String value;
    	private int tag;
    	
    	public Value(String value, int tag) {
    	    this.value = value;
    	    this.tag = tag;
    	}
    	
    	public String getValue() {
    	    return value;
    	}
    	
    	public int getTag() {
    	    return tag;
    	}
    	
    	public String toString() {
    	  return "(" + this.value + ", " + this.tag + ")";  
    	}
}
