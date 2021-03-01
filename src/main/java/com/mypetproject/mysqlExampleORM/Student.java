package com.mypetproject.mysqlExampleORM;

@Table(name = "students")
public class Student {

    @Column(ai = true, primary = true)
    private int id;

    @Column(type = "VARCHAR", size = 24)
    private String name;

    @Column()
    private int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Student(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // not a best way
    public void setId(int id) {
        this.id = id;
    }
}
