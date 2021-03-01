    MY OWN SIMPLE ORM [Java 8, MySQL 5.0.8]

## ANNOTATION MAPPING
### @interface Table (String name default "default")
    @RetentionPolicy.RUNTIME
    @ElementType.FIELD
The @Table annotation can have an optional name argument which specifies the table name. If not specified, the class name with normalized case is used by default.
    
### @Column (String type default "int", int size default "11")
    @RetentionPolicy.RUNTIME
    @ElementType.FIELD
The @Column annotation can have an optional type and size arguments which specifies the column propetries. If not specified, the class name with normalized case is used by default.

## Main methods 
###### private static void createTable() throws SQLException 
###### static void insertData(Student student) throws SQLException 
###### private static void removeTable() throws SQLException
###### private static void clearTable() throws SQLException
###### static void getAllStudents()
###### private static void loadDriver() throws SQLException
#
###### private static void connect() throws SQLException
###### private static void disconnect()



## Out

    Loading driver...
    Driver loaded!
    Connecting database...
    Database successfully connected!
    Successful insert!
    Successful insert!
    Successful insert!

    id name age
    1 Lenya 21
    2 Vanya 20
    3 Dasha 19

    Database successfully disconnected!

    Process finished with exit code 0
