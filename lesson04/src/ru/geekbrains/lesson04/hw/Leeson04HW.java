package ru.geekbrains.lesson04.hw; // ...

public class Leeson04HW {
    public static void main(String[] args) {
        // 4. Вывести при помощи методов из пункта 3 ФИО и должность
        Cooperator co1 = new Cooperator("Сквозник-Дмухановский Антон Антонович", 60, "+7-999-999-10-10", "директор", 800000);
        System.out.printf("ФИО:\t\t%s\nДолжность:\t%s\n\n", co1.getFio(), co1.getPost());

        // 5. Создать массив из 5 сотрудников. С помощью цикла вывести информацию только о сотрудниках старше 40 лет;
        Cooperator[] coops = new Cooperator[5];
        coops[0] = co1;
        coops[1] = new Cooperator("Сквозник-Дмухановская Анна Андреевна", 55, "+7-999-111-11-11", "секретарь", 900000);
        coops[2] = new Cooperator("Ляпкин-Тяпкин Аммос Федорович", 45, "+7-998-250-20-00", "главный редактор", 600000);
        coops[3] = new Cooperator("Уховертов Степан Ильич", 35, "+7-998-002-02-02", "обозреватель", 300000);
        coops[4] = new Cooperator("Хлестаков Иван Александрович", 30, "+7-952-350-40-40", "журналист", 150000);

        printCoopInfo(coops, 40);
    }

    private static void printCoopInfo(Cooperator[] cooperators, int age) {
        System.out.printf("Сотрудники старше %d лет:\n", age);
        for (Cooperator coop : cooperators) {
            if (coop.getAge() > age) {
                System.out.println(coop.getCoopInfo());
            }
        }
    }
}
