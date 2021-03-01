package com.mypetproject.mysqlExampleORM;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Processor {
    private static final String url = "jdbc:mysql://localhost/students";
    private static final String username = "root";
    private static final String password = "";

    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            connect();

//            removeTable(Student.class);
//            createTable(Student.class);
            clearTable(Student.class);
//
//            insertData();
//            insertData(new Student("Vanya", 20));
//            insertData(new Student("Dasha", 19));

            List<Student> list = new ArrayList<>(Arrays.asList(
                    new Student("Misha", 17),
                    new Student("Dima", 16),
                    new Student("Petya", 25),
                    new Student("Lenya", 21),
                    new Student("Dasha", 19),
                    new Student("Vanya", 20)
            ));

            for (Student st : list) {
                insertData(Student.class, st.getName(), st.getAge());
            }

            printAllData(Student.class);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void createTable(Class c) throws SQLException {
        if (!c.isAnnotationPresent(Table.class)) {
            throw new SQLException("Missed @Table");
        }

        StringBuffer query = new StringBuffer("CREATE TABLE IF NOT EXISTS ");
        query.append(((Table)c.getAnnotation(Table.class)).name());

        Field[] fields = c.getDeclaredFields();

        int count = 0;
        for (Field o : fields) {
            if (o.isAnnotationPresent(Column.class)) {
                count++;
            }
        }

        if (count == 0) {
            query.append(";");
            statement.execute(query.toString());
            return;
        }

        query.append(" (");
        String primary = null;

        for (Field o : fields) {
            if (o.isAnnotationPresent(Column.class)) {
                query.append("`")
                        .append(o.getName())
                        .append("` ")
                        .append(o.getAnnotation(Column.class).type())
                        .append(" (")
                        .append(o.getAnnotation(Column.class).size())
                        .append(") NOT NULL");

                if (o.getAnnotation(Column.class).ai()) {
                    query.append(" AUTO_INCREMENT");
                }

                query.append(", ");

                if (o.getAnnotation(Column.class).primary()) {
                    primary = ", PRIMARY KEY (`"+o.getName()+"`)";
                }
            }
        }

        query.setLength(query.length()-2); // removing last ", " characters

        if (primary != null) {
            query.append(primary);
        }

        query.append(");");

        statement.execute(query.toString());
    }

    private static void removeTable(Class c) throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXISTS "+((Table)c.getAnnotation(Table.class)).name()+";");
    }

    private static void clearTable(Class c) throws SQLException {
        statement.executeUpdate("DELETE FROM "+((Table)c.getAnnotation(Table.class)).name()+";");
    }

    static void insertData(Class c, Object... params) throws SQLException {
        Field[] fields = c.getDeclaredFields();

        int count = 0;
        for (Field o : fields) {
            if (o.isAnnotationPresent(Column.class)) {
                count++;
            }
        }

        if (count == 0) {
            throw new SQLException("Invalid table!");
        }

        StringBuffer values = new StringBuffer();
        StringBuffer query = new StringBuffer("INSERT INTO ");
        query.append(((Table)c.getAnnotation(Table.class)).name() + " (");

        for (Field o : fields) {
            if (o.isAnnotationPresent(Column.class) && !o.getAnnotation(Column.class).ai()) {
                query.append(o.getName()).append(", ");
                values.append("?, ");
            }
        }

        values.setLength(values.length()-2);
        values.append(");");

        query.setLength(query.length()-2);
        query.append(") VALUES (").append(values);

        PreparedStatement ps = connection.prepareStatement(query.toString());

        for (int i = 0, j = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(Column.class) && !fields[i].getAnnotation(Column.class).ai()) {
                try {
                    ps.setObject(i, params[j]);
                    j++;
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }

        ps.executeUpdate();
    }

//    static List<Object> loadData(Class c) {
//        Field[] fields = c.getDeclaredFields();
//        List<Object> list = new ArrayList<>();
//
//        try (ResultSet rs = statement.executeQuery("SELECT * FROM "+((Table)c.getAnnotation(Table.class)).name()+";")) {
//            while (rs.next()) {
//                for (Field o : fields) {
//                    Object l = c.getDeclaredConstructor(int i)
//                }
////                list.add(c.getConstructor(int.class).newInstance()));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
////        catch (IllegalAccessException e) {
////            e.printStackTrace();
////        } catch (InstantiationException e) {
////            e.printStackTrace();
////        }
//        return list;
//    }

    static void printAllData(Class c) {
        try (ResultSet rs = statement.executeQuery("SELECT * FROM "+((Table)c.getAnnotation(Table.class)).name()+";")) {
            System.out.println("\nid name age");
            while (rs.next()) {
                System.out.println(rs.getInt("id")+" "+rs.getString("name")+" "+rs.getInt("age"));;
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

    private static void connect() throws SQLException {
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
