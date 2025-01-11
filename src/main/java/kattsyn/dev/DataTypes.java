package kattsyn.dev;

public enum DataTypes {
    //todo: add regexes
    Float("floats", "^-?(0|[1-9]+)(?:[.]\\d{1,2}|)$"),
    Integer("integers", "[0-9]+"),
    String("strings", ".*");

    final String fileName;
    final String regex;

    DataTypes(String fileName, String regex) {
        this.fileName = fileName;
        this.regex = regex;
    }
}
