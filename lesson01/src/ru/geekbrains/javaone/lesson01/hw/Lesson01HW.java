package ru.geekbrains.javaone.lesson01.hw;
/**
 * A program that contains Java 1 lesson 1 Home Work
 *
 * @author Andrey Tikhomirov
 * @version 1.0
 */
public class Lesson01HW {

    //1. Создать пустой проект в IntelliJ IDEA и прописать метод main();
    public static void main(String[] args) {

    }

    //2. Создать переменные всех пройденных типов данных, и инициализировать их значения;
    private static void variables(){
        byte b = 120;       //от -128 до 127
        short s = 32000;    //от -32768 до 32767
        int i = 2147483000; //от -2147483648 до 2147483647
        long l = 400000L;   //от -9223372036854775808 до 9223372036854775807
        float f = 28.28f;
        double d = 128.128;
        char c = 'A';       //от '\u0000' или 0 до '\uffff' или 65535
        boolean bool = true;
        String str = "Hello, World!";
        System.out.println("byte b = " + b + ";\nshort s = " + s + ";\nint i = " + i + ";\nlong l = " + l + ";\nfloat f = " + f + ";\ndouble d = " + d + ";\nchar c = " + c + ";\nboolean bool = " + bool + ";\nString str = " + str);
    }

    //3. Написать метод вычисляющий выражение a * (b + (c / d)) и возвращающий результат,где a, b, c, d – входные параметры этого метода;
    private static int someMath(int a, int b, int c, int d){
        if (d == 0){
            System.out.println("Параметр d не должен быть равен нулю");
            return -1;
        }
        return a * (b + (c / d));
    }

    private static float someMath(float a, float b, float c, float d){
        if (d == 0){
            System.out.println("Параметр d не должен быть равен нулю");
            return -1;
        }
        return a * (b + (c / d));
    }

    //4. Написать метод, принимающий на вход два числа, и проверяющий что их сумма лежит в пределах от 10 до 20(включительно), если да – вернуть true, в противном случае – false;
    private static boolean sumBetween10and20(int a, int b){
        return (a + b >= 10) && (a + b <= 20);
    }

    private static boolean sumBetween10and20(float a, float b){
        return (a + b >= 10) && (a + b <= 20);
    }

    //5. Написать метод, которому в качестве параметра передается целое число, метод должен напечатать в консоль положительное ли число передали, или отрицательное; Замечание: ноль считаем положительным числом.
    private static void printSign(int a){
        System.out.println("Переданноее число является " + ((a >= 0) ? "положительным" : "отрицательным"));
    }
}
