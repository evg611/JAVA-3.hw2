package ru.gb.chat.server;




import java.sql.Connection;
import java.sql.Statement;

import java.sql.*;


public class DbConnection {


    private Connection connection;
    private Statement stmt;

    public Statement getStmt() {
        return stmt;
    }

    public DbConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            this.stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Невозможно подключиться к базе данных");

        }
    }


    public void close() {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
