package jplus.main;

import jplus.analyzer.NullabilityChecker;
import jplus.analyzer.SymbolAnalyzer;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.base.SymbolTable;
import jplus.generator.BoilerplateCodeGenerator;
import jplus.generator.JPlusParserRuleContext;
import jplus.processor.JPlusProcessor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.out.println("Usage: jplus <java file>");
//            return;
//        }

//        JPlusProcessor processor = new JPlusProcessor(Path.of(args[0]));
        JPlusProcessor processor = new JPlusProcessor(Path.of("./app/src/test/samples/NullsafeWithElvisOperator2.jplus"));
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
