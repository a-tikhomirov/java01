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
}
