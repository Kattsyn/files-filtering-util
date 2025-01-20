package kattsyn.dev;

import java.io.*;
import java.util.*;


public class FilesFilteringUtil {

    private static final String FLOAT_FILE_NAME = "floats.txt";
    private static final String INTEGER_FILE_NAME = "integers.txt";
    private static final String STRING_FILE_NAME = "strings.txt";

    private static class CmdParams {
        boolean help;
        String resultPath;
        String fileNamePrefix;
        boolean appendMode;
        StatType statType;
        List<String> inputFiles;

        /**
         * Можно было заменить на Boolean (не примитив), чтобы иметь еще опцию null, помимо false и true.
         * Но такой подход менее явный и может быть менее читаемым.
         */
        public enum StatType {
            NONE,
            SHORT,
            FULL
        }

        public CmdParams() {
            this.help = false;
            this.resultPath = "";
            this.fileNamePrefix = "";
            this.appendMode = false;
            this.statType = StatType.NONE;
            this.inputFiles = new ArrayList<>();
        }

        public void setFullStatType() {
            this.statType = StatType.FULL;
        }

        public void setShortStatType() {
            this.statType = StatType.SHORT;
        }
    }

    /**
     * Метод, принимающий массив строк из параметров и имен файлов в формате .txt,
     * содержимое которых будет распределено по отдельным файлам исходя из типов данных: String, Float, Integer.
     * Дополнительные параметры: --help - для вывода информации об утилите.-o <path> - записав параметр -o, затем <path> - путь, можно указать куда складывать результирующие файлы.
     * (например: -o C:\Users\UserName\Documents, тогда файлы будут складываться по этому пути)-p <fileName> - префиксная строка, которая будет добавлена к именам результирующих файлов.
     * (например: -p result_, тогда файлы будут result_integers.txt, result_floats.txt, result_strings.txt)
     * -a - записав параметр -a будет включен режим добавления в уже существующие файлы, иначе по умолчанию будет режим перезаписи.
     * -s - записав параметр -s по завершению программы будет выведено сообщение с краткой статистикой.
     * Краткая статистика включает в себя: вывод кол-ва записанных слов или чисел по соответствующему им типу данных.
     * -f - записав параметр -f по завершению программы будет выведено сообщение с полной статистикой.
     * Полная статистика включает в себя: 1. Краткую статистику 2. Min, Max, Sum, Avg чисел, если они были в изначальных файлах.
     * 3. Min и Max длины строк, если они были в изначальных файлах.
     * @param args массив строк параметров
     */
    public static void filterUtils(String[] args) {
        CmdParams cmdParams = parseArgs(args);

        if(cmdParams.help) {
            outHelpInfo();
            System.exit(0);
        }

        List<String> stringList = mergeFilesContent(cmdParams.inputFiles);

        Map<String, List<String>> stringListMap = new HashMap<>();
        List<Number> numbers = new ArrayList<>();

        separateStringsByMap(stringList, stringListMap, numbers);

        for (Map.Entry<String, List<String>> entry : stringListMap.entrySet()) {
            writeContentToFile(cmdParams.resultPath, cmdParams.fileNamePrefix + entry.getKey(), entry.getValue(), cmdParams.appendMode);
        }

        switch (cmdParams.statType) {
            case SHORT -> outShortStats(stringListMap);
            case FULL -> outFullStats(stringListMap, numbers);
        }

    }


    /**
     * Метод, который парсит параметры args. Последовательно проходит по массиву параметров и собирает всё в класс CmdParams,
     * содержимое которого уже будет использоваться в дальнейшем
     * @param args массив строк параметров.
     * @return объект типа CmdParams.
     */
    private static CmdParams parseArgs(String[] args) {
        CmdParams cmdParams = new CmdParams();

        if (args.length == 0) {
            cmdParams.help = true;
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--help" -> cmdParams.help = true;
                case "-s" -> cmdParams.setShortStatType();
                case "-f" -> cmdParams.setFullStatType();
                case "-a" -> cmdParams.appendMode = true;
                case "-p" -> cmdParams.fileNamePrefix = args[++i];
                case "-o" -> cmdParams.resultPath = args[++i];

                default -> {
                    if (args[i].contains(".txt")) {
                        cmdParams.inputFiles.add(args[i]);
                    } else {
                        System.out.println("Ошибка разбора параметров: " + args[i]);
                    }
                }
            }
        }

        return cmdParams;
    }

    private static void outHelpInfo() {
        System.out.println("""
                Это утилита, которая на вход принимает доп. параметры и имена файлов в формате .txt,
                содержимое которых будет распределено по отдельным файлам исходя из типов данных: String, Float, Integer.
                Дополнительные параметры:
                
                --help - для вывода информации об утилите.
                
                -o <path> - записав параметр -o, затем <path> - путь, можно указать куда складывать результирующие файлы.
                (например: -o C:\\Users\\UserName\\Documents , тогда файлы будут складываться по этому пути)
                
                -p <fileName> - префиксная строка, которая будет добавлена к именам результирующих файлов.
                (например: -p result_, тогда файлы будут result_integers.txt, result_floats.txt, result_strings.txt)
                
                -a - записав параметр -a будет включен режим добавления в уже существующие файлы, иначе по умолчанию будет режим перезаписи.
                
                -s - записав параметр -s по завершению программы будет выведено сообщение с краткой статистикой.
                Краткая статистика включает в себя: вывод кол-ва записанных слов или чисел по соответствующему им типу данных.
                
                -f - записав параметр -f по завершению программы будет выведено сообщение с полной статистикой.
                Полная статистика включает в себя:
                1. Краткую статистику
                2. Min, Max, Sum, Avg чисел, если они были в изначальных файлах.
                3. Min и Max длины строк, если они были в изначальных файлах.
                """);
    }

    /**
     * Метод, который выводит неполную статистику. Под неполной понимается: вывод кол-ва записанных слов или чисел по соответствующему им типу данных.
     * Под числами имеется в виду строка, которую можно представить в виде числа, неважно целое или вещественное.
     * @param stringListMap мапа, с уже распределенными числами и строками.
     */
    private static void outShortStats(Map<String, List<String>> stringListMap) {
        System.out.println("Всего вещественных чисел записано: " + stringListMap.get(FLOAT_FILE_NAME).size());
        System.out.println("Всего целых чисел записано: " + stringListMap.get(INTEGER_FILE_NAME).size());
        System.out.println("Всего строк записано: " + stringListMap.get(STRING_FILE_NAME).size());
    }

    /**
     * Метод, который выводит полную статистику. Под полной статистикой понимается следующее:
     * 1. Содержимое неполной статистики (описано в другом методе),
     * 2. Для чисел - вывод min, max, avg, sum чисел.
     * 3. Для строк - вывод min и max длин строк.
     * Статистика выводится для тех типов данных, которые имеются хотя бы в одном экземпляре. То есть, если строк нет, то полная статистика по строкам выведена не будет, а только короткая.
     * То же самое для чисел.
     * @param stringListMap мапа, с распределенными строками и числами. Нужна для того, чтобы передать ее в метод outShortStats(),
     *                      а также по ключу STRING_FILE_NAME будет вытаскивать строки и выводить длину самой длинной и самой короткой строки
     * @param numbers список чисел, среди которых будет искать min, max, avg, sum чисел.
     */
    private static void outFullStats(Map<String, List<String>> stringListMap, List<Number> numbers) {
        outShortStats(stringListMap);

        if (!numbers.isEmpty()) {
            float min = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            float sum = 0;
            for (Number n : numbers) {
                float value = n.floatValue();
                min = Math.min(min, value);
                max = Math.max(max, value);
                sum += value;
            }
            System.out.println("Минимальное число: " + min);
            System.out.println("Максимальное число: " + max);
            System.out.println("Сумма чисел: " + sum);
            System.out.println("Среднее число: " + sum / (numbers.size()));
        }

        if (!stringListMap.get(STRING_FILE_NAME).isEmpty()) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (String str : stringListMap.get(STRING_FILE_NAME)) {
                min = Math.min(min, str.length());
                max = Math.max(max, str.length());
            }
            System.out.println("Минимальная длина строки: " + min);
            System.out.println("Максимальная длина строки: " + max);
        }
    }

    /**
     * Метод, который распределяет строки из списка stringList по спискам, соответствующих своим типам данных в мапе stringListMap.
     * Если строку можно представить числом, то сначала парсим до соответствующего типа данных, так можно убрать незначащие нули и гарантированно привести к нужному виду число.
     * Затем оно добавляется в список numbers, в котором собираются числа, чтобы в дальнейшем с ними делать статистику.
     * @param stringList список слов, которые нужно распределить
     * @param stringListMap мапа, в значениях которой лежат списки строк, куда уже будут распределяться строки или числа
     * @param numbers список чисел, для дальнейшего расчета статистики.
     */
    private static void separateStringsByMap(List<String> stringList, Map<String, List<String>> stringListMap, List<Number> numbers) {

        stringListMap.put(INTEGER_FILE_NAME, new ArrayList<>());
        stringListMap.put(FLOAT_FILE_NAME, new ArrayList<>());
        stringListMap.put(STRING_FILE_NAME, new ArrayList<>());

        for (String string : stringList) {
            if (isFloat(string)) {
                float floatVal = Float.parseFloat(string);
                stringListMap.get(FLOAT_FILE_NAME).add(String.valueOf(floatVal));
                numbers.add(floatVal);
            } else if (isInteger(string)) {
                int intVal = Integer.parseInt(string);
                stringListMap.get(INTEGER_FILE_NAME).add(String.valueOf(intVal));
                numbers.add(intVal);
            } else {
                stringListMap.get(STRING_FILE_NAME).add(string);
            }
        }
    }


    /**
     * Метод, который собирает содержимое файлов в один список и возвращает его.
     *
     * @param fileNames список, который содержит имена файлов
     * @return List строк, который имеет все собранные слова или числа из файлов.
     */
    private static List<String> mergeFilesContent(List<String> fileNames) {
        List<String> resultList = new ArrayList<>();

        for (String fileName : fileNames) {
            try (FileReader fileReader = new FileReader(fileName); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                resultList.addAll(bufferedReader.lines().toList());
            } catch (IOException e) {
                System.out.println("Error while file reading: " + e);
            }
        }

        return resultList;
    }

    /**
     * Функция, которая создает файл по указанному пути и возвращает его. В случае если такой директории не существует, функция её создаст.
     * Если не получится создать директорию, то создаст файл по дефолтному пути.
     * Если filePath будет пустой (null), то функция вернет файл, созданный по дефолтному пути.
     *
     * @param filePath путь, по которому создать файл
     * @param fileName имя файла
     * @return File который был создан либо по указанному пути, либо по дефолтному пути создания файла, либо null, если не удалось создать файл.
     */
    public static File createFile(String filePath, String fileName) {
        if (filePath.isBlank()) {
            return new File(fileName);
        }

        File directory = new File(filePath);

        if (!directory.exists()) {
            boolean dirCreated = directory.mkdirs();
            if (dirCreated) {
                System.out.println("Директория создана: " + directory.getAbsolutePath());
            } else {
                System.out.println("Не удалось создать директорию: " + directory.getAbsolutePath());
                return new File(fileName);
            }
        }

        File file = new File(directory, fileName);

        if (!file.exists()) {
            try {
                boolean fileCreated = file.createNewFile();
                if (fileCreated) {
                    System.out.println("Файл создан: " + file.getAbsolutePath());
                } else {
                    System.out.println("Файл уже существует: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                System.out.println("Непредвиденная ошибка при создании файла: " + e.getMessage());
                return null;
            }
        }

        return file;
    }

    /**
     * Функция, которая записывает содержимое списка в файл с указанным названием fileName в указанный путь filePath.
     *
     * @param filePath   путь, в котором должен содержаться файл
     * @param fileName   имя, которое должен иметь файл
     * @param content    список, содержимое которого будем записывать в файл
     * @param appendMode режим добавления в уже существующий файл. Если true, то будет добавлять в конец файла содержимое нашего списка content
     * @param <T>        параметр <T>, тип данных которого будем записывать в файлы.
     */
    public static <T> void writeContentToFile(String filePath, String fileName, List<T> content, boolean appendMode) {
        File createFileDirResult = createFile(filePath, fileName);

        if (createFileDirResult == null) {
            return;
        }

        try (FileWriter fileWriter = new FileWriter(createFileDirResult, appendMode)) {
            for (T f : content) {
                fileWriter.write(f + "\n");
            }
        } catch (IOException exception) {
            System.out.println("Непредвиденная ошибка при чтении файла: " + exception.getMessage());
        }
    }


    /**
     * Метод, который возвращает true, если строка подходит под определение целого числа.
     * Такое число обязательно должно состоять из цифр. Может содержать незначащие нули, при конвертации они будут убраны.
     * В начале может содержать '-' или '+'.
     * Не учитывается возможное переполнение диапазона Integer.
     *
     * @param str строка, которую хотим проверить, можно ли ее представить в виде вещественного числа.
     * @return true, если строку можно представить в виде целого числа, иначе false.
     */
    private static boolean isInteger(String str) {
        if (str.isBlank()) {
            return false;
        }
        char[] data = str.toCharArray();

        int index = 0;
        if ((data[0] == '-' || data[0] == '+') && (data.length > 1)) {
            index = 1;
        }
        for (; index < data.length; index++) {
            if (!Character.isDigit(data[index])) {
                return false;
            }
        }
        return true;
    }


    /**
     * Метод, который возвращает true, если строка подходит под определение вещественного числа.
     * Такое число обязательно должно содержать '.' в качестве разделителя целой и дробной части,
     * состоять из цифр. Может содержать незначащие нули, при конвертации они будут убраны. В начале может содержать '-' или '+'.
     * Не учитывается возможное переполнение диапазона Float.
     *
     * @param str строка, которую хотим проверить, можно ли ее представить в виде вещественного числа.
     * @return true, если строку можно представить в виде вещественного числа, иначе false.
     */
    private static boolean isFloat(String str) {
        if (str.isBlank()) {
            return false;
        }
        char[] data = str.toCharArray();
        int index = 0;
        boolean containsSeparator = false;
        if ((data[0] == '-' || data[0] == '+') && (data.length > 1)) {
            index = 1;
        }
        for (; index < data.length; index++) {
            if (!Character.isDigit(data[index])) {
                if (data[index] == '.' && !containsSeparator) {
                    containsSeparator = true;
                } else {
                    return false;
                }
            }
        }
        return containsSeparator;
    }
}
