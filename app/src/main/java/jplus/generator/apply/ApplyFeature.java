package jplus.generator.apply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplyFeature {
    private final String action;
    private final List<String> argumentList;

    public ApplyFeature(String action) {
        this.action = action;
        this.argumentList = new ArrayList<>();
    }

    public ApplyFeature(String action, List<String> argumentList) {
        this(action);
        this.argumentList.addAll(argumentList);
    }

    public void addArgument(String argument) {
        argumentList.add(argument);
    }

    public String getAction() {
        return action;
    }

    public List<String> getArgumentList() {
        return Collections.unmodifiableList(argumentList);
    }

    @Override
    public String toString() {
        return "ApplyFeature{" +
                "action='" + action + '\'' +
                ", argumentList=" + argumentList +
                '}';
    }
}
