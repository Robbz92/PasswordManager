package org.example.Database;

import org.example.Gui.WebSite;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private Connection conn = null;
    private String databaseURL = "jdbc:sqlite:pmanage.db";
    public void connect(){
        try {
            conn = DriverManager.getConnection(databaseURL);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void createDB(){
        connect();

        try{
            conn = DriverManager.getConnection(databaseURL);
            Statement statement = conn.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, url TEXT, username TEXT, password TEXT)";
            statement.executeUpdate(createTableQuery);

            Statement statement1 = conn.createStatement();
            String createSecondTable = "CREATE TABLE IF NOT EXISTS signIn (lUser TEXT, lPass TEXT)";
            statement1.executeUpdate(createSecondTable);

            statement.close();
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void registerUser(String username, String hash) throws Exception {
        connect();
        // TODO: check if user already exist. Dont override password.
        String storeLogin = "INSERT INTO signIn (lUser, lPass) VALUES(?, ?)";
        PreparedStatement statement = conn.prepareStatement(storeLogin);

        statement.setString(1, username);
        statement.setString(2, hash);

        statement.executeUpdate();
        statement.close();
        conn.close();
    }
    public boolean login(String userName, String providedHash) throws Exception {
        connect();

        String loginQuery = "SELECT * FROM signIn WHERE lUser = ?";
        PreparedStatement statement = conn.prepareStatement(loginQuery);
        statement.setString(1, userName);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String storedHash = resultSet.getString("lPass");

            if (storedHash.equals(providedHash)) {
                statement.close();
                conn.close();
                return true;
            }
        }

        return false;
    }
    public void storeWebsiteInformation(String url, String userName, String encrypted) throws Exception {
        connect();
        String query = "INSERT INTO user (url, username, password) VALUES(?, ?, ?)";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, url);
        statement.setString(2, userName);
        statement.setString(3, encrypted);

        statement.executeUpdate();
        statement.close();
        conn.close();
    }
    public void deleteWebsiteInformation(String url) throws Exception {
        connect();
        String query = "DELETE FROM user WHERE url = ?";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, url);

        statement.executeUpdate();
        statement.close();
        conn.close();
    }
    public ArrayList<String> getWebsiteInformation() throws Exception {
        connect();

        ArrayList<String> arrayList = new ArrayList<>();
        String query = "SELECT url FROM user";

        PreparedStatement statement = conn.prepareStatement(query);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            arrayList.add(resultSet.getString(1));
        }

        statement.close();
        conn.close();

        return arrayList;
    }
    public WebSite getWebsiteInformationOnURL(String url) throws Exception{
        connect();

        String userName = "";
        String encrypedPassword = "";
        String query = "SELECT * FROM user WHERE url = ?";

        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, url);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            userName = resultSet.getString(3);
            encrypedPassword = resultSet.getString(4);
        }

        return new WebSite(url, userName, encrypedPassword);
    }
}
