import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RDSToCSV {
	private static String[] loadSettings() {
		String[] settings = new String[6];
		try {
			BufferedReader br = new BufferedReader(new FileReader("settings.db"));
			String line = null;
			for (int i = 0; i < 6; i++) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				settings[i] = line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return settings;
	}

	private static DBConnect getDatabaseConnection() throws SQLException {
		String[] settings = loadSettings();
		return new DBConnect(settings[0], settings[1], Integer.parseInt(settings[2]), settings[3], settings[4],
				settings[5]);
	}
	
	private static void printResultSet(ResultSet result) throws SQLException {
		int i = 0;
		while (result.next()) {
			i++;
		}
		System.out.println("TOTAL: " + i);
	}

	public static void pullRowsIntoCSV() throws SQLException {
		DBConnect dbc = getDatabaseConnection();
		dbc.executeQuery("select * from cis400db");
	}
	
	public static void createDatabase() throws SQLException {
		String[] settings = loadSettings();
		DBConnect dbc = new DBConnect(settings[0], "", Integer.parseInt(settings[2]), settings[3], settings[4],
				settings[5]);
		dbc.executeUpdate("CREATE DATABASE cis400db");
		System.out.println("Created database cis400db");
	}

	public static void createTable() throws SQLException {
		DBConnect dbc = getDatabaseConnection();
		ResultSet rs = dbc.executeUpdate(
				"CREATE TABLE game_data (time INT, a INT, b INT, c INT, d INT, e INT, held INT, friend_held INT, foe1_held INT, foe2_held INT, action INT)");
		System.out.println("Created table game_data");
	}
	
	public static void main(String[] args) {
		try {
			createTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
