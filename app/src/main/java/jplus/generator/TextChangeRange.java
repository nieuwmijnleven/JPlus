package jplus.generator;

import java.util.Objects;

public record TextChangeRange(int startLine, int startIndex, int endLine, int inclusiveEndIndex) {

    public boolean contains(TextChangeRange other) {
        return this.startLine() <= other.startLine()
                && this.endLine() >= other.endLine()
                && (this.startLine() != other.startLine() || this.startIndex() <= other.startIndex())
                && (this.endLine() != other.endLine() || this.inclusiveEndIndex() >= other.inclusiveEndIndex());
    }

    public boolean overlaps(TextChangeRange other) {
        return !(this.endLine() < other.startLine() ||
                this.startLine() > other.endLine() ||
                (this.endLine() == other.startLine() && this.inclusiveEndIndex() < other.startIndex()) ||
                (this.startLine() == other.endLine() && this.startIndex() > other.inclusiveEndIndex()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextChangeRange)) return false;
        TextChangeRange that = (TextChangeRange) o;
        return this.startLine() == that.startLine()
                && this.startIndex() == that.startIndex()
                && this.endLine() == that.endLine()
                && this.inclusiveEndIndex() == that.inclusiveEndIndex();
    }

    @Override
    public int hashCode() {
        return Objects.hash(startLine(), startIndex(), endLine(), inclusiveEndIndex());
    }

    @Override
    public String toString() {
        return "TextChangeRange{" +
                "startLine=" + startLine +
                ", startIndex=" + startIndex +
                ", endLine=" + endLine +
                ", inclusiveEndIndex=" + inclusiveEndIndex +
                '}';
    }
}