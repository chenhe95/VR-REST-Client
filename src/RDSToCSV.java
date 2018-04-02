import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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

	public static void pullRowsIntoCSV(String fileName) throws SQLException {

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(fileName));

			DBConnect dbc = getDatabaseConnection();
			ResultSet result = dbc.executeQuery("select * from cis400db");
			while (result.next()) {
				int a = result.getInt("a");
				int b = result.getInt("b");
				int c = result.getInt("c");
				int d = result.getInt("d");
				int e = result.getInt("e");

				int held = result.getInt("held");
				int friend_held = result.getInt("friend_held");
				int foe1_held = result.getInt("foe1_held");
				int foe2_held = result.getInt("foe2_held");
				int action = result.getInt("action");

				pw.println(String.join(",",
						new String[] { Integer.toString(a), Integer.toString(b), Integer.toString(c),
								Integer.toString(d), Integer.toString(e), Integer.toString(held),
								Integer.toString(friend_held), Integer.toString(foe1_held), Integer.toString(foe2_held),
								Integer.toString(action) } + "\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.flush();
				pw.close();
			}
		}
	}

	public static void insertRow(String row) throws SQLException {
		String[] values = row.split(",");
		String query = "INSERT INTO game_data VALUES (" + String.join(", ", values) + ");";
		DBConnect dbc = getDatabaseConnection();
		dbc.executeUpdate(query);
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
