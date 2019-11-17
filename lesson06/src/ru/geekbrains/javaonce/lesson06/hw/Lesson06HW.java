package ru.geekbrains.javaonce.lesson06.hw;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

public class Lesson06HW {

    /**
     * Методо склеивает побайтно двай файла
     * @param file1 путь/имя первого файла для склеивания
     * @param file2 путь/имя второго файла для склеивания
     * @return      0 - склеивание было успешно
     *              -1 - произошла ошибка
     */
    private static int combineFiles(String file1, String file2) {
        try {
            FileInputStream fin1 = new FileInputStream(file1);
            FileInputStream fin2 = new FileInputStream(file2);

            StringBuilder sbCombinedFile = new StringBuilder();
            sbCombinedFile.append(file1);
            sbCombinedFile.append("_");
            sbCombinedFile.append(file2);

            FileOutputStream fout = new FileOutputStream(sbCombinedFile.toString());

            readWrite(fin1, fout);
            readWrite(fin2, fout);

            fin1.close();
            fin2.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
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
    private static boolean isStrInDir(String str, String file) {
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
                    sb.append((isStrInDir(str, file.getPath())) ? "founded" : "-");
                    sb.append("\n");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            sb.append("no such dir...\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String file1 = "mntZdrives";
        String file2 = "switchOutput";
        String str = "bash";
        String dir = ".";

        // склеивание двух файлов
        System.out.println(combineFiles(file1, file2) == 0 ? "Files combined\n" : "something wrong\n");

        // поиск строки в файле
        System.out.printf("String \"%s\" in file \"%s\" founded - %s\n\n", str, file1, isStrInDir(str, file1));

        // поиск строки в папке
        System.out.printf("Results of checking String \"%s\" in directory \"%s\" are:\n%s", str, dir, checkStrInDir(str, dir));
    }
}