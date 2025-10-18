package jplus.base;

public class TypeInfo {
    public final String name;
    public final boolean isNullable;
    public final Type type;

    public static enum Type {
        Class,
        Method,
        Reference,
        Primitive,
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

    @Override
    public String toString() {
        return "TypeInfo{" +
                "name='" + name + '\'' +
                ", isNullable=" + isNullable +
                ", type=" + type +
                '}';
    }
}
