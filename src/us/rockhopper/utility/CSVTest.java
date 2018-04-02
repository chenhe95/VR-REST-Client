package us.rockhopper.utility;

import java.sql.SQLException;

public class CSVTest {
	public static void main(String[] args) {
		try {
			RDStoCSV.pullRowsIntoCSV("test_output.txt");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
