package com.columbia.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;


public class RDSManager {

	Connection connection;

	public RDSManager() {
		init();
	}

	private void init() {
		try {

			String hostname = "cloud2.ctdhsehllkaf.us-east-1.rds.amazonaws.com";
			String port = "3306";
			String userName = "ronan";
			String password = "ronan123";
            // 123123
			String dbName = "cloud2";
			// String driver = "com.mysql.jdbc.Driver";
			Class.forName("com.mysql.jdbc.Driver");
			String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/"
					+ dbName + "?user=" + userName + "&password=" + password;
			connection = DriverManager.getConnection(jdbcUrl);
			

		} catch (Exception e)

		{
			System.out
			.println("ERROR!!!!" + e.getMessage());
			e.printStackTrace();
		}

	}

	/**reads the video rating and displays it on myPlayer.jsp
	 * @param vName
	 * @return
	 */
	public float getObjectRating(String vName) {
		try {
			String queryToSelect = "select avgRating from ronanRating where vName='"
					+ vName + "'";
			float rating = 0;
			if (connection != null) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryToSelect);
				ResultSet result = preparedStatement.executeQuery();

				while (result.next()) {
					rating = result.getFloat(1);
				}

			}
			return rating;
		} catch (SQLException e) {
			System.out.println("ERROR!!!!!!!!!"
					+ e.getMessage());
			e.printStackTrace();
			return 1;
		}

	}

	/**
	 * @return list of videos from the rds db
	 */
	public LinkedList<String> getVideo() {
		try {
			
			String queryToSelectInOrder = "select DISTINCT vName from ronanRating order by avgRating desc";
			if (connection != null) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryToSelectInOrder);
				ResultSet resultVideoSet = preparedStatement.executeQuery();
				LinkedList<String> videos = new LinkedList<String>();

				while (resultVideoSet.next()) {
					videos.add(resultVideoSet.getString(1));
					

				}

				return videos;
			}
			return null;
		} catch (SQLException e) {
			System.out.println("ERROR!!!!!! "
					+ e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	/**inserts a new video entry into the table
	 * @param vName
	 * @param userRating
	 * @return
	 */
	public boolean setVideo(String vName, int userRating) {

		try {
			

			String queryToSelect = "select vID from ronanRating order by vID desc";
			if (connection != null) {
				PreparedStatement preparedStatement = connection.prepareStatement(queryToSelect);
				ResultSet result = preparedStatement.executeQuery();
				int vID = 1;
				if (result.next()) {
					vID = result.getInt(1) + 1;
				}

				
				java.util.Date date = new java.util.Date();
				Timestamp time = new Timestamp(date.getTime());
				int avgRating = userRating;
				int count = 0;
                
				String insertQuery = "insert into ronanRating (vID, vName, avgRating, time, count, sumRating ) values ("
						+ vID
						+ ", '"
						+ vName
						+ "', "
						+ avgRating
						+ ", '" + time + "', " + count + ", " + userRating + ")";
				
				PreparedStatement preparedStatement1 = connection.prepareStatement(insertQuery);
				preparedStatement1.execute(insertQuery);
				return true;
			}
			return false;
		}

		catch (Exception e) {
			System.out.println("ERROR!!!!!!! " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	/** Rates the video based on user input 
	 * @param vName
	 * @param userRating
	 * @return
	 */
	public boolean rateVideo(String vName, int userRating) {

		try {
			
			String queryToSelect = "select * from ronanRating where vName='"
					+ vName + "'";
			float avgRating;
			float count = 0;
			float totalRating = 0;

			if (connection != null) {
				PreparedStatement prepared_statement = connection.prepareStatement(queryToSelect);
				ResultSet result = prepared_statement.executeQuery();
				if (result.next()) {
					count = result.getInt(5) + 1;
					totalRating = result.getInt(6) + userRating;
					avgRating = (totalRating / count);

					String updateQuery = "update ronanRating SET avgRating ="
							+ avgRating
							+ ", count="
							+ (int) count
							+ ", sumRating="
							+ (int) totalRating
							+ " where vName='" + vName + "'";
					
					PreparedStatement preparedStatement1 = connection.prepareStatement(updateQuery);
					preparedStatement1.execute(updateQuery);
				}
				return true;
			}
			return false;
		}

		catch (Exception e) {
			System.out.println("ERROR!!!!!!!!! " + e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	/** deletes video from the database table
	 * @param vName
	 * @return
	 */
	public boolean deleteVideo(String vName) {
		try {

			String queryToDelete = "delete from ronanRating where vName='"
					+ vName + "'";
			
			if (connection != null) {
				PreparedStatement prepared_statement = connection.prepareStatement(queryToDelete);
				prepared_statement.executeUpdate();

				return true;
			}
			return false;
		}

		catch (Exception e) {
			System.out
			.println("ERROR!!!!!!!!! " + e.getMessage());
			e.printStackTrace();
			return false;

		}

	}

}
