package jplus.main;

import jplus.analyzer.NullabilityChecker;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.generator.JavaCodeGenerator;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

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
        ParseTree parseTree = parser.start_();
        //System.out.println(parseTree.toStringTree(parser));

        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(parseTree);
        if (nullabilityChecker.hasPassed()) {
            JavaCodeGenerator javaCodeGenerator = new JavaCodeGenerator();
            String javaCodes = javaCodeGenerator.visit(parseTree);
            System.out.println(javaCodes);
        }
    }
}
