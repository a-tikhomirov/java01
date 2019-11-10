package ru.geekbrains.javaone.lesson04.hw;

public class Cooperator {
    private static int idCounter = 1;

    private int id;
    private String fio;
    private int age;
    private String phoneNumber;
    private String post;
    private int salary;

    // 7. Продумать конструктор таким образом, чтобы при создании каждому сотруднику присваивался личный уникальный идентификационный порядковый номер
    private Cooperator() {
        id = idCounter;
        ++idCounter;
    }

    Cooperator(String fio, int age, String phoneNumber, String post, int salary) {
        this();
        this.fio = fio;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.post = post;
        this.salary = salary;
    }

    public int gedId() {
        return id;
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
    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getCoopInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("id:\t\t\t" + id + "\n");
        sb.append("ФИО:\t\t" + fio + "\n");
        sb.append("Возраст:\t" + age + "\n");
        sb.append("Номер тел.:\t" + phoneNumber + "\n");
        sb.append("Должность:\t" + post + "\n");
        sb.append("Зарплата:\t" + salary + "\n");
        return sb.toString();
    }

    public boolean isAgeBetween(int age1, int age2) {
        return age > age1 && age < age2;
    }

    public void moreSalary(int delta) {
        this.salary += delta;
    }
}
