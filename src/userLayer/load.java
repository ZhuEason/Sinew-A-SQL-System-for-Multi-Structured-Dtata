package userLayer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import postgresqlJDBC.JDBC;

public class load {
    
    public load() throws IOException, SQLException {
	// TODO Auto-generated constructor stub
	serialization();
    }
    
    public static void serialization () throws IOException, SQLException {
	byte[] bytes;
	JDBC con;
	Serialization s;
	
	s = new Serialization();
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
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	
    }
    
    public static void main(String[] args) throws IOException, SQLException {
	  load action = new load();
    }
}
