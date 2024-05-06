package database;
import java.sql.*;

public class ReservationDB {
	private Connection connection = null;
	
	public ReservationDB() {
		System.out.println("In rez db constructor");

		createDB();
	}
	
	public void addUserToDB(String username, String email, String password) throws SQLException {
	    PreparedStatement pstmt = connection.prepareStatement("insert into users (username, email, password) values (?, ?, ?)");
	    pstmt.setString(1, username);
	    pstmt.setString(2, email);
	    pstmt.setString(3, password);
	    
	    pstmt.executeUpdate();
	}

	
	public void createDB() {
		System.out.println("In create DB!");
		this.connection = null;
		Statement statement = null;
		try {
			// Establish a connection
			this.connection = DriverManager.getConnection("jdbc:sqlite:reservations.db");
			System.out.println("Database connected");
	
			// Create a statement
			statement = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, password TEXT)";
			statement.executeUpdate(sql);
			System.out.println("Table \"reservations\" created or already exists");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ":" + e.getMessage());
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ":" + e.getMessage());
			}
		}
	}
	
	public void closeDB() throws SQLException {
		if (this.connection != null) {
			this.connection.close();
		}
	}
	
	public static void main(String[] args) {
		
	}
}