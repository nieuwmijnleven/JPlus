package jplus.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CodeGeneratorUtils {
    public static String indent(String code, int level) {
        String indent = "    ".repeat(level);
        return Arrays.stream(code.split("\n"))
                .map(line -> indent + line)
                .collect(Collectors.joining("\n"));
    }
}
