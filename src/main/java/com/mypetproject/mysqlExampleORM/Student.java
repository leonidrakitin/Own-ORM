package com.mypetproject.mysqlExampleORM;

@Table(name = "students")
public class Student {

    @Column()
    private int id;

    @Column(type = "VARCHAR", size = 24)
    private String name;

    @Column()
    private int age;

    public Student(String name, int age) {
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
}
