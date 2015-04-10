package userLayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;

import postgresqlJDBC.JDBC;


public class Serialization implements Serializable {
    
    public String key[];
    public int aid[];
    public Object value[];
    
    public Serialization() throws IOException, SQLException {
	// TODO Auto-generated constructor stub
	Loader loader = new Loader();
	
	key = loader.key;
	aid = loader.aid;
	value = loader.value;
	
    }
    
//    public static void main(String[] args) throws IOException, SQLException {
//	Serialization s = new Serialization();
//	
//	System.out.println(s);
//    }
    
}
