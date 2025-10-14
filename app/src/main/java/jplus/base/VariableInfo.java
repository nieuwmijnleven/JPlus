package jplus.base;

public class VariableInfo {
    private final TypeInfo typeInfo;
    private final String name;

    public VariableInfo(TypeInfo typeInfo, String name) {
        this.typeInfo = typeInfo;
        this.name = name;
    }

    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    public String getName() {
        return name;
    }

    public boolean isNullable() {
        return typeInfo.isNullable();
    }
}
