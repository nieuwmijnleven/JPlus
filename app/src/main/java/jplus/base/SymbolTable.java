package jplus.base;

import jplus.generator.TextChangeRange;
import org.w3c.dom.Text;

import java.util.Comparator;
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
        SymbolInfo symbolInfo = symbolMap.get(name);
        if (symbolInfo == null && parent != null) {
            symbolInfo = parent.resolve(name);
        }
        return symbolInfo;
    }

    public boolean contains(String symbol, TypeInfo.Type type) {
        SymbolInfo symbolInfo = resolve(symbol);
        if (symbolInfo != null && symbolInfo.getTypeInfo().getType() == type) {
            return true;
        }
        return false;
    }

    public List<String> findSymbolsByType(List<TypeInfo.Type> typeList) {
        return symbolMap.entrySet().stream().map(Map.Entry::getValue).filter(symbolInfo -> typeList.contains(symbolInfo.getTypeInfo().type)).sorted(
            Comparator.<SymbolInfo>comparingInt(value -> value.getRange().startLine()).thenComparingInt(value -> value.getRange().startIndex())).map(symbolInfo -> symbolInfo.getSymbol()).toList();
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
        return enclosing.computeIfAbsent(name, s -> new SymbolTable((this)));
//        return enclosing.getOrDefault(name, new SymbolTable(this));
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
