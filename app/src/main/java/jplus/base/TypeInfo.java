package jplus.base;

public class TypeInfo {
    public final String name;
    public final boolean isNullable;
    public final Type type;

    public enum Type {
        Class,
        Method,
        Reference,
        Primitive,
        Constructor,
        Unknown
    }

    public TypeInfo(String name, boolean isNullable, Type type) {
        this.name = name;
        this.isNullable = isNullable;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TypeInfo{" +
                "name='" + name + '\'' +
                ", isNullable=" + isNullable +
                ", type=" + type +
                '}';
    }
}
