package domain;

public enum Flags {
    PARENTKEY("P"),
    PRIMARYKEY("K"),
    ATTRIBUTE("A"),
    MANDATORY("M"),
    INSERTABLE("I"),
    UPDATEALLOWED("U"),
    DEFAULTLOV("L"),
    DERIVED("/DERIVED"),
    NONE("-");

    private String value;

    Flags(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
