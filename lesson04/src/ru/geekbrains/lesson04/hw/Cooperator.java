package ru.geekbrains.lesson04.hw;

public class Cooperator {
    private String fio;
    private int age;
    private String phoneNumber;
    private String post;
    private int salary;

    Cooperator(String fio, int age, String phoneNumber, String post, int salary) {
        this.fio = fio;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.post = post;
        this.salary = salary;
    }

    public String getFio() {
        return fio;
    }
    private int getAge() {
        return age;
    }
    private String getPhoneNumber() {
        return phoneNumber;
    }
    private String getPost() {
        return post;
    }
    private int getSalary() {
        return salary;
    }
    
}
