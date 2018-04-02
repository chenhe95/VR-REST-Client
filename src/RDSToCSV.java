import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class RDSToCSV {
	public static String[] loadSettings() {
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

	public static void pullRowsIntoCSV() throws SQLException {
		String[] settings = loadSettings();
		DBConnect dbc = new DBConnect(settings[0], settings[1], Integer.parseInt(settings[2]), settings[3], settings[4],
				settings[5]);
		dbc.executeQuery("select * from cis400db");
	}
}
