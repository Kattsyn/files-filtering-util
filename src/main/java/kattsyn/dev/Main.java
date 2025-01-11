package kattsyn.dev;

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

        System.out.println("Вывод первого файла:\n " + TEST_INPUT_1);
        System.out.println();
        System.out.println("Вывод второго файла:\n " + TEST_INPUT_2);
        System.out.println();

        //Парсинг содержимого файлов в один список
        String[] filesInputs = new String[]{TEST_INPUT_1, TEST_INPUT_2};

        List<String> stringList = new ArrayList<>();
        for (String file : filesInputs) {
            stringList.addAll(List.of(file.split("\n")));
        }

        System.out.println(stringList);

        String[] classesNames = new String[]{"Float, Integer, String"};

        List<Float> floats = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        //todo: мапу, где ключ - имя типа данных, а значение ArrayList

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
        System.out.println("Всего вещественных чисел записано: " + floats.size());
        System.out.println("Всего целых чисел записано: " + integers.size());
        System.out.println("Всего строк записано: " + strings.size());

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

        System.out.println("min: " + min);
        System.out.println("max: " + max);
        System.out.println("sum: " + sum);
        System.out.println("avg: " + sum / (floats.size() + integers.size()));


    }


}