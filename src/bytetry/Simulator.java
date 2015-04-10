package bytetry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import postgresqlJDBC.*;

public class Simulator {
    private byte[] bytes;
    private JDBC con;
    
    public static void main(String[] args) {
	
	new Simulator().go();
    }
    
    public void go()  {
	con = new JDBC();
	Book book = new Book();
	try {
//	    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("seria"));
//	    out.writeObject(book);
//	    System.out.println("Object has been written..");
//	    out.close();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(book);
	    bytes = baos.toByteArray();
	    con.byteInsert(bytes);
	    
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	try {
//	    ObjectInputStream in = new ObjectInputStream(new FileInputStream("seria"));
//	    Book bookRead = (Book) in.readObject();
//	    System.out.println("object read here:");
//	    System.out.println(bookRead);
	    
//	    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//	    ObjectInputStream ois = new ObjectInputStream(bais);
//	    Book bookRead = (Book)ois.readObject();
//	    System.out.println(bookRead);
	    
	    con.byteQuery();
	    if (con.getResult() != null) {
		while (con.getResult().next()) {
		    byte[] bookBytes = con.getResult().getBytes(1);
		    ByteArrayInputStream bais = new ByteArrayInputStream(bookBytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
		    Book bookRead = (Book)ois.readObject();
		    System.out.println(bookRead);
		}
	    }
	    
	} catch(SQLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }

}
