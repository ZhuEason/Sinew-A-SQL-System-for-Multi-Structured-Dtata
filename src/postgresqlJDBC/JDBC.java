package postgresqlJDBC;

import java.sql.*;

import javax.naming.spi.DirStateFactory.Result;

public class JDBC {
    
    	public final static String url = "jdbc:postgresql://127.0.0.1/mydb"; // the name of the database that you want to open.
	public final static String  usr = "postgres";// the usrname of the database
        public final static String psd = "0";// the password for the db
        private Connection conn = null;
        
        int count = 0;
        private ResultSet resultSet = null;
        private PreparedStatement pstmt = null;
        private Statement st = null;
        
        public JDBC() {
            conn = connectionDB();
        }
        
        @SuppressWarnings("finally")
	public Connection connectionDB() {
            try {
		Class.forName("org.postgresql.Driver");//the name of the JDBC
		conn = DriverManager.getConnection(url, usr, psd);
            } catch (Exception e) {
		e.printStackTrace();
		System.err.println(e.getClass().getName() + ": " + e.getMessage());
		System.exit(0);
            } finally {
        	System.out.println("Opened database successfully");
        	return conn;
            }
        }
        
        //执行查询数据库的sql语句等
        @SuppressWarnings("finally")
	public ResultSet query(String sql) {
            try {
        	//pstmt = conn.prepareStatement(sql);
        	//resultSet = pstmt.executeQuery();
        	
        	st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
        	resultSet = st.executeQuery(sql);
            } catch (SQLException e) {
        	System.out.println(sql + " error");
        	e.printStackTrace();
            } finally {
        	System.out.println(sql + " has been successfully executed");
        	return resultSet;
            }
        }
        
        //执行insert, update, delete以及create table和drop table等
        public int update(String sql) {
            try {
        	pstmt = conn.prepareStatement(sql);
        	count = pstmt.executeUpdate();
            } catch(SQLException e) {
        	e.printStackTrace();
        	System.out.println("update error");
            }
            System.out.println("" + count + " records has been updated");
            return count;
        }
        
        public int count(String table_name) throws SQLException {
            int count = 0;
            
            ResultSet rs = query("SELECT COUNT(*) FROM " + table_name);
            if (rs.next()) {
        	count = rs.getInt(1);
            }
            
            return count;
        }
        
        @SuppressWarnings("finally")
	public ResultSet byteInsert(byte b[]) {
            String sql = new String();
            try {
        	int i = 1;
        	sql = "INSERT INTO webrequests (virtual) VALUES(?)";
        	pstmt = conn.prepareStatement(sql);
        	pstmt.setBytes(1, b);
        	count = pstmt.executeUpdate();
//	    ObjectInputStream in = new ObjectInputStream(new FileInputStream("seria"));
//    	    Book bookRead = (Book) in.readObject();
//    	    System.out.println("object read here:");
//    	    System.out.println(bookRead);
    	    
            } catch (SQLException e) {
        	System.out.println(sql + " error");
        	e.printStackTrace();
            } finally {
        	System.out.println(sql + " has been successfully executed");
        	return resultSet;
            }
        }
        
        @SuppressWarnings("finally")
	public ResultSet byteQuery() {
            String sql = new String();
            try {
        	sql = "SELECT * FROM webrequests";
        	//pstmt = conn.prepareStatement(sql);
        	st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
        	resultSet = st.executeQuery(sql);
            } catch (SQLException e) {
        	System.out.println(sql + " error");
        	e.printStackTrace();
            } finally {
        	System.out.println(sql + " has been successfully executed");
        	return resultSet;
            }
        }
        
        //返回查询结果集
        public ResultSet getResult () {
            return resultSet;
        }
        
        
//        public static void main(String args[]) throws SQLException {
//            JDBC myJdbc = new JDBC();
//            
//            System.out.println(myJdbc.count("weather"));
//            
//            myJdbc.update("INSERT INTO weather VALUES (\'Guangzhou\', 10, 40, 26.7, \'2015-03-23 \')");
//            
//            myJdbc.query("SELECT * FROM weather WHERE citi = 'AN'");
//   
//            ResultSet rs = myJdbc.getResult();
//            if (!rs.next()) {
//        	System.out.println("no tuople");
//            } else {
//        	do {
//            		Integer temp = rs.getInt(2); 
//            		System.out.println(temp);
//        	} while (rs.next());
//            }
//       }
}
