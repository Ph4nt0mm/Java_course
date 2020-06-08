package ru.hse.cs.java2020.task03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DataBase {
    private static Connection connection;
    private static Statement statement;

    public DataBase(String url, String userName, String userPassword) {
        try {
            connection = DriverManager.getConnection(url, userName, userPassword);
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Database login error:");
            e.printStackTrace();
        }
    }

    public DataBase() {

    }

    void stop() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataBaseResponse getInfo(String chatId) {
        try {
            DataBaseResponse result = null;
            ResultSet resultSet = statement.executeQuery("SELECT \"oath\", \"org_id\", "
                    + "\"statem\", \"tmp_info\" FROM public.\"javaDB\" WHERE \"chat_id\" = '" + chatId + "'");

            if (resultSet.next()) {
                result = new DataBaseResponse();
                result.setOathToken(resultSet.getString("oath"));
                result.setOrgId(resultSet.getString("org_id"));
                result.setState(resultSet.getString("statem"));
                result.setTmpInfo(resultSet.getString("tmp_info"));
                System.out.println("|");
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Something went wrong in request");
        }
        return null;
    }

    public void addNewChart(String chatId) {
        try {
            String req = "INSERT INTO public.\"javaDB\"(chat_id, statem) VALUES ('" + chatId + "', 'LOGINING')";
            statement.executeUpdate(req);
        } catch (SQLException e) {
            System.out.println("Cart wasnt added");
        }
    }

    public void updateChart(String chatId, String changeField, String changeData) throws SQLException {
        String req = "UPDATE public.\"javaDB\" SET " + changeField;
        if (changeData == "NULL") {
            req += "= NULL WHERE chat_id='" + chatId + "';";
            statement.executeUpdate(req);
        } else {
            System.out.println(changeField + " " + changeData + " " + chatId + " -");
            req += "='" + changeData + "' WHERE chat_id='" + chatId + "';";
            statement.executeUpdate(req);
        }
    }

    public void deleteChart(String chatId) throws SQLException {
        String req = "DELETE FROM public.\"javaDB\" WHERE chat_id = '" + chatId + "'";
        statement.executeUpdate(req);
    }
}
