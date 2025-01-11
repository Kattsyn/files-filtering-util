package kattsyn.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static final String FLOAT_REGEX = "/^-?(0|[1-9]+)(?:[.]\\d{1,2}|)$/";
    public static final String INTEGER_REGEX = "[0-9]+";
    public static final String STRING_REGEX = ".*";

    public static final String[] regexes = new String[]{FLOAT_REGEX, INTEGER_REGEX, STRING_REGEX};

    public static final String TEST_INPUT_1 = """
            agasga
            skwk
            24
            2.1
            wda
            123
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
        System.out.println("Всего floats: " + floats.size());
        System.out.println("Всего integers: " + integers.size());
        System.out.println("Всего strings: " + strings.size());


    }


}