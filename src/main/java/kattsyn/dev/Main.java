package kattsyn.dev;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    /*
     * ^ - начало строки
     * [+-]? - один или отсутствующий + или -
     *
     * $ - конец строки
     */

    public static final String FLOAT_REGEX = "^[+-]?([0-9]+[.][0-9]+)$";
    public static final String INTEGER_REGEX = "[0-9]+"; //todo: добавить обработку отриц чисел
    public static final String STRING_REGEX = ".*";

    public static final String[] regexes = new String[]{FLOAT_REGEX, INTEGER_REGEX, STRING_REGEX};

    public static final String TEST_INPUT_1 = """
            agasga
            skwk
            24
            2.1
            wda
            123
            00.021
            0.0
            1,23
            """;
    public static final String TEST_INPUT_2 = """
            21
            bara
            чичичи
            bere
            """;


    public static void main(String[] args) {

        List<String> testFile1Lines = new ArrayList<>();
        try (FileReader fileReader = new FileReader("test_input1.txt"); BufferedReader bufferedReader = new BufferedReader(fileReader)){
            testFile1Lines = bufferedReader.lines().toList();
        } catch (IOException e) {
            System.out.println("Error while file reading: " + e);
        }

//        System.out.println("Вывод первого файла:\n " + TEST_INPUT_1);
//        System.out.println();
        System.out.println("Вывод второго файла:\n " + TEST_INPUT_2);
        System.out.println();

        //Парсинг содержимого файлов в один список
        String[] filesInputs = new String[]{/*TEST_INPUT_1,*/ TEST_INPUT_2};

        List<String> stringList = new ArrayList<>();
        for (String file : filesInputs) {
            stringList.addAll(List.of(file.split("\n")));
        }
        stringList.addAll(testFile1Lines);
        System.out.println("stringList: " + stringList);

        String[] classesNames = new String[]{"Float, Integer, String"};

        List<Float> floats = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        //todo: мапу, где ключ - имя типа данных, а значение ArrayList


        //todo: создание файлов делать так:
        /*
        перебирать вхождения в HashMap. Если entry.value.size > 0, то создаем
        файл по имени (доп строка в параметрах)+entry.key
         */

        for (String string : stringList) {
            if (string.matches(FLOAT_REGEX)) {
                floats.add(Float.parseFloat(string));
            } else if (string.matches(INTEGER_REGEX)) {
                integers.add(Integer.parseInt(string));
            } else {
                strings.add(string);
            }
        }

        System.out.println("floats: " + floats);
        System.out.println("integers: " + integers);
        System.out.println("strings: " + strings);

        //Сбор статистики
        //short stats
        System.out.println("SHORT STATS");
        System.out.println("Всего вещественных чисел записано: " + floats.size());
        System.out.println("Всего целых чисел записано: " + integers.size());
        System.out.println("Всего строк записано: " + strings.size());
        System.out.println();

        //full stats
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float sum = 0;

        for (List<? extends Number> num : Arrays.asList(floats, integers)) {
            for (Number n : num) {
                float value = n.floatValue();
                min = Math.min(min, value);
                max = Math.max(max, value);
                sum += value;
            }
        }
        /*
        Без параметра записи в текущие файлы нужно будет создать файлы, проверить
        существует ли такой файл, если да, то перезаписать информацию и возможно вывести об этом сообщение
        если нет, то просто записать данные в файл. Метод записи в файл должен иметь на вход имя создания файла,
        булевскую переменную ПерезаписатьЛи, путь по которому создать файл и ArrayList строк которые нужно записать в файл.
        Можно добавить обработку является ли передаваемый путь директорией.
         */

        System.out.println("FULL STATS");
        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("sum: " + sum);
        System.out.println("avg: " + sum / (floats.size() + integers.size()));

        System.out.println();



        String FLOATS_FILE_NAME = "floats.txt";
        //todo: вынести в отдельный метод создания файла, если нет режима записи в имеющийся
        try {
            File floatFile = new File(FLOATS_FILE_NAME);
            if (floatFile.createNewFile()) {
                System.out.println("File created: " + floatFile.getAbsolutePath());
            } else {
                System.out.println("File already exists: " + floatFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Unexpected exception while file creating: " + e.getMessage());
        }

        try (FileWriter floatsWriter = new FileWriter(FLOATS_FILE_NAME)) {
            for (Float f : floats) {
                floatsWriter.write(f + "\n");
            }
        } catch (IOException exception) {
            System.out.println("Unexpected exception while writing file: " + exception.getMessage());
        }
    }
}