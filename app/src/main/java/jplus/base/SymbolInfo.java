package jplus.base;

import jplus.generator.TextChangeRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolInfo {
    private final String symbol;
    private TypeInfo typeInfo;
    private final TextChangeRange range;
    private final String originalText;
    private final List<Modifier> modifierList;
    private SymbolTable symbolTable;

    public SymbolInfo(String symbol, TypeInfo typeInfo, TextChangeRange range, String originalText, List<Modifier> modifierList, SymbolTable symbolTable) {
        this.symbol = symbol;
        this.typeInfo = typeInfo;
        this.range = range;
        this.originalText = originalText;
        this.modifierList = modifierList;
        this.symbolTable = symbolTable;
    }

    public SymbolInfo(String symbol, TypeInfo typeInfo, TextChangeRange range, String originalText, List<Modifier> modifierList) {
        this(symbol, typeInfo, range, originalText, modifierList, null);
    }

    public String getSymbol() {
        return symbol;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(TypeInfo typeInfo) {
        this.typeInfo = typeInfo;
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

    public static SymbolInfo copyOf(SymbolInfo src) {
        return SymbolInfo.builder()
                .symbol(src.symbol)
                .typeInfo(src.typeInfo)
                .originalText(src.originalText)
                .range(src.range)
                .modifierList(src.modifierList)
                .symbolTable(src.symbolTable)
                .build();
    }

    @Override
    public String toString() {
        return "SymbolInfo{" +
                "typeInfo=" + typeInfo +
                ", range=" + range +
                ", originalText='" + originalText + '\'' +
                '}';
    }

    public static class Builder {
        private String symbol;
        private TypeInfo typeInfo;
        private TextChangeRange range;
        private String originalText;
        private List modifierList;
        private SymbolTable symbolTable;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder typeInfo(TypeInfo typeInfo) {
            this.typeInfo = typeInfo;
            return this;
        }

        public Builder range(TextChangeRange range) {
            this.range = range;
            return this;
        }

        public Builder originalText(String originalText) {
            this.originalText = originalText;
            return this;
        }

        public Builder modifierList(List modifierList) {
            this.modifierList = modifierList;
            return this;
        }

        public Builder symbolTable(SymbolTable symbolTable) {
            this.symbolTable = symbolTable;
            return this;
        }

        public SymbolInfo build() {
            return new SymbolInfo(symbol, typeInfo, range, originalText, modifierList, symbolTable);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
