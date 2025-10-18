package jplus.main;

import jplus.analyzer.NullabilityChecker;
import jplus.analyzer.SymbolAnalyzer;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.base.SymbolTable;
import jplus.generator.BoilerplateCodeGenerator;
import jplus.generator.JPlusParserRuleContext;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: jplus <java file>");
            return;
        }

        CharStream input = CharStreams.fromFileName(args[0]);
        JPlus20Lexer lexer = new JPlus20Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JPlus20Parser parser = new JPlus20Parser(tokens);
        JPlusParserRuleContext jPlusParserRuleContext = parser.start_();
//        System.out.println(jPlusParserRuleContext.toStringTree(parser));

        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(jPlusParserRuleContext);
        if (!nullabilityChecker.hasPassed()) {
            return;
        }

        jPlusParserRuleContext.getText();

        SymbolAnalyzer symbolAnalyzer = new SymbolAnalyzer();
        symbolAnalyzer.visit(jPlusParserRuleContext);
        SymbolTable symbolTable = symbolAnalyzer.getTopLevelSymbolTable();
//        System.out.println(symbolTable.toString());

        BoilerplateCodeGenerator boilerplateCodeGenerator = new BoilerplateCodeGenerator(symbolTable);
        boilerplateCodeGenerator.visit(jPlusParserRuleContext);
        System.out.println(boilerplateCodeGenerator.generate());
    }
}
