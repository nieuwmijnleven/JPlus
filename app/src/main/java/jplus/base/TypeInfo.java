package jplus.base;

public class TypeInfo {
    public final String name;
    public final boolean isNullable;

    public TypeInfo(String name, boolean isNullable) {
        this.name = name;
        this.isNullable = isNullable;
    }

    public String getName() {
        return name;
    }

    public boolean isNullable() {
        return isNullable;
    }
}
