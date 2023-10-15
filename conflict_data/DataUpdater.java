import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DataUpdater {

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("config.env"));

            String apiUrlIsrael = properties.getProperty("API_URL_ISRAEL");
            String apiUrlPalestine = properties.getProperty("API_URL_PALESTINE");
            String dbUrl = properties.getProperty("DB_URL");
            String dbUser = properties.getProperty("DB_USER");
            String dbPassword = properties.getProperty("DB_PASSWORD");

            String cleanedDataIsrael = fetchDataAndClean(apiUrlIsrael);
            String cleanedDataPalestine = fetchDataAndClean(apiUrlPalestine);

            updateDatabase(dbUrl, dbUser, dbPassword, "israel_data", cleanedDataIsrael);
            updateDatabase(dbUrl, dbUser, dbPassword, "palestine_data", cleanedDataPalestine);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String fetchDataAndClean(String apiUrl) throws IOException {
        StringBuilder apiResponse = new StringBuilder();
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                apiResponse.append(line);
            }
        }
        return cleanData(apiResponse.toString());
    }

    private static String cleanData(String rawData) {
        // Implement your data cleaning logic here
        return rawData; // Replace this with actual data cleaning
    }

    private static void updateDatabase(String dbUrl, String dbUser, String dbPassword, String tableName, String cleanedData)
            throws SQLException {
        try (Connection dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO " + tableName + " (event_data) VALUES (?)")) {
            preparedStatement.setString(1, cleanedData);
            preparedStatement.executeUpdate();
            System.out.println("Data inserted into " + tableName + " successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
