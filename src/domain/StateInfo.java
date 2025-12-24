package domain;

import java.util.ArrayList;
import java.util.List;

public class StateInfo {

    public String name;
    public String stateType;
    public List<TransitionInfo> transitions = new ArrayList<>();
    public List<String> entryActions = new ArrayList<>();
    public List<String> exitActions = new ArrayList<>();

}
