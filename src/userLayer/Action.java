package userLayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import bytetry.Book;
import postgresqlJDBC.JDBC;

public class Action{
    
    public Action() throws IOException, SQLException, ClassNotFoundException {
	// TODO Auto-generated constructor stub
	serialization();
    }
    
    public static void serialization () throws IOException, SQLException, ClassNotFoundException {
	byte[] bytes;
	JDBC con;
	
	Serialization s = new Serialization();
	
	con = new JDBC();
	
	try {
//	    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("seria"));
//	    out.writeObject(book);
//	    System.out.println("Object has been written..");
//	    out.close();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(s);
	    bytes = baos.toByteArray();
	    con.byteInsert(bytes);
	    
		
	    
//	    	ResultSet rs = con.byteQuery();
		
//		try {
//		    if (rs != null) {
//			while (rs.next()) {
//			    byte[] Bytes = rs.getBytes(2);
//			    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
//			    ObjectInputStream ois = new ObjectInputStream(bais);
//			    Serialization o = (Serialization)ois.readObject();
//			}
//		    }
//		}catch(SQLException e) {
//		    e.printStackTrace();
//		} catch (IOException e) {
//		    e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//		    e.printStackTrace();
//		}
	    
//	    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//	    ObjectInputStream ois = new ObjectInputStream(bais);
//	    Serialization p = (Serialization)ois.readObject();
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
  
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
	  Action action = new Action();
    }
}
