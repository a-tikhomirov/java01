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
    public int getAge() {
        return age;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getPost() {
        return post;
    }
    public int getSalary() {
        return salary;
    }

    public String getCoopInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ФИО:\t\t" + fio + "\n");
        sb.append("Возраст:\t" + age + "\n");
        sb.append("Номер тел.:\t" + phoneNumber + "\n");
        sb.append("Должность:\t" + post + "\n");
        sb.append("Зарплата:\t" + salary + "\n");
        return sb.toString();
    }
}
