package jplus.base;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, VariableInfo> variables = new HashMap<>();

    public void declare(String name, VariableInfo variableInfo) {
        variables.put(name, variableInfo);
    }

    public VariableInfo resolve(String name) {
        return variables.get(name);
    }
}
