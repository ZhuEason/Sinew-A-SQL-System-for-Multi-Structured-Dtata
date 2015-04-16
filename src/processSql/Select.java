package processSql; 
import java.awt.RenderingHints.Key;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;

import postgresqlJDBC.JDBC;
import userLayer.Serialization;

public class Select extends SqlParser{
    private JDBC con;
    private boolean f;
    private boolean s;
    private boolean w;
    private boolean s_dirty;
    private boolean s_materializer;
    private boolean w_dirty;
    private boolean w_materializer;
    private String select = "";
    private String from = "";
    private String where = "";
    private String sign = "";
    private String value = "";
    
    public Select(String expSql) throws SQLException {
	super(expSql);
	con = new JDBC();
	
	f = false;
	s = false;
	w = false;
	s_dirty = false;
	s_materializer = false;
	w_dirty = false;
	w_materializer = false;
	
	Queue<Value> token = super.getToken();
	
	while (token.peek() != null) {
	    Value newValue = token.poll();
	    if (newValue.getTag() == KeyWord.SELECT) {
		s = true;
	    } else if (newValue.getTag() == KeyWord.KEY && s == true) {
		s = false;
		select = newValue.getValue();
	    } else if (newValue.getTag() == KeyWord.FROM) {
		f = true;
	    } else if (newValue.getTag() == KeyWord.VALUE && f == true) {
		f = false;
		from = newValue.getValue();
	    } else if (newValue.getTag() == KeyWord.WHERE) {
		w = true;
	    } else if (newValue.getTag() == KeyWord.KEY && w == true) {
		where = newValue.getValue();
	    } else if (newValue.getTag() == KeyWord.SIGN && w == true) {
		sign = newValue.getValue();
	    } else if (newValue.getTag() == KeyWord.VALUE && w ==true) {
		value = newValue.getValue();
		w = false;
	    }
	}
	
	//System.out.println("Value: " + value);
	extract_key_txt();
    }
    
public void extract_key_txt() throws SQLException {
	
	String sql;
	sql = "SELECT description.materialized, description.dirty FROM description WHERE description._id = (SELECT _id FROM dictionary WHERE key_name= '" + select + "')" ;
	ResultSet rs_select = con.query(sql);
	
	int select_id;
	int where_id;
	select_id = getId(select);
	where_id = getId(where);
	
	if (rs_select != null) {
	    if ( rs_select.next() ) {
		if ("f".equals(rs_select.getString(1))) {
		    s_materializer = false;
		} else if ("t".equals(rs_select.getString(1))) {
		    s_materializer = true;
		}
		
		if ("f".equals(rs_select.getString(2))) {
		    s_dirty = false;
		} else if ("t".equals(rs_select.getString(2))) {
		    s_dirty = true;
		}
	    }
	}
	
	sql = "SELECT description.materialized, description.dirty FROM description WHERE description._id = (SELECT _id FROM dictionary WHERE key_name= '" + where + "')" ;
	ResultSet rs_where = con.query(sql);
	
	if (rs_where != null) {
	    if (rs_where.next()) {
		if ("f".equals(rs_where.getString(1))) {
		    w_materializer = false;
		} else if ("t".equals(rs_where.getString(1))) {
		    w_materializer = true;
		}
		
		if ("f".equals(rs_where.getString(2))) {
		    w_dirty = false;
		} else if ("t".equals(rs_where.getString(2))) {
		    w_dirty = true;
		}
	    }
	}
	
	
	if (s_materializer && w_materializer) {
	  //两个都已经materialized的情况
	    sql = "SELECT " + select + " FROM " + from + " WHERE " + where + " " + sign + value;
	    ResultSet result = con.query(sql);
	    
	    System.out.println(select);
	    if (result != null) {
		while (result.next()) {
		    System.out.println(result.getObject(1));
		}
	    }
	} else if (!s_materializer && !w_materializer) {
	    //两个都没有materilized的情况	
	    System.out.println("select_id: " + select_id + "where_id: "+ where_id);
	    v_query(select_id, where_id);
		
	} else if (!s_materializer && w_materializer) {
	    //select没有materilized的情况
	    v_select(select_id, where);
	} else if (s_materializer && !w_materializer) {
	    //select has been materilized了，where not;
	    v_where(select, where_id);
	}
    }
    
    public void  v_select(int s_id, String where) {
	String sql = "SELECT * FROM " + from + " WHERE " + where + sign + value;
	ResultSet rs = con.query(sql);
	int s_index = 0;
	
	try {
	    if (rs != null) {
		while (rs.next()) {
		    byte[] Bytes = rs.getBytes(2);
		    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
		    Serialization s = (Serialization)ois.readObject();
		    
		    s_index = binarySearch(s.aid, s_id);
		    System.out.println(s.value[s_index]);
		    
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
    
    public void v_where(String select, int w_id) {
	int w_index = 0;
	ResultSet rs = con.byteQuery();
	try {
	    if (rs != null) {
		while (rs.next()) {
		    byte[] Bytes = rs.getBytes(2);
		    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
		    Serialization s = (Serialization)ois.readObject();
		    
		    w_index = binarySearch(s.aid, w_id);
		    if (w_index >= 0) {
			if (w_index > 0) {
			    if (sign.equals("=")) {
				if (s.value[w_index].toString().equals(value) && rs.getObject(select) != null) {
				    System.out.println(rs.getObject(select));
				}
			    } else if (sign.equals(">")) {
				if ((Double)s.value[w_index] > Double.parseDouble(value) && rs.getObject(select) != null) {
				    System.out.println(rs.getObject(select));
				}
			    } else if (sign.equals("<")) {
				if ((Double)s.value[w_index] < Double.parseDouble(value)&& rs.getObject(select) != null) {
				    System.out.println(rs.getObject(select));
				}
			    } else if (sign.equals(">=")) {
				if ((Double)s.value[w_index] >= Double.parseDouble(value) && rs.getObject(select) != null) {
				    System.out.println(rs.getObject(select));
				}
			    } else if (sign.equals("<=")) {
				if ((Double)s.value[w_index] <= Double.parseDouble(value) && rs.getObject(select) != null) {
				    System.out.println(rs.getObject(select));
				}
			    } else if (sign.equals("<>")) {
				if ((Double)s.value[w_index] != Double.parseDouble(value) && rs.getObject(select) != null) {
				    System.out.println(rs.getObject(select));
				}
			    }
			} else {
			    System.out.println("w_index not found");
			}
		    } else {
			System.out.println("s_index not found");
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
    }
    
    public void v_query(int s_id, int w_id) {
	
	int s_index = -1;
	int w_index = -1;
	ResultSet rs = con.byteQuery();
	
	try {
	    if (rs != null) {
		while (rs.next()) {
		    byte[] Bytes = rs.getBytes(2);
		    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
//		    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    Serialization s = (Serialization)ois.readObject();
		    
		    s_index = binarySearch(s.aid, s_id);
		    if (s_index >= 0) {
			w_index = binarySearch(s.aid, w_id);
			
			if (w_index >= 0) {
			    if (sign.equals("=")) {
				if (s.value[w_index].toString().equals(value)) {
				    System.out.println(s.value[s_index]);
				}
			    } else if (sign.equals(">")) {
				if ((Double)s.value[w_index] > Double.parseDouble(value)) {
				    System.out.println(s.value[s_index]);
				}
			    } else if (sign.equals("<")) {
				if ((Double)s.value[w_index] < Double.parseDouble(value)) {
				    System.out.println(s.value[s_index]);
				}
			    } else if (sign.equals(">=")) {
				if ((Double)s.value[w_index] >= Double.parseDouble(value)) {
				    System.out.println(s.value[s_index]);
				}
			    } else if (sign.equals("<=")) {
				if ((Double)s.value[w_index] <= Double.parseDouble(value)) {
				    System.out.println(s.value[s_index]);
				}
			    } else if (sign.equals("<>")) {
				if (!s.value[w_index].toString().equals(value)) {
				    System.out.println(s.value[s_index]);
				}
			    }   
			} else {
			    System.out.println("w_index not found");
			}
		    } else {
			System.out.println("s_index not found");
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
    }
    
   
    
    public static void main(String[] args) throws SQLException {
	String sql = "SELECT country FROM webrequests where hits < 23";
	String test = "SELECT co FROM webrequests where avg = 128.5";
	Select s = new Select(test);
    }
  
}
