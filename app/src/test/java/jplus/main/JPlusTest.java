package jplus.main;

import jplus.analyzer.NullabilityChecker;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.generator.JPlusParserRuleContext;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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

    private record ParseResult(JPlusParserRuleContext parseTree, JPlus20Parser parser){
        String toParseTreeString() {
            return parseTree.toStringTree(parser);
        }
    }

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

    @Test
    void testNullableType1() throws Exception {
        ParseResult parseResult = createParseTreeFromFile("./src/test/samples/NullableType1.jplus");

        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(parseResult.parseTree);

        assertEquals("Error: (line:6, column:8) s2 is a non-nullable variable. But null value is assigned to it.\n", outContent.toString());
    }

    @Test
    void testNullableType2() throws Exception {
        ParseResult parseResult = createParseTreeFromFile("./src/test/samples/NullableType2.jplus");

        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(parseResult.parseTree);

        assertEquals("Error: (line:8, column:8) s1 is a nullable variable. But it direct accesses to length(). You must consider to use null-safe operator(?.)\n", outContent.toString());
    }

    @Test
    void testNullsafeOperator() throws Exception {
        checkGeneratedCode("./src/test/samples/NullsafeOperator.jplus", "jmllRsMo+kZOWnmV1fd9mexA77M=");
    }

    @Test
    void testElvisOperator() throws Exception {
        checkGeneratedCode("./src/test/samples/ElvisOperator.jplus", "lJ1j7ieF7Kvd/xvM++eYTxAd+M4=");
    }

    @Test
    void testCascadingElvisOperator() throws Exception {
        checkGeneratedCode("./src/test/samples/CascadingElvisOperator.jplus", "gkYR2fcqei23p1gbxYEcQQXP8Es=");
    }

    @Test
    void testNullsafeWithElvisOperator() throws Exception {
        checkGeneratedCode("./src/test/samples/NullsafeWithElvisOperator.jplus", "EQeqIo9gt+H9pgoBDDsVPiRXH0E=");
    }

    private void checkGeneratedCode(String fileName, String expected) throws IOException, NoSuchAlgorithmException {
        ParseResult parseResult = createParseTreeFromFile(fileName);
        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(parseResult.parseTree);
        if (!nullabilityChecker.hasPassed()) {
            fail();
        }

        String generatedJavaCode = parseResult.parseTree.getText();
        parseResult = createParseTreeFromString(generatedJavaCode);
        String parseTreeString = parseResult.toParseTreeString();

        MessageDigest messageDigest = MessageDigest.getInstance("sha-1");
        byte[] hash = messageDigest.digest(parseTreeString.getBytes(StandardCharsets.UTF_8));
        String hashString = Base64.getEncoder().encodeToString(hash);

        assertEquals(expected, hashString);
    }

    @Test
    void testCodeGeneration() throws Exception {
        ParseResult parseResult = createParseTreeFromFile("./src/test/samples/TestExample.java");
        String parseTreeString  = parseResult.toParseTreeString();
        String javaCode = parseResult.parseTree.getText();

        ParseResult parseResultByCodeGenerator = createParseTreeFromString(javaCode);
        String parseTreeStringByCodeGenerator = parseResultByCodeGenerator.toParseTreeString();

        assertEquals(parseTreeString, parseTreeStringByCodeGenerator);
    }
}