package domain;

public enum States {
    STATE("state"),
    INITIAL_STATE("startstate"),
    FINAL_STATE("endstate");

    private final String state;

    States(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
