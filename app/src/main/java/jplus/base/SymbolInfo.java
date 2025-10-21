package jplus.base;

import jplus.generator.TextChangeRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolInfo {
    private final String symbol;
    private final TypeInfo typeInfo;
    private final TextChangeRange range;
    private final String originalText;
    private final List<Modifier> modifierList;

    private SymbolTable symbolTable;

    public SymbolInfo(String symbol, TypeInfo typeInfo, TextChangeRange range, String originalText, List<Modifier> modifierList) {
        this.symbol = symbol;
        this.typeInfo = typeInfo;
        this.range = range;
        this.originalText = originalText;
        this.modifierList = modifierList;
    }

    public SymbolInfo(String symbol, TypeInfo typeInfo, TextChangeRange range, String originalText) {
        this(symbol, typeInfo, range, originalText, new ArrayList<>());
    }

    public String getSymbol() {
        return symbol;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public TextChangeRange getRange() {
        return range;
    }

    public String getOriginalText() {
        return originalText;
    }

    public List<Modifier> getModifierList() {
        return Collections.unmodifiableList(modifierList);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String toString() {
        return "SymbolInfo{" +
                "typeInfo=" + typeInfo +
                ", range=" + range +
                ", originalText='" + originalText + '\'' +
                '}';
    }
}
