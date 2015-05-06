package processSql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import postgresqlJDBC.JDBC;
import userVersion2.Data;

public class SqlParser {
    private String sqlExp;
    protected Queue<Value> sqlToken;
    private int pointer;
    private JDBC con;
    private String[] key_word;
    private String[] reserved_word;
    
    public SqlParser(String sqlExp) throws SQLException {
	// TODO Auto-generated constructor stub
		this.sqlExp = sqlExp;
		pointer = 0;
		sqlToken = new LinkedList<Value>();
		con = new JDBC();
	
	int count = con.count("dictionary");
	key_word = new String[count];
	int index = 0;

	reserved_word = new String[6];
	reserved_word[0] = "select";
	reserved_word[1] = "update";
	reserved_word[2] = "insert";
	reserved_word[3] = "from";
	reserved_word[4] = "set";
	reserved_word[5] = "where";

	
	String sql = "select * from dictionary";
	ResultSet rs = con.query(sql);
	
	if (rs != null) {
	    while (rs.next()) {
		key_word[index++] = rs.getString(2);
	    }
	} else {
	    System.out.println("none in the dictionary");
	}
	
	
	do {
	    Scanner();
	   // System.out.println("!!!!");
	} while (pointer < sqlExp.length());
    }
    
    public void Scanner() {
	String tempValue = "";
	boolean flag = false;
	
	while (sqlExp.charAt(pointer) == ' ') {
	    if (pointer < sqlExp.length()-1) {
		pointer++;
	    }
	}
	
	char ch = sqlExp.charAt(pointer);
	
	if ( (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '\'' ) {
	    while ( (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '\'' || (ch <= '9' && ch >= '0' ) || ch == '_' || ch == '-' || ch == '.' ) {
		tempValue += ch;
		 ++pointer;
		 if (pointer < sqlExp.length()) {
		     ch = sqlExp.charAt(pointer);
		 } else if (pointer == sqlExp.length()) {
		    break;
		}
	    }
	    //System.out.println(tempValue);
	    
	    tempValue = tempValue.toLowerCase();
	    
	    for (int i = 0; i < key_word.length; i++) {
			if (tempValue.equals(key_word[i])) {
				Value newValue = new Value(tempValue, KeyWord.KEY);
				System.out.println(newValue);
				sqlToken.add(newValue);
				flag = true;
			}
	    }
	    
	    for (int i = 0; i < reserved_word.length; i++) {
			if (tempValue.equals(reserved_word[i])) {
				Value newValue = new Value(tempValue, i);
				System.out.println(newValue);
				sqlToken.add(newValue);
				flag = true;
			}
	    }
	    
	    if (!flag) {
		Value  newValue =new Value(tempValue, KeyWord.VALUE);
		System.out.println(newValue);
		sqlToken.add(newValue);
	    }
	    
	} else if (ch >= '0' && ch <= '9') {
	    while ( (ch >= '0' && ch <= '9') || ch == '.') {
		tempValue += ch;
		pointer++;
		if (pointer < sqlExp.length()) {
		    ch = sqlExp.charAt(pointer);
		} else if (pointer == sqlExp.length()) {
		    break;
		}
	    }
	    Value newValue = new Value(tempValue, KeyWord.VALUE);
	    System.out.println(newValue);
	    sqlToken.add(newValue);
	} else {
	    switch (ch) {
	    case '<' :
		tempValue += ch;
		if (pointer < sqlExp.length()-1) {
		    ch = sqlExp.charAt(++pointer);
		}
		if (ch == '>') {
		    tempValue += ch;
		    pointer++;
		} else if ( ch == '=') {
		    pointer++;
		    tempValue += ch;
		}
		break;
	    case '>' :
		tempValue += ch;
		if (pointer < sqlExp.length()-1) {
		    ch = sqlExp.charAt(++pointer);
		}
		if (ch == '=') {
		    tempValue += ch;
		    pointer++;
		}
		break;
	    case '=':
		tempValue += ch;
		if (pointer < sqlExp.length()-1) {
		    ch = sqlExp.charAt(++pointer);
		}
		break;
	    }
	    
	    Value newValue = new Value(tempValue, KeyWord.SIGN);
	    System.out.println(newValue);
	    sqlToken.add(newValue);
	}
    }
    
    public int getId(String select_name) throws SQLException {
		int id = -1;
		String sql = "select _id from dictionary where key_name =  '" + select_name + "'";
		ResultSet rs = con.query(sql);
		if (rs != null) {
		    while (rs.next() ) {
				id = rs.getInt(1);
		    }
		}
   	
		return id; 
    }
       
       
    public static int binarySearch(int[] array, int value) {
		int low = 0; 
		int high = array.length - 1;
   	
		while (low <= high) {
		    int middle = (low + high) / 2;
   	    
		    if (value == array[middle]) {
				return middle;
			 } 
   	    
			if (value > array[middle]) {
				low = middle + 1;
			} else if (value < array[middle]) {
				high = middle - 1;
			}
		}
   	
		return -1;
     }
    
    public Queue<Value> getToken() {
		return sqlToken;
    }
    
    public void write_back(ResultSet rs, Data s) throws IOException, SQLException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
	oos.writeObject(s);
	byte[] bytes = baos.toByteArray();
	
     //con.byteInsert(bytes);
	
	rs.updateBytes(2, bytes);
	rs.updateRow();
    }
    
    public static void main(String[] args) throws SQLException {
	String sql = "SELECT url FROM webrequests where hits < 35";
	String update = "UPDATE person SET url = 'www' WHERE count = 1";
	//String test = "web";
	String num = "1";
	String test = "SELECT co FROM webrequests where avg = 128.5";
	SqlParser s = new SqlParser(test);
    }
}
