package ru.geekbrains.lesson04.hw;
// ...
public class Leeson04HW {
    public static void main(String[] args) {
        // 4. Вывести при помощи методов класса ФИО и должность
        Cooperator co1 = new Cooperator("Дмухановский Антон Антонович", 60, "+7-999-999-10-10", "директор", 180000);
        System.out.printf("ФИО:\t\t%s\nДолжность:\t%s\n\n", co1.getFio(), co1.getPost());

        // 5. Создать массив из 5 сотрудников. С помощью цикла вывести информацию только о сотрудниках старше 40 лет;
        Cooperator[] coops = new Cooperator[5];
        coops[0] = co1;
        coops[1] = new Cooperator("Дмухановская Анна Андреевна", 55, "+7-999-111-11-11", "секретарь", 190000);
        coops[2] = new Cooperator("Ляпкин-Тяпкин Аммос Федорович", 45, "+7-998-250-20-00", "главный редактор", 160000);
        coops[3] = new Cooperator("Уховертов Степан Ильич", 35, "+7-998-002-02-02", "обозреватель", 130000);
        coops[4] = new Cooperator("Хлестаков Иван Александрович", 30, "+7-952-350-40-40", "журналист", 100000);

        printCoopInfo(coops, 40);

        //6. * Создать метод, повышающий зарплату всем сотрудникам старше 45 лет на 5000.
        moreSalary(coops, 45, 5000);
        moreSalary(coops, 25, 50, 10000);
    }

    /**
     * Отображние информации о сотрудниках возрастом
     * старше <code>age</code>
     *
     * @param cooperators   массив экземпляров класса <code>Cooperator</code>
     * @param age           планка возраста сотрудника
     */
    private static void printCoopInfo(Cooperator[] cooperators, int age) {
        System.out.printf("Сотрудники старше %d лет:\n", age);
        for (Cooperator coop : cooperators) {
            if (coop.getAge() > age) {
                System.out.println(coop.getCoopInfo());
            }
        }
    }

    /**
     * Повышение зарплаты сотрудникам,
     * чей возраст больше <code>age</code>
     * посредством метода <code>setSalary()</code> класса <code>Cooperator</code>
     *
     * @param cooperators   массив экземпляров класса <code>Cooperator</code>
     * @param age           планка возраста сотрудника для повышения ЗП
     * @param salaryDelta   сумма повышения ЗП
     */
    private static void moreSalary(Cooperator[] cooperators, int age, int salaryDelta) {
        System.out.printf("Повышение запрплаты на %d сотрудникам, чей возраст больше %d:\n", salaryDelta, age);
        for (Cooperator coop : cooperators) {
            if (coop.getAge() > age) {
                coop.setSalary(coop.getSalary() + salaryDelta);
                System.out.println(coop.getCoopInfo());
            }
        }
    }

    /**
     * Повышение зарплаты сотрудникам, чей возраст
     * больше <code>ageStart</code> и меньше <code>ageFinish</code>
     * посредством метода <code>moreSalary()</code>
     * класса <code>Cooperator</code>
     *
     * @param cooperators   массив экземпляров класса <code>Cooperator</code>
     * @param ageStart      планка возраста сотрудника для повышения ЗП
     * @param ageFinish     планка возраста сотрудника для повышения ЗП
     * @param salaryDelta   сумма повышения ЗП
     */
    private static void moreSalary(Cooperator[] cooperators, int ageStart, int ageFinish, int salaryDelta) {
        if (ageFinish <= ageStart) {
            System.out.println("Значения возраста для повышения должны быть перданы в порядке повышения и не должны быть равны");
            return;
        }
        System.out.printf("Повышение запрплаты на %d сотрудникам, чей возраст больше %d и меньще %d\n", salaryDelta, ageStart, ageFinish);
        for (Cooperator coop : cooperators) {
            if (coop.isAgeBetween(ageStart, ageFinish)) {
                coop.moreSalary(salaryDelta);
                System.out.println(coop.getCoopInfo());
            }
        }
    }
}
