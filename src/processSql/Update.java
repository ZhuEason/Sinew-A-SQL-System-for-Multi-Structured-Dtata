package processSql;

import java.awt.RenderingHints.Key;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;

import postgresqlJDBC.JDBC;
import userLayer.Serialization;

public class Update extends SqlParser{
    private JDBC con;
    private String tablename;
    private String s_key;
    private String s_sign;
    private String s_value;
    private String w_key;
    private String w_sign;
    private String w_value;
    private boolean u;
    private boolean s;
    private boolean w;
    private boolean s_dirty;
    private boolean s_materializer;
    private boolean w_dirty;
    private boolean w_materializer;
    
    public Update( String sqlExp) throws SQLException {
	super(sqlExp);
	
	con = new JDBC();
	u = false;
	s = false;
	w = false;
	Queue<Value> token = super.getToken();
	
	while (token.peek() != null) {
	    Value newValue = token.poll();
	    
	    if (newValue.getTag() == KeyWord.UPDATE) {
		u = true;
	    } else if (newValue.getTag() == KeyWord.VALUE && u == true) {
		tablename = newValue.getValue();
		u = false;
	    } else if (newValue.getTag() == KeyWord.SET) {
		s = true;
	    } else if (newValue.getTag() == KeyWord.KEY && s == true) {
		s_key = newValue.getValue();
	    } else if (newValue.getTag() == KeyWord.SIGN && s == true) {
		s_sign = newValue.getValue();
	    } else if (newValue.getTag() == KeyWord.VALUE && s == true) {
		s_value = newValue.getValue();
		s = false;
	    } else if (newValue.getTag() == KeyWord.WHERE ) {
		w = true;
	    } else if (newValue.getTag() == KeyWord.KEY && w == true) {
		w_key = newValue.getValue();
	    } else  if (newValue.getTag() == KeyWord.SIGN && w == true) {
		w_sign = newValue.getValue();
	    } else if (newValue.getTag() == KeyWord.VALUE && w == true) {
		w_value = newValue.getValue();
		w = false;
	    }
	}
	
//	System.out.println("tablename :" + tablename);
//	System.out.println("s_key :" + s_key);
//	System.out.println("s_sign :" + s_sign);
//	System.out.println("s_value :" + s_value);
//	System.out.println("w_key :" + w_key);
//	System.out.println("w_sign :" + w_sign);
//	System.out.println("w_value :" + w_value);
	
	extract_key_txt();
	
    }
    
    public void extract_key_txt() throws SQLException {
	String sql;
	sql = "SELECT description.materialized, description.dirty FROM description WHERE description._id = (SELECT _id FROM dictionary WHERE key_name= '" + s_key + "')" ;
	ResultSet rs_select = con.query(sql);
	
	int s_key_id;
	int w_key_id;
	s_key_id = getId(s_key);
	w_key_id = getId(w_key);
	
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
	
	sql = "SELECT description.materialized, description.dirty FROM description WHERE description._id = (SELECT _id FROM dictionary WHERE key_name= '" + w_key + "')" ;
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
	    m_update();
	} else if (!s_materializer && w_materializer) {
	    //set被materialized了，where没有materialized
	    v_where(w_key_id);
	} else if (s_materializer && !w_materializer) {
	    v_set(s_key_id);
	} else {
	    v_update(s_key_id, w_key_id);
	}
	
    }
    
    public void m_update() {
	String sql = "UPDATE " + tablename + " SET " + s_key + s_sign + s_value + " WHERE " + w_key + w_sign + w_value;
	con.update(sql);
    }
    
    public void v_where (int w_id) {
	int w_index;
	ResultSet rs = con.byteQuery();
	
	try {
	    if (rs != null) {
		while (rs.next()) {
		    byte[] Bytes = rs.getBytes(2);
		    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
		    Serialization s = (Serialization)ois.readObject();
		    
		    w_index = binarySearch(s.aid, w_id);
		    if (w_index > 0) {
			if (w_sign.equals("=")) {
				if (s.value[w_index].equals(w_value) ) {
				    rs.updateObject(s_key, s_value);
				}
			} else if (w_sign.equals(">")) {
			    	if ((Double)s.value[w_index] > Double.parseDouble(w_value)) {
			    	    rs.updateObject(s_key, s_value);
			    	}
			} else if (w_sign.equals("<")) {
			       if ((Double)s.value[w_index] < Double.parseDouble(w_value)) {
				   rs.updateObject(s_key, s_value);
			       }
			} else if (w_sign.equals("<=")) {
			    	if ((Double)s.value[w_index] <= Double.parseDouble(w_value)) {
				   rs.updateObject(s_key, s_value);
			       }
			} else if (w_sign.equals(">=")) {
			    	if ((Double)s.value[w_index] >= Double.parseDouble(w_value)) {
			    	    rs.updateObject(s_key, s_value);
			       }
			} else if (w_sign.equals("<>")) {
			    	if ((Double)s.value[w_index] != Double.parseDouble(w_value)) {
			    	    rs.updateObject(s_key, s_value);
			       }
			}
		    }
		}
	    }
	}catch(SQLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    public void v_set(int s_id) {
	String sql = "SELECT * FROM " + tablename + " WHERE " + w_key + w_sign + w_value;
	ResultSet rs = con.query(sql);
	int s_index = -1;
	
	try {
	    if (rs != null) {
		while (rs.next()) {
		    byte[] Bytes = rs.getBytes(2);
		    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
		    Serialization s = (Serialization)ois.readObject();
		    
		    s_index = binarySearch(s.aid, s_id);
		    if (s_index >= 0) {
			s.value[s_index] = s_value;
			
			write_back(rs, s);
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
    
    public void v_update(int s_id, int w_id) {
	int s_index = -1;
	int w_index = -1;
	ResultSet rs = con.byteQuery();
	
	try {
	    if (rs != null) {
		while (rs.next()) {
		    byte[] Bytes = rs.getBytes(2);
		    ByteArrayInputStream bais = new ByteArrayInputStream(Bytes);
		    ObjectInputStream ois = new ObjectInputStream(bais);
		    Serialization s = (Serialization)ois.readObject();
		    
		    s_index = binarySearch(s.aid, s_id);
		    w_index = binarySearch(s.aid, w_id);
		    
		    if (w_index >= 0 && s_index >= 0) {
			if (w_sign.equals("=")) {
			    if (s.value[w_index].toString().equals(w_value)) {
				s.value[s_index] = s_value;
				write_back(rs, s);
			    }
			} else if (w_sign.equals(">") ) {
			     if ((Double)s.value[w_index] > Double.parseDouble(w_value)) {
				s.value[s_index] = s_value;
				write_back(rs, s);
			     }
			} else if (w_sign.equals("<")) {
			    if ((Double)s.value[w_index] < Double.parseDouble(w_value)) {
				s.value[s_index] = s_value;
				write_back(rs, s);
			} else if (w_sign.equals("<=")) {
			    if ((Double)s.value[w_index] <= Double.parseDouble(w_value)) {
				s.value[s_index] = s_value;
				write_back(rs, s);
			    }
			} else if (w_sign.equals(">=")) {
			    if ((Double)s.value[w_index] >= Double.parseDouble(w_value)) {
				s.value[s_index] = s_value;
				write_back(rs, s);
			    }
			} else if (w_sign.equals("<>")) {
			    if (!s.value[w_index].toString().equals(w_value)) {
				s.value[s_index] = s_value;
				write_back(rs, s);
			    }
			}
			}
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
	String sql = "UPDATE webrequests SET co = 40 WHERE avg = 128.5";
	Update u = new Update(sql);
    }
}
