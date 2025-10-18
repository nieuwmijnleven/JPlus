package jplus.base;

import jplus.generator.TextChangeRange;

public class SymbolInfo {
    private final TypeInfo typeInfo;
    private final TextChangeRange range;
    private final String originalText;

    public SymbolInfo(TypeInfo typeInfo, TextChangeRange range, String originalText) {
        this.typeInfo = typeInfo;
        this.range = range;
        this.originalText = originalText;
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

    @Override
    public String toString() {
        return "SymbolInfo{" +
                "typeInfo=" + typeInfo +
                ", range=" + range +
                ", originalText='" + originalText + '\'' +
                '}';
    }
}
