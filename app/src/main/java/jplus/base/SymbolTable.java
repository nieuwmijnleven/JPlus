package jplus.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private SymbolTable parent;

    private final Map<String, SymbolInfo> symbolMap = new HashMap<>();

    private Map<String, SymbolTable> enclosing = new HashMap<>();

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public void declare(String name, SymbolInfo symbolInfo) {
        symbolMap.put(name, symbolInfo);
    }

    public SymbolInfo resolve(String name) {
        return symbolMap.get(name);
    }

    public List<String> findSymbolsByType(TypeInfo.Type type) {
        return symbolMap.entrySet().stream().filter(entry -> entry.getValue().getTypeInfo().type == type).map(entry -> entry.getKey()).toList();
    }

    public SymbolTable getParent() {
        return parent;
    }

    public SymbolTable addEnclosingSymbolTable(String name, SymbolTable symbolTable) {
        if (symbolTable == null) throw new IllegalArgumentException();
        enclosing.put(name, symbolTable);
        return symbolTable;
    }

    public SymbolTable getEnclosingSymbolTable(String name) {
        return enclosing.getOrDefault(name, new SymbolTable(this));
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                //"parent=" + parent +
                ", symbolMap=" + symbolMap +
                ", enclosing=" + enclosing +
                '}';
    }
}
