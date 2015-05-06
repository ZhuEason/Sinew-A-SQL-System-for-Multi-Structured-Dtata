package userVersion2;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import postgresqlJDBC.JDBC;

public class Serialization {
	
	public static void writeObject(Data obj)
	{
		JDBC con;
		con = new JDBC();
		byte[] bytes;
		
		try {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(baos);
		    oos.writeObject(obj);
		    bytes = baos.toByteArray();
		    con.byteInsert(bytes);
		    
			
		    
		    
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    
	}

}

//		    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("seria"));
//		    out.writeObject(book);
//		    System.out.println("Object has been written..");
//		    out.close();
//
//
//		    	ResultSet rs = con.byteQuery();
			
//			try {
//			    if (rs != null) {
//				while (rs.next()) {
//				    byte[] Bytes = rs.getBytes(2);
//				    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
//				    ObjectInputStream ois = new ObjectInputStream(bais);
//				    Serialization o = (Serialization)ois.readObject();
//				}
//			    }
//			}catch(SQLException e) {
//			    e.printStackTrace();
//			} catch (IOException e) {
//			    e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//			    e.printStackTrace();
//			}
		    
//		    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//		    ObjectInputStream ois = new ObjectInputStream(bais);
//		    Serialization p = (Serialization)ois.readObject();
