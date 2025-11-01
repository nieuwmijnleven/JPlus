package jplus.main;

import jplus.processor.JPlusProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

    @Test
    void testNullableType1() throws Exception {
        JPlusProcessor processor = new JPlusProcessor(Path.of("./src/test/samples/NullableType1.jplus"));
        processor.process();
        processor.analyzeSymbols();

        var issues = processor.checkNullability();
        if (!issues.isEmpty()) {
            issues.forEach(nullabilityIssue -> {
                System.out.printf("Error: (line:%d, column:%d) %s\n", nullabilityIssue.getLine(), nullabilityIssue.getColumn(), nullabilityIssue.getMessage());
            });
            return;
        }

        assertEquals("Error: (line:6, column:8) s2 is a non-nullable variable. But null value is assigned to it.\n", outContent.toString());
    }

    @Test
    void testNullableType2() throws Exception {
        JPlusProcessor processor = new JPlusProcessor(Path.of("./src/test/samples/NullableType2.jplus"));
        processor.process();
        processor.analyzeSymbols();

        var issues = processor.checkNullability();
        if (!issues.isEmpty()) {
            issues.forEach(nullabilityIssue -> {
                System.out.printf("Error: (line:%d, column:%d) %s\n", nullabilityIssue.getLine(), nullabilityIssue.getColumn(), nullabilityIssue.getMessage());
            });
            return;
        }

        assertEquals("Error: (line:8, column:8) s1 is a nullable variable. But it direct accesses to length(). You must consider to use null-safe operator(?.)\n", outContent.toString());
    }

    @Test
    void testNullabilityChecker1() throws Exception {
        JPlusProcessor processor = new JPlusProcessor(Path.of("./src/test/samples/NullabilityChecker1.jplus"));
        processor.process();
        processor.analyzeSymbols();

        var issues = processor.checkNullability();
        if (!issues.isEmpty()) {
            issues.forEach(nullabilityIssue -> {
                System.out.printf("Error: (line:%d, column:%d) %s\n", nullabilityIssue.getLine(), nullabilityIssue.getColumn(), nullabilityIssue.getMessage());
            });
            return;
        }

        String expected = "Error: (line:5, column:4) lastname is a non-nullable variable. But null value is assigned to it.\n" +
                "Error: (line:9, column:26) fullname is a nullable variable. But it directly accesses split(). Consider using null-safe operator(?.).\n" +
                "Error: (line:11, column:8) lastname is a non-nullable variable. But null value is assigned to it.\n" +
                "Error: (line:15, column:15) firstname is a nullable variable. But it directly accesses length(). Consider using null-safe operator(?.).\n";

        assertEquals(expected, outContent.toString());
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

    @Test
    void testApplyGetter() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyGetter.jplus", "2KehX1uPY6NqXQiwDyed7V5y5SQ=");
    }

    @Test
    void testApplySetter() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplySetter.jplus", "Q3LnhpGWKx1+rCJd5qFDg1XPiEg=");
    }

    @Test
    void testApplyEquals() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyEquals.jplus", "jN/DMdHIgKyDAs11SRYagASygi0=");
    }

    @Test
    void testApplyEquality() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyEquality.jplus", "+2gyKfUyySc1XkRvw14TFuasdUo=");
    }

    @Test
    void testApplyData() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyData.jplus", "LYAPBouq1JKIktjRfP4ImUzs080=");
    }

    @Test
    void testApplyToString() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyToString.jplus", "3WXjcqipwTO+F7/0KS/GSYQtIE4=");
    }

    @Test
    void testApplyHashCode() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyHashCode.jplus", "FiHLi04TBUW4bDxmWsfSNLklKGg=");
    }

    @Test
    void testApplyConstructorWithRequired() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyConstructorWithRequired.jplus", "gQSG/fieVXDm6m4/LcO0FojTI5U=");
    }

    @Test
    void testApplyConstructorWithAll() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyConstructorWithAll.jplus", "NjPcw+ydxvus5tvXDc53hdhqnAU=");
    }

    @Test
    void testApplyConstructorWithNo() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyConstructorWithNo.jplus", "yZtUmt/+VIn8dxshd7pNCR0wJ/8=");
    }

    @Test
    void testApplyConstructorWithAllAndBuilder() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyConstructorWithAllAndBuilder.jplus", "pY9jEWiq5yX/urk0RZcNMDjMsQc=");
    }

    @Test
    void testApplyConstructorWithRequiredAndBuilder() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyConstructorWithRequiredAndBuilder.jplus", "Oh4/D9SMOiOZmqtBH+KJf8l7PBw=");
    }

    @Test
    void testApplyConstructorWithNoAndBuilder() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyConstructorWithNoAndBuilder.jplus", "pY9jEWiq5yX/urk0RZcNMDjMsQc=");
    }

    @Test
    void testApplyDuplicatedHashCode() throws Exception {
        checkGeneratedCode("./src/test/samples/ApplyDuplicatedHashCode.jplus", "FiHLi04TBUW4bDxmWsfSNLklKGg=");
    }

    private void checkGeneratedCode(String fileName, String expected) throws Exception {
        JPlusProcessor processor = new JPlusProcessor(Path.of(fileName));
        processor.process();
        processor.analyzeSymbols();

        var issues = processor.checkNullability();
        if (!issues.isEmpty()) {
            fail();
        }

        String generatedJavaCode = processor.generateJavaCode();
//        System.err.println("generated = " + generatedJavaCode);
        processor = new JPlusProcessor(generatedJavaCode);
        processor.process();

        String parseTreeString = processor.getParseTreeString();
        String hashString = getHashString(parseTreeString);

        assertEquals(expected, hashString);
    }

    private String getHashString(String s) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("sha-1");
        byte[] hash = messageDigest.digest(s.getBytes(StandardCharsets.UTF_8));
        String hashString = Base64.getEncoder().encodeToString(hash);
        return hashString;
    }

    @Test
    void testCodeGeneration() throws Exception {
        JPlusProcessor processor = new JPlusProcessor(Path.of("./src/test/samples/TestExample.java"));
        processor.process();
        processor.analyzeSymbols();

        var issues = processor.checkNullability();
        if (!issues.isEmpty()) {
            fail();
        }

        String parseTreeString = processor.getParseTreeString();
        String generatedJavaCode = processor.generateJavaCode();

        processor = new JPlusProcessor(generatedJavaCode);
        processor.process();
        String parseTreeStringOfGeneratedJavaCode = processor.getParseTreeString();

        assertEquals(parseTreeString, parseTreeStringOfGeneratedJavaCode);
    }
}