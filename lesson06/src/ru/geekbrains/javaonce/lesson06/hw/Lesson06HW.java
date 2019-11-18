package ru.geekbrains.javaonce.lesson06.hw;

import java.io.*;
import java.nio.file.*;

public class Lesson06HW {

    /**
     * Метод склеивает побайтно два файла
     * @param file1 путь/имя первого файла для склеивания
     * @param file2 путь/имя второго файла для склеивания
     * @param combinedFile  путь/имя к создаваемому склеенному файлу
     * @return      true - склеивание было успешно
     *              false - произошла ошибка
     */
    private static boolean combineFiles(String file1, String file2, String combinedFile) {
        try {
            FileInputStream fin1 = new FileInputStream(file1);
            FileInputStream fin2 = new FileInputStream(file2);

            FileOutputStream fout = new FileOutputStream(combinedFile);

            readWrite(fin1, fout);
            readWrite(fin2, fout);

            fin1.close();
            fin2.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Метод побайтного переносит содержимое одного заданного потока в другой
     * @param readFrom  <code>FileInputStream</code> поток, из которого будет производится чтение
     * @param writeTo   <code>FileOutputStream</code> поток, в который будет производится
     *                  запись считанного из первого потока
     * @throws IOException  возникает в случае ошибки ввода/вывода
     */
    private static void readWrite(FileInputStream readFrom, FileOutputStream writeTo) throws IOException {
        int i;
        while ((i = readFrom.read())!= -1) {
            writeTo.write(i);
        }
    }

    /**
     * Метод проверяет наличие заданной строки в заданном файле
     * @param str   строка, нахождение которой в файле нужно проверить
     * @param file  путь/файл в котором нужно проверить наличие строки
     * @return      true - строка найдена в файле
     *              false - строка не найдена в файле
     */
    private static boolean isStrInFile(String str, String file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            return isByteArrInStream(str.getBytes(), fin);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Метод проверяет наличие заданного массива байт в заданном потоке
     * @param arr   массив байт для проверки
     * @param fin   <code>FileInputStream</code> поток в котором необходимо
     *              проверить наличичие заданного массива
     * @return      true - массив байт был найден
     *              false - массив байт не был найден
     * @throws IOException  возникает в случае ошибки ввода/вывода
     */
    private static boolean isByteArrInStream(byte[] arr, FileInputStream fin) throws IOException {
        long arrLen = arr.length;
        long fileLen = fin.getChannel().size();
        int rByte, arrByteInd = 0;
        boolean compare;
        if (arrLen < fileLen) { // если число байт массива превышет длину файла, можно не сравнивать)
            rByte = fin.read();
            while (fileLen-- >= arrLen) {
                if (rByte == arr[0]) {  // сделано именно в таком порядке,чтобы сохранить последний считанный
                                        // байт, в случае его несовпадения с байтом из массива в цикле while ниже
                    compare = true;
                    while (arrByteInd++ != arrLen - 1) {
                        rByte = fin.read();
                        --fileLen;
                        compare &= arr[arrByteInd] == rByte;
                        if (!compare) {
                            ++fileLen;  // если байт не совпал, он сохраняется на следующий цикл, счетчик не уменьшаем
                            break;
                        }
                    }
                    if (compare) return true;
                    arrByteInd = 0;
                } else
                    rByte = fin.read();
            }
        }
        return false;
    }

    /**
     * Метод проверяет наличие заданной строки во всех файлах (исключая директории) заданной папки
     * @param str       строка, нахождение которой в файлах нужно проверить
     * @param directory путь к директории, файлы которой нужно проверить на вхождение строки
     * @return          Возвращает строку, содержащую результаты проверки вхождения заданной строки
     */
    private static String checkStrInDir(String str, String directory) {
        if (directory.isEmpty())
            directory = ".";

        File dir = new File(directory);
        File[] files = dir.listFiles();

        StringBuilder sb = new StringBuilder();
        try {
            for (File file: files) {
                if (!file.isDirectory()) {
                    sb.append(file.getPath() + " string \"");
                    sb.append(str + "\" ");
                    sb.append((isStrInFile(str, file.getPath())) ? "founded" : "-");
                    sb.append("\n");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            sb.append("no such dir...\n");
        }
        return sb.toString();
    }

    //1 вариант: используем класс файл
    // сразу ищет слово при прочтении файла
    private static boolean readLine (String fileName, String word) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName))).contains(word);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //склейка двух файлов при прочтении второго, сразу записывая символы в конец первого
    //при желании как файл1 можно указать пустой файл и вызвать метод 2 раза, передавая
    //как файл2 другие 2 файла в нужной последовательности
    //склеится по сути также
    private static void splice (String fileA, String fileB) {
        try (FileOutputStream fos = new FileOutputStream(fileA, true); FileInputStream fin = new FileInputStream(fileB)){
            int i;
            while ((i = fin.read()) != -1) {
                fos.write(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String file1 = "mntZdrives";
        String file2 = "switchOutput";
        String str = "bash";
        String dir = ".";

        StringBuilder sb = new StringBuilder();
        sb.append(file1);
        sb.append("_");
        sb.append(file2);
        // склеивание двух файлов
        //System.out.println(combineFiles(file1, file2, sb.toString()) ? "Files combined\n" : "something wrong\n");

        long startTime = System.nanoTime();
        // поиск строки в файле
        System.out.printf("String \"%s\" in file \"%s\" founded - %s\n\n", str, file1, isStrInFile(str, file1));
        float delta = (System.nanoTime() - startTime) * 0.000000001f;
        System.out.printf("%f sec\n\n", delta);

        startTime = System.nanoTime();
        // поиск строки в файле
        System.out.printf("String \"%s\" in file \"%s\" founded - %s\n\n", str, file1, readLine(file1, str));
        delta = (System.nanoTime() - startTime) * 0.000000001f;
        System.out.printf("%f sec\n\n", delta);

        // поиск строки в папке
        System.out.printf("Results of checking String \"%s\" in directory \"%s\" are:\n%s", str, dir, checkStrInDir(str, dir));
    }
}
