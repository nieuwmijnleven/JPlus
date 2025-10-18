package jplus.generator;

import jplus.base.JPlus20Parser.*;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.util.FragmentedText;
import jplus.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BoilerplateCodeGenerator extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable symbolTable;
    private final List<ApplyStatement> applyStatementList = new ArrayList<>();
    private final CodeGeneratorContext codeGeneratorCtx = CodeGeneratorContext.getInstance();

    // ---------- Nested Static Classes ----------

    public static class ApplyFeature {
        private final String action;
        private final List<String> argumentList;

        public ApplyFeature(String action) {
            this.action = action;
            this.argumentList = new ArrayList<>();
        }

        public ApplyFeature(String action, List<String> argumentList) {
            this(action);
            this.argumentList.addAll(argumentList);
        }

        public void addArgument(String argument) {
            argumentList.add(argument);
        }

        public String getAction() {
            return action;
        }

        public List<String> getArgumentList() {
            return Collections.unmodifiableList(argumentList);
        }

        @Override
        public String toString() {
            return "ApplyFeature{" +
                    "action='" + action + '\'' +
                    ", argumentList=" + argumentList +
                    '}';
        }
    }

    public static class ApplyStatement {
        private String qualifiedName;
        private final List<ApplyFeature> featureList;

        public ApplyStatement(String qualifiedName) {
            this.qualifiedName = qualifiedName;
            this.featureList = new ArrayList<>();
        }

        public ApplyStatement(String qualifiedName, List<ApplyFeature> featureList) {
            this(qualifiedName);
            this.featureList.addAll(featureList);
        }

        public void addApplyFeature(ApplyFeature feature) {
            featureList.add(feature);
        }

        public void setQualifiedName(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public String getQualifiedName() {
            return qualifiedName;
        }

        public List<ApplyFeature> getFeatureList() {
            return Collections.unmodifiableList(featureList);
        }

        @Override
        public String toString() {
            return "ApplyStatement{" +
                    "qualifiedName='" + qualifiedName + '\'' +
                    ", featureList=" + featureList +
                    '}';
        }
    }

    // ---------- Constructor ----------

    public BoilerplateCodeGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
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
        ApplyStatement applyStatement = getApplyStatement("TopLevelClass", ctx.applyFeatureList());
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
        String topLevelClass = symbolTable.resolve("TopLevelClass").getTypeInfo().getName();

        applyStatementList.stream()
                .filter(stmt -> "TopLevelClass".equals(stmt.getQualifiedName()))
                .findFirst()
                .ifPresent(stmt -> stmt.setQualifiedName(topLevelClass));

        AtomicInteger baseIndent = new AtomicInteger(0);
        FragmentedText fragmentedText = codeGeneratorCtx.getFragmentedText();

        for (ApplyStatement applyStatement : applyStatementList) {
            String qualifiedName = applyStatement.qualifiedName;
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

            List<String> fieldList = new ArrayList<>();
            fieldList.addAll(classSymbolTable.findSymbolsByType(TypeInfo.Type.Primitive));
            fieldList.addAll(classSymbolTable.findSymbolsByType(TypeInfo.Type.Reference));

            if (fieldList.isEmpty()) continue;

            int indent = classSymbolTable.resolve(fieldList.get(0)).getRange().startIndex();
            if (topLevelClass.equals(targetClass)) {
                baseIndent.set(indent);
            }

            for (ApplyFeature feature : applyStatement.featureList) {
                String action = feature.action;
                List<String> arguments = feature.argumentList;

                StringBuilder sb = new StringBuilder();

                if ("getter".equalsIgnoreCase(action)) {
                    for (String field : fieldList) {
                        TypeInfo typeInfo = classSymbolTable.resolve(field).getTypeInfo();
                        String typeName = typeInfo.getName();

                        sb.append("\n")
                                .append(typeName).append(" get").append(Utils.convertToCammel(field)).append("() {\n")
                                .append("\treturn ").append(Utils.convertToCammel(field)).append(";\n")
                                .append("}\n");
                    }

                    String replacedText = sb.toString().indent(indent) + "}".indent(indent - baseIndent.get());
                    fragmentedText.update(newRange, replacedText);
                }

                // TODO: implement setter, builder, etc.
            }
        }

        return fragmentedText.toString();
    }
}