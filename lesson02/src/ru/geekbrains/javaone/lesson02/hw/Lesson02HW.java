package ru.geekbrains.javaone.lesson02.hw;

import java.util.Arrays;

/**
 * A program that contains Java 1 lesson 2 Home Work
 *
 * @author Andrey Tikhomirov
 * @version 1.0
 */
public class Lesson02HW {
    public static void main(String[] args) {
        int[] arr = {1, 1, 0, 0, 1, 0, 1, 1, 0, 0};
        System.out.println("Заданный массив:\n" + Arrays.toString(arr));
        System.out.println("Замена 0 на 1, 1 на 0:\n" + Arrays.toString(changeArr0to1(arr)) + "\n");

        arr = getRandomArray(10,0,1);
        System.out.println("Случайный массив:\n" + Arrays.toString(arr));
        System.out.println("Замена 0 на 1, 1 на 0:\n" + Arrays.toString(changeArr0to1(arr)) + "\n");

        int[] arrAsc = new int[8];
        System.out.println("Массив возрастающих значений:\n" + Arrays.toString(getAscArray(arrAsc)) + "\n");
    }

    /*
    *   Метод возвращает целочисленный массив случайных чисел в диапазоне от min до max длины len
    *
    *   @param  len Длина массива
    *   @param  min Минимальное возможное значение элемента массива
    *   @param  max Максимальное возможное значение элемента массива
     */
    private static int[] getRandomArray(int len, int min, int max){
        int[] arr = new int[len];
        max++;
        for (int i = 0; i < len; i++){
            arr[i] = (int) (Math.random() * max) + min;
        }
        return arr;
    }

    /*
    *   1. Метод, заменяющий в принятом массиве 0 на 1, 1 на 0;
    *
    *   @param  arr массив для обработки
     */
    private static int[] changeArr0to1(int[] arr){
        int len = arr.length;
        for (int i = 0; i < len; i++){
            switch (arr[i]){
                case 0:
                    arr[i] = 1;
                    break;
                case 1:
                    arr[i] = 0;
                    break;
                default:
                    System.out.println("Элемент массива номер " + (i + 1) + " не равен 0 или 1");
            }
        }
        return arr;
    }

    /*
    *   2. Метод, который с помощью цикла заполняет массив значениями 1 4 7 10 13 16 19 22;
    *
    *   @param  arr массив для обработки
     */
    private static int[] getAscArray(int[] arr){
        int len = arr.length;
        arr[0] = 1;
        for (int i = 1; i < len; i++){
            arr[i] = arr[i - 1] + 3;
        }
        return arr;
    }
}