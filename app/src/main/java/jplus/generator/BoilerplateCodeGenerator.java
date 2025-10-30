package jplus.generator;

import jplus.base.JPlus20Parser.ApplyBlockContext;
import jplus.base.JPlus20Parser.ApplyDeclarationContext;
import jplus.base.JPlus20Parser.ApplyFeatureListContext;
import jplus.base.JPlus20Parser.ApplyStatementContext;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.generator.apply.ApplyFeature;
import jplus.generator.apply.ApplyFeatureProcessingContext;
import jplus.generator.apply.ApplyFeatureProcessor;
import jplus.generator.apply.ApplyStatement;
import jplus.generator.apply.BuilderFeatureProcessor;
import jplus.generator.apply.ConstructorFeatureProcessor;
import jplus.generator.apply.DataFeatureProcessor;
import jplus.generator.apply.EqualityFeatureProcessor;
import jplus.generator.apply.EqualsFeatureProcessor;
import jplus.generator.apply.GetterFeatureProcessor;
import jplus.generator.apply.HashCodeFeatureProcessor;
import jplus.generator.apply.SetterFeatureProcessor;
import jplus.generator.apply.ToStringFeatureProcessor;
import jplus.util.FragmentedText;
import jplus.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class BoilerplateCodeGenerator extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable symbolTable;
    private final FragmentedText fragmentedText;
    private final List<ApplyStatement> applyStatementList = new ArrayList<>();
    private final Map<String, ApplyFeatureProcessor> strategyMap = new HashMap<>();
    private final Map<String, Set<String>> processedClassActionMap = new HashMap<>();
    private final Map<String, ApplyFeatureProcessingContext> processedClassActionContextMap = new HashMap<>();

    public BoilerplateCodeGenerator(SymbolTable symbolTable, FragmentedText fragmentedText) {
        this.symbolTable = symbolTable;
        this.fragmentedText = fragmentedText;
        registerStrategies();
    }

    private void registerStrategies() {
        strategyMap.put("getter", new GetterFeatureProcessor());
        strategyMap.put("setter", new SetterFeatureProcessor());
        strategyMap.put("equals", new EqualsFeatureProcessor());
        strategyMap.put("equality", new EqualityFeatureProcessor());
        strategyMap.put("constructor", new ConstructorFeatureProcessor());
        strategyMap.put("tostring", new ToStringFeatureProcessor());
        strategyMap.put("hashcode", new HashCodeFeatureProcessor());
        strategyMap.put("builder", new BuilderFeatureProcessor());
        strategyMap.put("data", new DataFeatureProcessor());
    }

    // ---------- Visitor Override ----------

    @Override
    public Void visitApplyDeclaration(ApplyDeclarationContext ctx) {
        if (ctx.applyStatement() != null) {
            handleApplyStatement(ctx.applyStatement());
        } else if (ctx.applyBlock() != null) {
            handleApplyBlock(ctx.applyBlock());
        }
        return null;
    }

    // ---------- Apply Statement Handling ----------

    private void handleApplyStatement(ApplyStatementContext ctx) {
        ApplyStatement applyStatement = getApplyStatement("^TopLevelClass$", ctx.applyFeatureList());
        applyStatementList.add(applyStatement);
    }

    private void handleApplyBlock(ApplyBlockContext ctx) {
        ctx.applyBlockEntry().forEach(entry -> {
            String qualifiedName = Utils.getTokenString(entry.qualifiedName());
            ApplyStatement applyStatement = getApplyStatement(qualifiedName, entry.applyFeatureList());
            applyStatementList.add(applyStatement);
        });
    }

    private ApplyStatement getApplyStatement(String className, ApplyFeatureListContext ctx) {
        ApplyStatement applyStatement = new ApplyStatement(className);
        ctx.applyFeature().forEach(featureCtx -> {
            String action = Utils.getTokenString(featureCtx.identifier());
            ApplyFeature feature = new ApplyFeature(action);

            if (featureCtx.applyFeatureArgs() != null) {
                featureCtx.applyFeatureArgs().identifier()
                        .forEach(idCtx -> feature.addArgument(Utils.getTokenString(idCtx)));
            }

            applyStatement.addApplyFeature(feature);
        });
        return applyStatement;
    }

    // ---------- Code Generation ----------

    public String generate() {
        String topLevelClass = symbolTable.resolve("^TopLevelClass$").getTypeInfo().getName();

        applyStatementList.stream()
                .filter(stmt -> "^TopLevelClass$".equals(stmt.getQualifiedName()))
                .findFirst()
                .ifPresent(stmt -> stmt.setQualifiedName(topLevelClass));

        int baseIndent = 4;
        for (ApplyStatement applyStatement : applyStatementList) {
            String qualifiedName = applyStatement.getQualifiedName();
            String[] classNames = qualifiedName.split("\\.");
            String targetClass = classNames[classNames.length - 1];

            SymbolTable enclosingSymbolTable = symbolTable;
            for (int i = 0; i < classNames.length - 1; ++i) {
                if (symbolTable.resolve(classNames[i]) == null) {
                    throw new IllegalStateException(classNames[i] + " is not found in SymbolTable");
                }
                enclosingSymbolTable = enclosingSymbolTable.getEnclosingSymbolTable(classNames[i]);
            }

            SymbolInfo symbolInfo = enclosingSymbolTable.resolve(targetClass);
            TextChangeRange range = symbolInfo.getRange();
            String classText = symbolInfo.getOriginalText();
            TextChangeRange newRange = new TextChangeRange(
                    range.endLine(), range.inclusiveEndIndex(),
                    range.endLine(), range.inclusiveEndIndex()
            );

            SymbolTable classSymbolTable = enclosingSymbolTable.getEnclosingSymbolTable(targetClass);

            List<String> fieldList = classSymbolTable.findSymbolsByType(List.of(TypeInfo.Type.Primitive, TypeInfo.Type.Reference));
            List<String> primitiveTypeFieldList = classSymbolTable.findSymbolsByType(List.of(TypeInfo.Type.Primitive));
            List<String> referenceTypeFieldList = classSymbolTable.findSymbolsByType(List.of(TypeInfo.Type.Reference));

            if (fieldList.isEmpty()) continue;

            int indent = classSymbolTable.resolve(fieldList.get(0)).getRange().startIndex();
            if (topLevelClass.equals(targetClass)) {
                baseIndent = indent;
            }
            String indentation = Utils.indent(" ", baseIndent);
            String doubleIndentation = Utils.indent(" ", baseIndent * 2);

            symbolInfo = classSymbolTable.resolve(fieldList.get(fieldList.size()-1));
            int constructorIndent = symbolInfo.getRange().startIndex();
            int endLine = symbolInfo.getRange().endLine();
            int endIndex = symbolInfo.getRange().inclusiveEndIndex() + 1;
            TextChangeRange constructorRange = new TextChangeRange(endLine, endIndex, endLine, endIndex);

            ApplyFeatureProcessingContext context = processedClassActionContextMap.computeIfAbsent(qualifiedName, k -> {
                return ApplyFeatureProcessingContext.builder()
                        .targetClass(targetClass)
                        .qualifiedName(qualifiedName)
                        .classSymbolTable(classSymbolTable)
                        .fieldList(fieldList)
                        .primitiveFields(primitiveTypeFieldList)
                        .referenceFields(referenceTypeFieldList)
                        .processedActionList(processedClassActionMap.computeIfAbsent(qualifiedName, key -> new HashSet<>()))
                        .indentation(indentation)
                        .constructorPartText(new StringBuilder())
                        .methodPartText(new StringBuilder())
                        .build();
            });

            for (ApplyFeature feature : applyStatement.getFeatureList()) {
                String action = feature.getAction();
                ApplyFeatureProcessor processor = strategyMap.get(action.toLowerCase());
                if (processor == null) throw new IllegalArgumentException("Unsupported action: " + action + " in feature " + feature + " at class " + qualifiedName);

                context.setFeature(feature);
                processor.process(context);
            }

            String replacedText = Utils.indentLines(context.getMethodPartText(), indent) + Utils.indentLines("\n}", indent - baseIndent);
            fragmentedText.update(newRange, replacedText);

            replacedText = Utils.indentLines(context.getConstructorPartText(), constructorIndent) + "\n";
            fragmentedText.update(constructorRange, replacedText);
        }

        return fragmentedText.toString();
    }
}