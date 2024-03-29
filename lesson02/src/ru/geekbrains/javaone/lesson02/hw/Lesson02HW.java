package ru.geekbrains.javaone.lesson02.hw;

import java.util.Arrays;    //для преобразования массива в строку

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
        changeArr0to1(arr);
        System.out.println("Замена 0 на 1, 1 на 0:\n" + Arrays.toString(arr) + "\n");

        arr = getRandomArray(10,0,1);
        System.out.println("Сгенерированный массив:\n" + Arrays.toString(arr));
        changeArr0to1(arr);
        System.out.println("Замена 0 на 1, 1 на 0:\n" + Arrays.toString(arr) + "\n");

        int[] arrAsc = new int[8];
        getAscArray(arrAsc, 1, 3);
        System.out.println("Массив возрастающих значений:\n" + Arrays.toString(arrAsc) + "\n");

        int[] arrToMul = {1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1};
        System.out.println("Заданный массив:\n" + Arrays.toString(arrToMul));
        changeArrMul(arrToMul);
        System.out.println("Числа меньше 6 умножены на 2:\n" + Arrays.toString(arrToMul) + "\n");

        arrToMul = getRandomArray(12,1,15);
        System.out.println("Сгенерированный массив:\n" + Arrays.toString(arrToMul));
        changeArrMul(arrToMul);
        System.out.println("Числа меньше 6 умножены на 2:\n" + Arrays.toString(arrToMul) + "\n");

        arrToMul = getRandomArray(12,1,20);
        System.out.println("Сгенерированный массив:\n" + Arrays.toString(arrToMul));
        System.out.println("Минимальный элемент массива: " + getMinFromArr(arrToMul));
        System.out.println("Максимальный элемент массива: " + getMaxFromArr(arrToMul) + "\n");

        int[][] sqrArr = getRandomSqrArray(5,0,0);
        System.out.println("Сгенерированный квадратный массив:\n" + toString(sqrArr));
        fillSqrArrWithOne(sqrArr);
        System.out.println("Квадратный массив заполненный единицами по диагонали:\n" + toString(sqrArr) + "\n");

        int[] arrBalanced = {10, 1, 2, 3, 4};
        System.out.println("Заданный массив:\n" + Arrays.toString(arrBalanced));
        System.out.println("Заданный массив " + (checkBalance(arrBalanced) ? "сбалансированный" : "несбалансированный") + "\n");

        int[] arrBalanced2 = {1, 1, 1, 2, 1};
        System.out.println("Заданный массив:\n" + Arrays.toString(arrBalanced2));
        System.out.println("Заданный массив " + (checkBalance(arrBalanced2) ? "сбалансированный" : "несбалансированный") + "\n");

        /*
        *   Примеры сгенерированных сбалансированных массивов:
        *   [14, 5, 5, 2, 14, 15 || 8, 4, 8, 15, 5, 15] 55 || 55
        *   [14, 7, 12, 6, 2 || 11, 10, 1, 3, 3, 2, 11] 41 || 41
        *   [5, 14, 2, 4, 1, 15 || 8, 2, 10, 1, 7, 13] 41 || 41
         */
        arrToMul = getRandomArray(12,1,15);
        System.out.println("Сгенерированный массив:\n" + Arrays.toString(arrToMul));
        System.out.println("Заданный массив " + (checkBalance(arrToMul) ? "сбалансированный" : "несбалансированный") + "\n");

        arr = getRandomArray(10,1,10);
        System.out.println("Сгенерированный массив:\n" + Arrays.toString(arr));
        int n = -3;
        shiftArrWithTmp(arr, n);
        System.out.println("Массив со сдвигом на " + n + ":\n" + Arrays.toString(arr) + "\n");

        arr = getRandomArray(10,1,10);
        System.out.println("Сгенерированный массив:\n" + Arrays.toString(arr));
        n = 11;
        shiftArr(arr, n);
        System.out.println("Массив со сдвигом на " + n + ":\n" + Arrays.toString(arr));
    }

    /*
    *   Метод возвращает целочисленный массив
    *   случайных чисел в диапазоне от min до max длины len
    *
    *   @param  len Длина массива
    *   @param  min Минимальное возможное значение элемента массива
    *   @param  max Максимальное возможное значение элемента массива
     */
    private static int[] getRandomArray(int len, int min, int max){
        int[] arr = new int[len];
        max = max - min + 1;
        for (int i = 0; i < len; i++){
            arr[i] = (int) (Math.random() * max) + min;
        }
        return arr;
    }

    /*
    *   Метод возвращает целочисленный квадратный массив
    *   случайных чисел в диапазоне от min до max размерностью len
    *
    *   @param  len Размерность массива
    *   @param  min Минимальное возможное значение элемента массива
    *   @param  max Максимальное возможное значение элемента массива
     */
    private static int[][] getRandomSqrArray(int len, int min, int max){
        int[][] arrSqr = new int[len][len];
        for (int i = 0; i < len; i++){
            arrSqr[i] = getRandomArray(len, min, max);
        }
        return arrSqr;
    }

    /*
    *   Метод возвращает двумерный целочисленный массив в виде строки
    *
    *   @param arr  Двумерный целочисленный массив
     */
    private static String toString(int[][] arr){
        if (arr == null)
            return "null";
        int iMax = arr.length - 1;
        if (iMax == -1)
            return "[]";
        int jMax;

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append('[');
            jMax = arr[i].length - 1;
            if (jMax == -1)
                b.append("]\n");
            else
                for (int j = 0; ; j++){
                    b.append(arr[i][j]);
                    if (j == jMax){
                        b.append("]");
                        break;
                    }
                    b.append(",  \t");
                }
            if (i == iMax)
                return b.toString();
            b.append("\n");
        }
    }

    /*
    *   1. Метод, заменяющий в принятом массиве 0 на 1, 1 на 0;
    *
    *   @param  arr одномерный целочисленный массив
     */
    private static void changeArr0to1(int[] arr){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        for (int i = 0; i < arr.length; i++){
            //arr[i] = (arr[i] == 1) ? 0 : 1;
            //arr[i] = 1 - arr[i];
            arr[i] ^= 1;
            /*
            switch (arr[i]){
                case 0:
                    arr[i] = 1;
                    break;
                case 1:
                    arr[i] = 0;
                    break;
                default:
                    System.out.println("Элемент массива номер " + (i + 1) + " не равен 0 или 1");
            }*/
        }
    }

    /*
    *   2. Метод, который с помощью цикла
    *   заполняет массив значениями 1 4 7 10 13 16 19 22;
    *
    *   @param  arr одномерный целочисленный массив
    *   @param  shift   сдвиг прогрессии
    *   @param  step    шаг прогрессии
     */
    private static void getAscArray(int[] arr, int shift, int step){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i * step + shift;
        }

        /*arr[0] = 1;
        for (int i = 1; i < len; i++){
            arr[i] = arr[i - 1] + 3;
        }*/
    }

    /*
    *   3. Метод, принимающий на вход массив и умножающий числа меньше 6 на 2;
    *
    *   @param  arr одномерный целочисленный массив
     */
    private static void changeArrMul(int[] arr){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        for (int i = 0; i < arr.length; i++){
            if (arr[i] < 6) {
                arr[i] *= 2;
            }
        }
    }

    /*
    *   4.1 Метод возвращает минимальное значение одномерного массива
    *
    *   @param  arr одномерный целочисленный массив
     */
    private static int getMinFromArr(int[] arr){
        int min = arr[0];
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        for (int i = 1; i < arr.length; i++){
            if (arr[i] < min){
                min = arr[i];
            }
        }
        return min;
    }

    /*
    *   4.2 Метод возвращает максимальное значение одномерного массива
    *
    *   @param  arr одномерный целочисленный массив
     */
    private static int getMaxFromArr(int[] arr) {
        int max = arr[0];
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    /*
    *   5. Метод, который заполняет диагональные
    *   элементы квадратного целочисленного массива единицами
    *
    *   @param  arr квадратный целочисленный массив
     */
    private static void fillSqrArrWithOne(int[][] arr){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        for (int i = 0; i < arr.length; i++){
            arr[i][i] = 1;
            arr[arr.length - i - 1][i] = 1; // arr[i][arr.length - i - 1] = 1;
        }
    }

    /*
    *   6. Метод возвращает true если в массиве есть место,
    *   в котором сумма левой и правой части массива равны.
    *
    *   @param  arr одномерный ненулевой целочисленный массив для проверки
     */
    private static boolean checkBalance(int[] arr){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        int leftSum, rightSum;
        leftSum = arr[0];
        rightSum = getArrSum(arr, 1, (arr.length - 1));
        for (int i = 1; i < arr.length; i++){
            //leftSum = getArrSum(arr, 0, i);
            //rightSum = getArrSum(arr, i, (len - i));
            if (leftSum == rightSum){
                System.out.println("Balance point = " + i + ";\nBalance summ = " + leftSum + ";");
                return true;
            }
            leftSum += arr[i];
            rightSum -= arr[i];
        }
        return false;
    }

    /*
    *   Метод возвращает сумму заданного числа элементов
    *   одномерного целочисленного массива начиная с заданной позиции
    *
    *   @param  arr целочисленный одномерный массив
    *   @param  ind индекс элемента, начиная с которого будет считаться сумма
    *   @param  count   число элементов для суммирования
     */
    private static int getArrSum(int[] arr, int ind, int count){
        if (arr == null){
            return 0;
        }
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        if (ind + count > arr.length || ind > arr.length || ind < 0 || count < 0){
            System.out.println("Заданный индекс или число элементов суммирования выходят за границы массива или отрицательны");
            return 0;
        }
        int sum = 0;
        for (int i = ind; i < (ind + count); i++){
            sum += arr[i];
        }
        return sum;
    }

    /*
    *   7. Метод циклически смещает все элементы заданного массива
    *   на n позиций с использованием вспомогательного массива
    *
    *   @param  arr целочисленный одномерный массив
    *   @param  n   число на которое будут сдвинуты элементы массива
     */
    private static void shiftArrWithTmp(int[] arr, int n){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        int[] tmpArr = new int[ arr.length];
        for (int i = 0; i <  arr.length; i++){
            if (n < 0){
                tmpArr[i] = arr[(i - n) %  arr.length];
            } else {
                tmpArr[i] = arr[( arr.length - n + i) %  arr.length];
            }
        }
        for (int i = 0; i <  arr.length; i++)
            arr[i] = tmpArr[i];
    }

    /*
    *   8. Метод циклически смещает все элементы заданного массива
    *   на n позиций без использования вспомогательного массива
    *
    *   @param  arr целочисленный одномерный массив
    *   @param  shiftValue   число на которое будут сдвинуты элементы массива
     */
    private static void shiftArr(int[] arr, int shiftValue){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        int shiftCount = Math.abs(shiftValue) % arr.length;
        for (int i = 0; i < shiftCount; i++) shiftOnce(arr, (shiftValue < 0));
    }

    /*
     *   Метод осуществляет циклический сдвиг элементов
     *   массива на 1 элемент влево или вправо
     *
     *   @param  arr целочисленный одномерный массив
     *   @param  left    если true - сдвиг влево, если false - вправо
     */
    private static void shiftOnce(int[] arr, boolean left){
        if (left){
            shiftLeft(arr);
        } else {
            shiftRight(arr);
        }
    }

    private static void shiftLeft(int[] arr) {
        int lastIndex = arr.length - 1;
        int tmp = arr[0];
        for (int i = 0; i < (lastIndex - 1); i++) arr[i] = arr[i + 1];
        arr[lastIndex - 1] = tmp;
    }

    private static void shiftRight(int[] arr) {
        int lastIndex = arr.length - 1;
        int tmp = arr[lastIndex];
        for (int i = lastIndex; i > 0; i--) arr[i] = arr[i - 1];
        arr[0] = tmp;
    }

    /*
    *   Метод осуществляет циклический сдвиг элементов
    *   массива на 1 элемент влево или вправо
    *
    *   @param  arr целочисленный одномерный массив
    *   @param  left    если true - сдвиг влево, если false - вправо
     */
    private static void shiftOnceV1(int[] arr, boolean left){
        //int len = arr.length; //простое поле, не вычисляется - можно не выделять отдельную переменную
        int lastIndex = arr.length - 1;
        int tmp = (left) ? arr[0] : arr[lastIndex];
        for (int i = 0; i < lastIndex; i++){
            if (left) {
                arr[i] = arr[i + 1];
            } else {
                arr[lastIndex - i] = arr[lastIndex - i - 1];
            }
        }
        if (left) {
            arr[lastIndex] = tmp;
        } else {
            arr[0] = tmp;
        }
        /*
        if (left){
            tmp = arr[0];
            for (int i = 0; i < (len - 1); i++) arr[i] = arr[i + 1];
            arr[len - 1] = tmp;
        } else {
            tmp = arr[len - 1];
            for (int i = (len - 1); i > 0; i--) arr[i] = arr[i - 1];
            arr[0] = tmp;
        }*/
    }

}
