package jplus.main;

import jplus.analyzer.NullabilityChecker;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.generator.JavaCodeGenerator;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class JPlusTest {

    private ByteArrayOutputStream outContent;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(new PrintStream(originalOut));
    }

    private record ParseResult(ParseTree parseTree, JPlus20Parser parser){}

    private ParseResult getParseResult(CharStream input) {
        JPlus20Lexer lexer = new JPlus20Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JPlus20Parser parser = new JPlus20Parser(tokens);
        return new ParseResult(parser.start_(), parser);
    }

    private ParseResult createParseTreeFromFile(String fileName) throws IOException {
        CharStream input = CharStreams.fromFileName(fileName);
        return getParseResult(input);
    }

    private ParseResult createParseTreeFromString(String code) throws IOException {
        CharStream input = CharStreams.fromString(code);
        return getParseResult(input);
    }

    private String getParseTreeString(ParseResult parseResult) {
        return parseResult.parseTree().toStringTree(parseResult.parser());
    }

    @Test
    void testCheckNullability1() throws Exception {
        ParseResult parseResult = createParseTreeFromFile("./src/test/samples/NullableType1.jplus");

        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(parseResult.parseTree);

        assertEquals("Error: (line:6, column:8) s2 is a non-nullable variable. But null value is assigned to it.\n", outContent.toString());
    }

    @Test
    void testCheckNullability2() throws Exception {
        ParseResult parseResult = createParseTreeFromFile("./src/test/samples/NullableType2.jplus");

        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(parseResult.parseTree);

        assertEquals("Error: (line:8, column:8) s1 is a nullable variable. But it direct accesses to length(). You must consider to use null-safe operator(?.)\n", outContent.toString());
    }

    @Test
    void testCodeGeneration() throws Exception {
        ParseResult parseResult = createParseTreeFromFile("./src/test/samples/TestExample.java");
        String parseTreeString  = getParseTreeString(parseResult);

        JavaCodeGenerator codeGenerator = new JavaCodeGenerator();
        String code = codeGenerator.visit(parseResult.parseTree);

        ParseResult parseResultByCodeGenerator = createParseTreeFromString(code);
        String parseTreeStringByCodeGenerator = getParseTreeString(parseResult);

        assertTrue(parseTreeString.equals(parseTreeStringByCodeGenerator));
    }
}