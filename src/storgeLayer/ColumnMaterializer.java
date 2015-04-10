package storgeLayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import userLayer.Serialization;
import postgresqlJDBC.*;

public class ColumnMaterializer {
    private JDBC con;
    
    public ColumnMaterializer() throws SQLException {
	// TODO Auto-generated constructor stub
	String sql;
	con = new JDBC();
	
	int id;
	
	sql = "select d._id from description d, sum s where round(d.count,1)/s.count > 0.6";
	ResultSet rs = con.query(sql);
	if (rs != null) {
		while (rs.next()) {
		    id = rs.getInt(1);    
		    deserialization(id);
		}
	} else {
		sql = "INSERT INTO sum VALUES(" + 0 + ")";
		con.update(sql);
	}
	
    }
    
    public void deserialization(int id) {
	
	int index = 0;
	ResultSet rs = con.byteQuery();
	try {
	    if (rs != null) {
		while (rs.next()) {
		    byte[] Bytes = rs.getBytes(2);
		    int in_id = rs.getInt(1);
		    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
		    Serialization s = (Serialization)ois.readObject();
		    
		    index = binarySearch(s.aid, id);
		    System.out.println("index: " + index);
		    if (index >= 0) {
			s.aid[index] = -1;
			materializer(in_id, id, s.value[index]);
			s.value[index] = null;
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(s);
			byte[] bytes = baos.toByteArray();
			
		     //con.byteInsert(bytes);
			
			rs.updateBytes(2, bytes);
			rs.updateRow();
			
		    } else {
			System.out.println("index not found");
		    }
		}
	    } 
	} catch(SQLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
	
//	try {
//	    
//	    con.byteQuery();
//	    if (con.getResult() != null) {
//		while (con.getResult().next()) {
//		    byte[] SeriaBytes = con.getResult().getBytes(2);
//		    ByteArrayInputStream bais = new ByteArrayInputStream(SeriaBytes);
//		    ObjectInputStream ois = new ObjectInputStream(bais);
//		    Serialization s = (Serialization)ois.readObject();
//		    System.out.println("aid[index]: " + s.aid[index]);
//		}
//	    }
//	    
//	} catch(SQLException e) {
//	    e.printStackTrace();
//	} catch (IOException e) {
//	    e.printStackTrace();
//	} catch (ClassNotFoundException e) {
//	    e.printStackTrace();
//	}
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
    
    public void materializer(int in_id, int id, Object value) throws SQLException {
	String sql = "SELECT * FROM dictionary WHERE _id = " + id;
	
	ResultSet rs = con.query(sql);
	
	if (rs != null) {
	    while (rs.next()) {
		String in_columnName = rs.getString(2);
		String type = rs.getString(3);
		String in_type = "";
		
		if (type.equals("string")) {
		    in_type = "varchar(20)";
		} else if (type.equals("double")) {
		    in_type = "double precision";
		} else if (type.equals("integer")) {
		    in_type = "integer";
		}
		
		sql = "SELECT * FROM description WHERE _id = " + id;
		ResultSet rs2 = con.query(sql);
		if (rs2.next()) {
		    String is_materialize = rs2.getString(3);
		    System.out.println("materialize:" + is_materialize);
		    if (is_materialize.equals("f")) {
			sql = "ALTER TABLE webrequests ADD " + in_columnName + " " + in_type;
			con.update(sql);
			rs2.updateString(3, "t");
			rs2.updateString(4, "t");
			rs2.updateRow();
		    }
		}
		
		sql = "UPDATE webrequests SET " + in_columnName + "=" + "'"  + value +"'" + " WHERE id = " + in_id;
		System.out.println(sql);
		con.update(sql);
		}
	    }
	}
    
    public static void main(String[] args) throws SQLException {
	ColumnMaterializer column = new ColumnMaterializer();
    }
}
