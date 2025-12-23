package domain;

public enum AccessModifiers {
    KEY("key    "),
    PARENT_KEY("parentkey    "),
    PUBLIC("public    "),
    PRIVATE("private    ");

    private final String accessModifier;

    AccessModifiers(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public String getAccessModifier() {
        return accessModifier;
    }
}
