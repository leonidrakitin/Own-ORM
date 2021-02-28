package com.mypetproject.mysqlExampleORM;

import java.lang.reflect.Field;
import java.sql.*;

public class MainApp {
    private static final String url = "jdbc:mysql://localhost/students";
    public static final String username = "root";
    public static final String password = "";

    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            connect();

            removeTable();
            createTable();
//            clearTable();

            insertStudent(new Student("Lenya", 21));
            insertStudent(new Student("Vanya", 20));
            insertStudent(new Student("Dasha", 19));

            getAllStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static void createTable() throws SQLException {
        Class<?> studentClass = Student.class;
        Field[] fields = studentClass.getDeclaredFields();

        String tableName = studentClass.getAnnotation(Table.class).name();
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

    public static void removeTable() throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXISTS students;");
    }

    public static void clearTable() throws SQLException {
        statement.executeUpdate("DELETE FROM students;");
    }

    public static void insertStudent(Student student) throws SQLException {
        if (!statement.execute("INSERT INTO students (name, age) VALUES ('"+student.getName()+"', "+student.getAge()+");")) {
            System.out.println("Successful insert!");
        } else {
            System.out.println("Error insert!");
        }
    }

    public static void getAllStudents() throws SQLException{
        try (ResultSet rs = statement.executeQuery("SELECT * FROM students")) {
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

    public static void disconnect() {
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
