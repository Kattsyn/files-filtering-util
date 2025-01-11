package kattsyn.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

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

        for (String string : stringList) {

        }

    }


}