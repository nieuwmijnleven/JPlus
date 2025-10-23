package jplus.processor;

import jplus.analyzer.NullabilityChecker;
import jplus.analyzer.SymbolAnalyzer;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.base.SymbolTable;
import jplus.generator.BoilerplateCodeGenerator;
import jplus.generator.JPlusParserRuleContext;
import jplus.generator.TextChangeRange;
import jplus.util.FragmentedText;
import jplus.util.Utils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JPlusProcessor {

    private final String originalText;
    private final TextChangeRange originalTextRange;
    private JPlus20Parser parser;
    private JPlusParserRuleContext parseTree;
    private SymbolTable symbolTable;
    private boolean nullabilityChecked = false;
    private boolean symbolsAnalyzed = false;

    public JPlusProcessor(String originalText) {
        this.originalText = originalText;
        this.originalTextRange = Utils.computeTextChangeRange(originalText, 0, originalText.length()-1);
    }

    public JPlusProcessor(Path filePath) throws Exception {
        this(Files.readString(filePath, StandardCharsets.UTF_8));
    }

    public void process() throws Exception {
//        CodeGeneratorContext context = CodeGeneratorContext.getInstance();
//        context.setOriginal(text);
//        TextChangeRange range = Utils.computeTextChangeRange(text, 0, text.length()-1);
//        context.setFragmentedText(new FragmentedText(range, text));

        CharStream input = CharStreams.fromString(originalText);
        JPlus20Lexer lexer = new JPlus20Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser = new JPlus20Parser(tokens);

        parseTree = parser.start_();
    }

    public List<NullabilityChecker.NullabilityIssue> checkNullability() {
        if (parseTree == null) {
            throw new IllegalStateException("Call process() first.");
        }

        NullabilityChecker nullabilityChecker = new NullabilityChecker();
        nullabilityChecker.visit(parseTree);
        nullabilityChecked = true;
        return nullabilityChecker.getIssues();
    }

    public void analyzeSymbols() {
        if (parseTree == null) {
            throw new IllegalStateException("Call process() first.");
        }

        SymbolAnalyzer symbolAnalyzer = new SymbolAnalyzer();
        symbolAnalyzer.visit(parseTree);
        symbolTable = symbolAnalyzer.getTopLevelSymbolTable();
        symbolsAnalyzed = true;
    }

    public String generateJavaCode() {
        if (!nullabilityChecked || !symbolsAnalyzed) {
            throw new IllegalStateException("Must perform nullability check and symbol analysis first.");
        }

        String generated = parseTree.getText();
        TextChangeRange generatedRange = Utils.computeTextChangeRange(generated, 0, generated.length()-1);
        FragmentedText fragmentedText = new FragmentedText(generatedRange, generated);
        BoilerplateCodeGenerator generator = new BoilerplateCodeGenerator(symbolTable, fragmentedText);
        generator.visit(parseTree);
        return generator.generate();
    }

    public String compile() {
        try {
            process();
            checkNullability();
            analyzeSymbols();
            return generateJavaCode();
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            System.err.println(e);
//            throw new RuntimeException(e);
        }

        return null;
    }

    // Getter
    public JPlusParserRuleContext getParseTree() {
        return parseTree;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
