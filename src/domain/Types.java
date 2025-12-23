package domain;

public enum Types {
    NUMBER("NUMBER"),
    TEXT("TEXT"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"),
    DATETIME("DATETIME"),
    ENUMERATION("ENUMERATION");

    private final String type;

    Types(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
