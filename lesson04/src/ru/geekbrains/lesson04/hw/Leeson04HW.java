package ru.geekbrains.lesson04.hw;

public class Leeson04HW {
    public static void main(String[] args) {
        Cooperator co1 = new Cooperator("Сквозник-Дмухановский Антон Антонович", 60, "+7-999-999-10-10", "директор", 900000);

        System.out.printf("ФИО:\t\t%s\nДолжность:\t%s\n", co1.getFio(), co1.getPost());
    }
}
