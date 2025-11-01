package jplus.main;

import jplus.processor.JPlusProcessor;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: jplus <java file>");
            return;
        }

        JPlusProcessor processor = new JPlusProcessor(Path.of(args[0]));
        processor.process();
        processor.analyzeSymbols();

        var issues = processor.checkNullability();
        if (!issues.isEmpty()) {
            issues.forEach(nullabilityIssue -> {
                System.out.printf("Error: (line:%d, column:%d) %s\n", nullabilityIssue.getLine(), nullabilityIssue.getColumn(), nullabilityIssue.getMessage());
            });
            return;
        }

        String javaCode = processor.generateJavaCode();
        System.out.println(javaCode);
    }
}
