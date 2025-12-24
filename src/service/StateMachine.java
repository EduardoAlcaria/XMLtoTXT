package service;

import domain.StateInfo;
import domain.States;
import domain.TransitionInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StateMachine {
    protected static List<StateInfo> extractStates(Document doc) {
        List<StateInfo> states = new ArrayList<>();

        NodeList stateNodes = doc.getElementsByTagName("STATE");

        for (int i = 0; i < stateNodes.getLength(); i++) {
            Node stateNode = stateNodes.item(i);
            if (stateNode.getNodeType() == Node.ELEMENT_NODE) {
                Element stateElement = (Element) stateNode;
                StateInfo state = new StateInfo();

                state.name = Extract.elementText(stateElement, "NAME");
                state.stateType = Extract.elementText(stateElement, "STATE_TYPE");


                NodeList entryActionNodes = stateElement.getElementsByTagName("ENTRY_ACTIONS");
                if (entryActionNodes.getLength() > 0) {
                    Element entryActionsElement = (Element) entryActionNodes.item(0);
                    NodeList actionNodes = entryActionsElement.getElementsByTagName("ACTION");
                    for (int j = 0; j < actionNodes.getLength(); j++) {
                        Element actionElement = (Element) actionNodes.item(j);
                        String action = Extract.elementText(actionElement, "DO_ACTION");
                        if (action != null && !action.isEmpty()) {
                            state.entryActions.add(action);
                        }
                    }
                }


                NodeList exitActionNodes = stateElement.getElementsByTagName("EXIT_ACTIONS");
                if (exitActionNodes.getLength() > 0) {
                    Element exitActionsElement = (Element) exitActionNodes.item(0);
                    NodeList actionNodes = exitActionsElement.getElementsByTagName("ACTION");
                    for (int j = 0; j < actionNodes.getLength(); j++) {
                        Element actionElement = (Element) actionNodes.item(j);
                        String action = Extract.elementText(actionElement, "DO_ACTION");
                        if (action != null && !action.isEmpty()) {
                            state.exitActions.add(action);
                        }
                    }
                }


                NodeList transitionsNodes = stateElement.getElementsByTagName("TRANSITIONS");
                if (transitionsNodes.getLength() > 0) {
                    Element transitionsElement = (Element) transitionsNodes.item(0);
                    NodeList transitionNodes = transitionsElement.getChildNodes();

                    for (int j = 0; j < transitionNodes.getLength(); j++) {
                        Node transNode = transitionNodes.item(j);
                        if (transNode.getNodeType() == Node.ELEMENT_NODE &&
                                transNode.getNodeName().equals("TRANSITION")) {

                            Element transElement = (Element) transNode;
                            TransitionInfo transition = new TransitionInfo();

                            transition.event = Extract.elementText(transElement, "EVENT");
                            transition.doAction = Extract.elementText(transElement, "DO_ACTION");
                            transition.condition = Extract.elementText(transElement, "CONDITION");
                            transition.toState = Extract.elementText(transElement, "TO_STATE");

                            state.transitions.add(transition);
                        }
                    }
                }

                states.add(state);
            }
        }

        return states;
    }


    protected static void writeStates(PrintWriter writer, List<StateInfo> states) {
        if (states.isEmpty()) {
            return;
        }

        writer.println("states {");

        for (StateInfo state : states) {
            String stateDeclaration = getStateDeclaration(state.stateType);
            String stateName = state.name;

            if (stateName.equalsIgnoreCase("{start}")){
                writer.println("   " + stateDeclaration + " {");

            }else{
                writer.println("   " + stateDeclaration + " " + stateName + " {");
            }



            for (String entryAction : state.entryActions) {
                writer.println("      onEntry do " + entryAction + ";");
            }


            for (TransitionInfo transition : state.transitions) {
                writer.print("      ");

                if (transition.event != null && !transition.event.isEmpty()) {
                    writer.print("on " + transition.event + " ");
                } else {
                    writer.print("always ");
                }


                if (transition.condition != null && !transition.condition.isEmpty()) {
                    writer.print("[" + transition.condition + "] ");
                }


                if (transition.doAction != null && !transition.doAction.isEmpty()) {
                    writer.print("do " + transition.doAction + " ");
                }

                writer.println("transitionTo " + transition.toState + ";");
            }


            for (String exitAction : state.exitActions) {
                writer.println("      onExit do " + exitAction + ";");
            }

            writer.println("   }");
        }

        writer.println("}");
    }


    private static String getStateDeclaration(String stateType) {
        if (stateType == null) {
            return "state";
        }

        return switch (stateType) {
            case "INITIAL_STATE" -> States.INITIAL_STATE.getState();
            case "FINAL_STATE" -> States.FINAL_STATE.getState();
            default -> States.STATE.getState();
        };
    }
}
