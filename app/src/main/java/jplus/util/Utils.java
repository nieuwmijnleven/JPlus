package jplus.util;

import jplus.generator.TextChangeRange;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

public class Utils {

    private Utils() {}

    public static String getTokenString(ParserRuleContext ctx) {
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = (ctx.stop != null) ? ctx.stop.getStopIndex() : ctx.start.getStopIndex();
        return ctx.start.getTokenSource().getInputStream().getText(Interval.of(startIndex, stopIndex));
    }

    public static String indent(String string, int n) {
        if (string.isEmpty()) {
            return "";
        }

        if (n < 0) {
            throw new IllegalArgumentException("indent must be greater than 0.");
        }

        if (n == 0) {
            return string;
        }

        return " ".repeat(n);
    }

    public static String indentLines(String string, int n) {
        if (string.isEmpty()) {
            return "";
        }

        if (n < 0) {
            throw new IllegalArgumentException("indent must be greater than 0.");
        }

        if (n == 0) {
            return string;
        }

        Stream<String> stream = string.lines();
        final String spaces = " ".repeat(n);
        stream = stream.map(s -> spaces + s);
        return stream.collect(Collectors.joining("\n", "", ""));
    }

    public static String convertToPascalCase(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    public static String convertToCamelCase(String s) {
        return s.substring(0,1).toLowerCase() + s.substring(1);
    }

    public static int getIndexFromLineColumn(String text, TextChangeRange range, int targetLine, int targetColumn) {
        // 1. target이 주어진 range 범위 안에 있는지 확인
        if (targetLine < range.startLine() || targetLine > range.endLine()) {
            return -1;
        }

        if (targetLine == range.startLine() && targetColumn < range.startIndex()) {
            return -1;
        }

        if (targetLine == range.endLine() && targetColumn > range.inclusiveEndIndex()) {
            return -1;
        }

        // 2. 현재 위치 초기화 (range 시작점 기준)
        int currentLine = range.startLine();
        int currentColumn = range.startIndex();
        int index = 0;

        // 3. 텍스트 순회하면서 target 좌표 찾기
        while (index < text.length()) {
            if (currentLine == targetLine && currentColumn == targetColumn) {
                return index;
            }

            char c = text.charAt(index);
            if (c == '\n') {
                currentLine++;
                currentColumn = 0;
            } else {
                currentColumn++;
            }

            index++;
        }

        // 4. 끝까지 도달했지만 좌표 못 찾음
        return -1;
    }


    public static TextChangeRange computeTextChangeRange(String text, int startOffset, int endOffset) {
        if (startOffset < 0 || endOffset < startOffset || endOffset >= text.length()) {
            throw new IllegalArgumentException("Invalid start or end offset: " + "text.length() = " + text.length() + ", startOffset = " + startOffset + ", endOffset = " + endOffset);
        }

        int line = 1;
        int column = 0;
        int currentOffset = 0;

        int startLine = 1, startIndex = 0;
        int endLine = 1, endIndex = 0;

        while (currentOffset <= endOffset && currentOffset < text.length()) {
            char c = text.charAt(currentOffset);

            if (currentOffset == startOffset) {
                startLine = line;
                startIndex = column;
            }

            if (currentOffset == endOffset) {
                endLine = line;
                endIndex = column;
                break;
            }

            if (c == '\n') {
                line++;
                column = 0;
            } else {
                column++;
            }

            currentOffset++;
        }

        return new TextChangeRange(startLine, startIndex, endLine, endIndex);
    }

    public static TextChangeRange getRangeFromStartIndexAndEndIndex(
            String originalText,
            TextChangeRange originalTextChangeRange,
            int startIndex,
            int endIndex) {

        if (startIndex < 0 || endIndex < startIndex || endIndex >= originalText.length()) {
            throw new IllegalArgumentException("Invalid startIndex or endIndex");
        }

        int currentLine = originalTextChangeRange.startLine();
        int currentColumn = originalTextChangeRange.startIndex();
        int currentIndex = 0;

        int startLine = -1, startColumn = -1;
        int endLine = -1, endColumn = -1;

        while (currentIndex <= endIndex && currentIndex < originalText.length()) {
            if (currentIndex == startIndex) {
                startLine = currentLine;
                startColumn = currentColumn;
            }

            if (currentIndex == endIndex) {
                endLine = currentLine;
                endColumn = currentColumn;
                break;
            }

            char c = originalText.charAt(currentIndex);
            if (c == '\n') {
                currentLine++;
                currentColumn = 0;
            } else {
                currentColumn++;
            }

            currentIndex++;
        }

        // startIndex == endIndex일 경우 루프 안에서 끝날 수 있으므로 마지막 문자 처리
        if (endLine == -1 && currentIndex == endIndex) {
            endLine = currentLine;
            endColumn = currentColumn;
        }

        if (startLine == -1 || endLine == -1) {
            throw new IllegalStateException("Could not resolve line/column positions.");
        }

        return new TextChangeRange(startLine, startColumn, endLine, endColumn);
    }


//    public static TextChangeRange getTextChangeRange(ParserRuleContext ctx) {
//        int startLine = ctx.start.getLine();
//        int startIndex = ctx.start.getCharPositionInLine();
//        int endLine = ctx.stop.getLine();
//        int endIndex = ctx.stop.getCharPositionInLine() + ctx.stop.getText().length() - 1;
//        return new TextChangeRange(startLine, startIndex, endLine, endIndex);
//    }

    public static TextChangeRange getTextChangeRange(String original, ParserRuleContext ctx) {
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = (ctx.stop != null) ? ctx.stop.getStopIndex() : ctx.start.getStopIndex();
        return Utils.computeTextChangeRange(original, startIndex, stopIndex);
    }
}
