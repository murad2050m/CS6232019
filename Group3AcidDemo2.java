package CS623;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Group3AcidDemo2 {

	public static void main(String args[]) throws SQLException, IOException, ClassNotFoundException {

		// Load the MySQL driver
		//Class.forName("com.mysql.jdbc.Driver");

		// Connect to the database
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS623?serverTimezone=UTC","username","password");
		
		// For atomicity
		conn.setAutoCommit(false);
		
		// For isolation 
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); 
		
		System.out.println("Check initial status...");
		Statement stmt1=conn.createStatement();
		ResultSet rs=stmt1.executeQuery("Select * FROM product");
		System.out.println("In Table <product>");
		while (rs.next()){
			System.out.println(rs.getString("prod_id")+","+rs.getString("pname"));
		}
		rs=stmt1.executeQuery("Select * FROM Stock");
		System.out.println("In Table <stock>");
		while (rs.next()){
			System.out.println(rs.getString("prod_id")+","+rs.getString("depo_id")+","+rs.getString("quantity"));
		}
		
		Statement stmt2 = null;
		try {
			// create statement object
			stmt2 = conn.createStatement();
			
			//DROP FOREIGN KEY CONSTRAINT > update values > ADD FOREIGN KEY CONSTRAINT
			//IMOPRTANT NOTE: MySQL does not rollback() ALTER TABLE statements!!! This may defect the data!!!
			stmt2.execute("ALTER TABLE Stock DROP FOREIGN KEY fk_stock_prod;"); //ALTER TABLE will auto commit in MySQL!!!
			stmt2.executeUpdate("UPDATE product SET prod_id='p1' WHERE prod_id='pp1'");
			stmt2.executeUpdate("UPDATE stock SET prod_id='p1' WHERE prod_id='pp1'");
			stmt2.execute("JnALTER TABLE stock ADD CONSTRAINT fk_stock_prod FOREIGN KEY (prod_id) "
					+ "REFERENCES Product(prod_id) ON DELETE CASCADE;");
			
		} catch (SQLException e) {
			System.out.println("Transaction failure, rollback procedure. Catch Exception " +e);
			// For atomicity
			conn.rollback();
			stmt2.close();
			conn.close();
			return;
		} // main
		conn.commit();
		stmt2.close();
		
		System.out.println("Transaction successful. Check final status...");
		rs=stmt1.executeQuery("Select * FROM product");
		System.out.println("In Table <product>");
		while (rs.next()){
			System.out.println(rs.getString("prod_id")+","+rs.getString("pname"));
		}
		rs=stmt1.executeQuery("Select * FROM Stock");
		System.out.println("In Table <stock>");
		while (rs.next()){
			System.out.println(rs.getString("prod_id")+","+rs.getString("depo_id")+","+rs.getString("quantity"));
		}
		
		stmt1.close();
		conn.close();
	}
}