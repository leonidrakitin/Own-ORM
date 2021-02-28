package com.mypetproject.mysqlExampleORM;

import java.lang.reflect.Field;
import java.sql.*;

public class MainApp {
    private static final String url = "jdbc:mysql://localhost/students";
    private static final String username = "root";
    private static final String password = "";

    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            connect();

            removeTable();
            createTable();
//            clearTable();

            insertData(new Student("Lenya", 21));
            insertData(new Student("Vanya", 20));
            insertData(new Student("Dasha", 19));

            getAllStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void createTable() throws SQLException {
        String tableName = Student.class.getAnnotation(Table.class).name();

        Field[] fields = Student.class.getDeclaredFields();
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";

        for (Field o : fields) {
            if (o.isAnnotationPresent(Column.class)) {
                String type = o.getAnnotation(Column.class).type();
                int size = o.getAnnotation(Column.class).size();

                query += "`" + o.getName() + "` " + type + " " + "(" + size + ")" + " NOT NULL" + (o.getName() == "id" ? " AUTO_INCREMENT" : "") + ",";
            }
        }
        query += "PRIMARY KEY (`id`));";
        statement.execute(query);
    }

    private static void removeTable() throws SQLException {
        String tableName = Student.class.getAnnotation(Table.class).name();
        statement.executeUpdate("DROP TABLE IF EXISTS "+tableName+";");
    }

    private static void clearTable() throws SQLException {
        String tableName = Student.class.getAnnotation(Table.class).name();
        statement.executeUpdate("DELETE FROM "+tableName+";");
    }

    static void insertData(Student student) throws SQLException {
        String tableName = Student.class.getAnnotation(Table.class).name();

        if (!statement.execute("INSERT INTO "+tableName+" (name, age) VALUES ('"+student.getName()+"', "+student.getAge()+");")) {
            System.out.println("Successful insert!");
        } else {
            System.out.println("Error insert!");
        }
    }

    static void getAllStudents() {
        String tableName = Student.class.getAnnotation(Table.class).name();

        try (ResultSet rs = statement.executeQuery("SELECT * FROM "+tableName+";")) {
            System.out.println("\nid name age");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " " + rs.getString("name") + " " + rs.getInt("age"));
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadDriver() throws SQLException {
        System.out.println("Loading driver...");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Cannot find the driver in the classpath!", e);
        }
    }

    private static void connect() throws SQLException{
        loadDriver();

        System.out.println("Connecting database...");

        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            System.out.println("Database successfully connected!");
        } catch (SQLException e) {
            throw new SQLException("Cannot connect the database!", e);
        }
    }

    private static void disconnect() {
        if (statement != null) {
            try {
                statement.close();
                statement = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Database successfully disconnected!");
            }
        }
    }
}
