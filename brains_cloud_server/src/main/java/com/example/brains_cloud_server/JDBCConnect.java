package com.example.brains_cloud_server;

import javafx.scene.control.Alert;

import java.sql.*;

public class JDBCConnect implements AuthService {
    private static Connection connection;
    private static Statement stmt;
    private String login;
    private String pass;
    private String nick;

    public JDBCConnect(String login, String pass, int index) {
        try {
            connect();
            if (index == 1) {
                nick = getNickByLoginPass(login, pass);
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clients(\n" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "login TEXT,\n" +
                        "password TEXT,\n" +
                        "nick TEXT,\n" +
                        "free INTEGER \n" +
                        ");");
                stmt.executeUpdate(String.format("INSERT INTO clients (login,password,nick,free) VALUES ('%s','%s', '%s',1)", login, pass, nick));
                System.out.println("Подключился " + nick);
            } else {
                try (ResultSet rs = stmt.executeQuery("SELECT nick FROM clients WHERE login='" + login + "' AND password='" + pass + "';")) {

                    while (rs.next()) {
                        System.out.println("Подключился 0 " );
                        nick = rs.getString("nick");
                    }
                    System.out.println("Подключился 1 " + nick);
                } catch (SQLException e) {
                    System.out.println("Подключился 2 " + nick);
                }

                System.out.println("Подключился 3 " + nick);


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:clientsSQ.db");
        stmt = connection.createStatement();
    }

    @Override
    public void start() {
        System.out.println("Сервер аутентификации запущен");
    }

    @Override
    public String getNickByLoginPass(String login, String pass) throws SQLException {
        nick = "Client_" + login;
        return nick;
    }

    @Override
    public void stop() {
        System.out.println("Сервер аутентификации остановлен");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
