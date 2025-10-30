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
        Array,
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

    public static class Builder {
        private String name;
        private boolean isNullable;
        private Type type;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder isNullable(boolean isNullable) {
            this.isNullable = isNullable;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public TypeInfo build() {
            return new TypeInfo(name, isNullable, type);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
