package userLayer;

import java.sql.ResultSet;
import java.sql.SQLException;

import postgresqlJDBC.*;

public class select {
    private JDBC con;
    private String[] key_word;
    boolean dirty;
    boolean materializer;
    
    public select(String[] originl_sql) throws SQLException {
	con = new JDBC();
	
	dirty = false;
	materializer = false;
	
	int count = con.count("dictionary");
	key_word = new String[count];
	int index = 0;
	String select = "";
	String from = "";
	String where = "";
	String value = "";
	
	String sql = "select * from dictionary";
	ResultSet rs = con.query(sql);
	
	if (rs != null) {
	    while (rs.next()) {
		key_word[index++] = rs.getString(2);
	    }
	} else {
	    System.out.println("none in the dictionary");
	}
	
	for (int i =0;i < originl_sql.length; i++) {
	    originl_sql[i].toLowerCase();
	    if (originl_sql[i].equals(KeyWord.select)) {
		System.out.println("select start");
		i++;
		for (int j = 0; j < key_word.length; j++) {
		    if (key_word[j].equals(originl_sql[i])) {
			select = originl_sql[i];
			break;
		    } 
		}
	    } else if (originl_sql[i].equals(KeyWord.from)) {
		i++;
		from = originl_sql[i];
	    } else if (originl_sql[i].equals(KeyWord.where)) {
		   i++;
		   for (int j = 0; j < key_word.length; j++) {
		       if (key_word[j].equals(originl_sql[i])) {
			    where = originl_sql[i];
			    break;
			}
		   }	
		    
		   for (i = i+1; i < originl_sql.length; i++) {
			value += originl_sql[i];
		   }
	    }
	}
	
	System.out.println("select: " + select);
	System.out.println("from: "+ from);
	System.out.println("where: " + where);
	System.out.println("value: " + value);
    }
    
    public void extract_key_txt(String tablename, String )
    
    public static void main(String[] args) throws SQLException {
	select l = new select(args);
    }
}
