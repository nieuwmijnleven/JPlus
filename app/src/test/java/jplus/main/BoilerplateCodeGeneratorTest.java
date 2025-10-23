package jplus.main;

import jplus.analyzer.SymbolAnalyzer;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.base.SymbolTable;
import jplus.generator.BoilerplateCodeGenerator;
import jplus.generator.JPlusParserRuleContext;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoilerplateCodeGeneratorTest {

    private String makeCode(String applyPart, String classPart) {
        return applyPart + "\n" + classPart;
    }

    @Test
    void emptyClassMember_apply_() throws Exception {
        String apply = "apply getter;";
        String emptyClassMember = "public class User {}";

        CharStream input = CharStreams.fromString(makeCode(apply, emptyClassMember));
        JPlus20Lexer lexer = new JPlus20Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JPlus20Parser parser = new JPlus20Parser(tokens);
        JPlusParserRuleContext jPlusParserRuleContext = parser.start_();
//        System.out.println(jPlusParserRuleContext.toStringTree(parser));

        jPlusParserRuleContext.getText();

        SymbolAnalyzer symbolAnalyzer = new SymbolAnalyzer();
        symbolAnalyzer.visit(jPlusParserRuleContext);
        SymbolTable symbolTable = symbolAnalyzer.getTopLevelSymbolTable();
//        System.out.println(symbolTable.toString());

        BoilerplateCodeGenerator boilerplateCodeGenerator = new BoilerplateCodeGenerator(symbolTable);
        boilerplateCodeGenerator.visit(jPlusParserRuleContext);

        String generatedBoilerplate = boilerplateCodeGenerator.generate();
        System.out.println(generatedBoilerplate);

        MessageDigest messageDigest = MessageDigest.getInstance("sha-1");
        byte[] hash = messageDigest.digest(generatedBoilerplate.getBytes(StandardCharsets.UTF_8));
        String hashString = Base64.getEncoder().encodeToString(hash);
        System.out.println(hashString);
    }
}
