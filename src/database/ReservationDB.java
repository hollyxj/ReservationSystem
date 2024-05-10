package database;
import java.io.IOException;
import java.sql.*;
import com.google.gson.*;
import encryption.*;
import java.io.FileReader;


public class ReservationDB {
	private Connection connection = null;
	
	public ReservationDB() {
		System.out.println("In rez db constructor");

		// Create users table in Database
		String sqlUsers = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, password TEXT, is_admin BOOLEAN, appointments TEXT)";
		createDBTable("users", sqlUsers);
		
		// Create users table in Database
		String sqlAvail = "CREATE TABLE IF NOT EXISTS availability (id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, date TEXT, appointmentType TEXT, who TEXT, notes TEXT, shortDescription TEXT)";
		createDBTable("availability", sqlAvail);
	}
	
	public void addUserToDB(String username, String email, String password, Boolean isAdmin, String appointments) throws SQLException {
	    PreparedStatement pstmt = connection.prepareStatement("insert into users (username, email, password, is_admin, appointments) values (?, ?, ?, ?, ?)");
	    pstmt.setString(1, username);
	    pstmt.setString(2, email);
	    pstmt.setString(3, password);
	    pstmt.setBoolean(4, isAdmin);
	    pstmt.setString(5, appointments);
	    
	    pstmt.executeUpdate();
	}
	
	public void updateAppointmentsColumn(String userEmail, String appointmentID) {
        try {
            // Update the "appointments" column of the user in the "users" table
            String updateQuery = "UPDATE users SET appointments = IFNULL(appointments, '') || ',' || ? WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, appointmentID);
            statement.setString(2, userEmail);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating appointments column: " + e.getMessage());
        }
    }
	
	public void addAvailabilityToDB(String time, String date, String appointmentType, String who, String notes, String shortDescription) throws SQLException {
	    PreparedStatement pstmt = connection.prepareStatement("insert into availability (time, date, appointmentType, who, notes, shortDescription) values (?, ?, ?, ?, ?, ?)");
	    
	    pstmt.setString(1, time);
	    pstmt.setString(2, date);
	    pstmt.setString(3, appointmentType);
	    pstmt.setString(4, who);
	    pstmt.setString(5, notes);
	    pstmt.setString(6, shortDescription);
	    
	    pstmt.executeUpdate();
	}
	
	
	public void createDBTable(String tableName, String sql) {
		System.out.println("In create DB!");
		this.connection = null;
		Statement statement = null;
		try {
			// Establish a connection
			this.connection = DriverManager.getConnection("jdbc:sqlite:reservations.db");
			System.out.println("Database connected");
	
			// Create a statement
			statement = connection.createStatement();
			statement.executeUpdate(sql);
			System.out.println("Table \"" + tableName + "\" created or already exists");
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
	
	public String getEncryptedPasswordFromDB(String email) throws SQLException {
        System.out.println("email="+email);

		PreparedStatement pstmt = connection.prepareStatement("select password from users where email=?");
	    pstmt.setString(1, email);
        ResultSet resultSet = pstmt.executeQuery();

        if (resultSet.next()) {
            String hashedPassword = resultSet.getString("password");
            System.out.println("---------------------------");
            System.out.println("hashedPassword="+hashedPassword);

    	    // return encrypted password
    	    return hashedPassword;
        } 
        return null;
	}
	
	 public String getAppointmentsForUser(String userEmail) {
	        try {
	            // Retrieve appointments for the user from the "users" table
	            String selectQuery = "SELECT appointments FROM users WHERE email = ?";
	            PreparedStatement statement = connection.prepareStatement(selectQuery);
	            statement.setString(1, userEmail);
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                return resultSet.getString("appointments");
	            }
	        } catch (SQLException e) {
	            System.err.println("Error retrieving appointments for user: " + e.getMessage());
	        }
	        return null;
	    }
	
	public void closeDB() throws SQLException {
		if (this.connection != null) {
			this.connection.close();
		}
	}
	
	public JsonArray getAvailabilityFromDB() {
        JsonArray jsonArray = new JsonArray();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
	            // Establish JDBC connection
	            connection = DriverManager.getConnection("jdbc:sqlite:reservations.db");

	            // Prepare SQL query
	            String sqlQuery = "SELECT * FROM availability";
	            pstmt = connection.prepareStatement(sqlQuery);

	            // Execute query
	            resultSet = pstmt.executeQuery();

	            // Convert ResultSet to JSON
	            jsonArray = resultSetToJson(resultSet);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } 
	        return jsonArray;
	    }

	    private JsonArray resultSetToJson(ResultSet resultSet) throws SQLException {
	        JsonArray jsonArray = new JsonArray();
	        ResultSetMetaData metaData = resultSet.getMetaData();
	        int columnCount = metaData.getColumnCount();

	        while (resultSet.next()) {
	            JsonObject jsonObject = new JsonObject();

	            for (int i = 1; i <= columnCount; i++) {
	                String columnName = metaData.getColumnLabel(i);
	                Object value = resultSet.getObject(i);
	                jsonObject.addProperty(columnName, value.toString());
	            }

	            jsonArray.add(jsonObject);
	        }

	        return jsonArray;
	    }
	
	    public String getNameFromEmail(String userEmail) {
	        try {
	            // Retrieve username from the "users" table based on email
	            String selectQuery = "SELECT username FROM users WHERE email = ?";
	            PreparedStatement statement = connection.prepareStatement(selectQuery);
	            statement.setString(1, userEmail);
	            ResultSet resultSet = statement.executeQuery();
	            if (resultSet.next()) {
	                return resultSet.getString("username");
	            }
	        } catch (SQLException e) {
	            System.err.println("Error retrieving name for email: " + e.getMessage());
	        }
	        return null;
	    }
	 
		public static void main(String[] args) {
			
		}
}