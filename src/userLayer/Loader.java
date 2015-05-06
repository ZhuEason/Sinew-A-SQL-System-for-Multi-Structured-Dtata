package userLayer;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import postgresqlJDBC.*;

public class Loader extends JasonParser{
    private JDBC con;
    public String key[];
    public Object value[];
    public int aid[];
    
    public Loader() throws IOException, SQLException {
	super();
	con = new JDBC();
	
	value = new Object[getCount()];
	aid = new int[getCount()];
	key = new String[getCount()];
	
	String type = "";
	String sql = "";
	
	ResultSet rs;
	
	for (int i = 0; i < getCount(); i++) {
	    value[i] = getValue().poll();
	    key[i] = getKey().poll();
//	    System.out.println(key[i]);
	    
	    sql = "SELECT _id FROM dictionary WHERE key_name = '" + key[i] + "'";
	    rs =  con.query(sql);
	    
	    if (!rs.next()) {
		int new_id = con.count("dictionary");
		if (value[i] instanceof Integer) {
		    type = "integer";
		} else if (value[i] instanceof Double) {
		    type = "double";
		} else if (value[i] instanceof String) {
		    type = "string";
		} else {
		    type = "else";
		}
		sql = "INSERT INTO dictionary VALUES(" + (new_id+1) + ", \'" + key[i] + "\', \'"  + type + "')";
		con.update(sql);
		aid[i] = new_id + 1;
		sql = "INSERT INTO description VALUES(" + (new_id+1) +", " + 1 + " ,\'f\' " + ", \'f\'" + ")";
		con.update(sql);
	    } else {
		aid[i] = rs.getInt(1);
		int count = 0;
		int old_id = rs.getInt(1);
		sql = "SELECT count FROM description WHERE _id = " + old_id ;
		rs = con.query(sql);
		if (rs.next()) {
		    count = rs.getInt(1);
		}
		sql = "UPDATE description SET count = " + (count+1) + " WHERE _id = " + old_id;

		con.update(sql);
		
	    }
	    
	}	
	
	int count;
	    
	    
	sql = "SELECT count FROM sum" ;
	rs = con.query(sql);   
	
	if (rs.next()) {
		count = rs.getInt(1);
	} else {
		count = 0;
		sql = "INSERT INTO sum VALUES(" + 0 + ")";
		con.update(sql);
	}
	sql = "UPDATE sum SET count = " + (count+1);
	con.update(sql);
	
	quickSort(0, aid.length-1);
	
	for (int i = 0; i < aid.length; i++) {
	    System.out.println(aid[i]);
	}
    }
    
    public void quickSort( int l, int h) {
	int a_key;
	Object o_key;
	int i = l, j = h;
	
	if (l < h) {
	    a_key = aid[l];
	    o_key = value[l];
	    
	    while (i < j) {
		while (i < j && aid[j] >= a_key) {
		    j--;
		}
		aid[i] = aid[j];
		value[i] = value[j];
		
		while (i < j && aid[i] < a_key) {
		    i++;
		}
		aid[j] = aid[i];
		value[j] = value[i];
	    }
	    
	    aid[j] = a_key;
		value[i] = o_key;
	//    System.out.println("lalallalal");
	    quickSort(l, j);
	    quickSort(j+1, h);
	}
    }
    
//    public static void main(String args[]) throws IOException, SQLException {
//	
//	Loader l = new Loader();
//	
////	int aid[] = {4, 3};
////	
////	l.quickSort(aid, 0, 1);
//	
//    }
}
