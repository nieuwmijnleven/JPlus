package jplus.analyzer;

import jplus.base.JPlus20Parser;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.Modifier;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.generator.TextChangeRange;
import jplus.util.Utils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NullabilityChecker extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable topLevelSymbolTable;
    private SymbolTable currentSymbolTable;
    private String originalText;
    private boolean hasPassed = true;

    public static class NullabilityIssue {
        private final int line;
        private final int column;
        private final int offset;
        private final String message;

        public NullabilityIssue(int line, int column, int offset, String message) {
            this.line = line;
            this.column = column;
            this.offset = offset;
            this.message = message;
        }

        public int getLine() { return line; }
        public int getColumn() { return column; }
        public int getOffset() { return offset; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("Error: (line:%d, column:%d) %s", line, column, message);
        }
    }

    public NullabilityChecker(SymbolTable symbolTable) {
        this.topLevelSymbolTable = symbolTable;
        this.currentSymbolTable = symbolTable;
    }

    private final List<NullabilityIssue> issues = new ArrayList<>();

    public List<NullabilityIssue> getIssues() {
        return issues;
    }

    @Override
    public Void visitStart_(JPlus20Parser.Start_Context ctx) {
        this.originalText = ctx.start.getInputStream().toString();
        return super.visitStart_(ctx);
    }

    @Override
    public Void visitClassDeclaration(JPlus20Parser.ClassDeclarationContext ctx) {
        if (ctx.normalClassDeclaration() != null) {
            String className = Utils.getTokenString(ctx.normalClassDeclaration().typeIdentifier());
            currentSymbolTable = currentSymbolTable.getEnclosingSymbolTable(className);
        } else if (ctx.enumDeclaration() != null) {

        } else if (ctx.recordDeclaration() != null) {

        }

        super.visitClassDeclaration(ctx);
        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }

    @Override
    public Void visitConstructorDeclaration(JPlus20Parser.ConstructorDeclarationContext ctx) {
        List<String> typeNameList = new ArrayList<>();
        var formalParameterList = ctx.constructorDeclarator().formalParameterList().formalParameter();
        for (var formalParameterContext : formalParameterList) {
            String typeName = Utils.getTokenString(formalParameterContext.unannType());
            typeNameList.add(typeName);
        }

        String symbolName = "^constructor$_" + typeNameList.stream().collect(Collectors.joining("_"));
        currentSymbolTable = currentSymbolTable.getEnclosingSymbolTable(symbolName);
        super.visitConstructorDeclaration(ctx);
        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }

    @Override
    public Void visitMethodDeclaration(JPlus20Parser.MethodDeclarationContext ctx) {
        List<String> typeNameList = new ArrayList<>();
        var methodDeclarator = ctx.methodHeader().methodDeclarator();
        var formalParameterList = methodDeclarator.formalParameterList() != null ? methodDeclarator.formalParameterList().formalParameter() : new ArrayList<JPlus20Parser.FormalParameterContext>();
        for (var formalParameterContext : formalParameterList) {
            String typeName = Utils.getTokenString(formalParameterContext.unannType());
            typeNameList.add(typeName);
        }

        String methodName = Utils.getTokenString(ctx.methodHeader().methodDeclarator().identifier());
        String symbolName = "^" + methodName + "$_" + typeNameList.stream().collect(Collectors.joining("_"));

        currentSymbolTable = currentSymbolTable.getEnclosingSymbolTable(symbolName);
        super.visitMethodDeclaration(ctx);
        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }

    @Override
    public Void visitFieldDeclaration(JPlus20Parser.FieldDeclarationContext ctx) {
        for (var variableDeclaratorContext : ctx.variableDeclaratorList().variableDeclarator()) {
            String symbol = Utils.getTokenString(variableDeclaratorContext.variableDeclaratorId());
            SymbolInfo symbolInfo = currentSymbolTable.resolve(symbol);
            TypeInfo typeInfo = symbolInfo.getTypeInfo();

            if (variableDeclaratorContext.variableInitializer() != null) {
                String expression = Utils.getTokenString(variableDeclaratorContext.variableInitializer());

                if (!typeInfo.isNullable() && "null".equals(expression)) {
                    int line = ctx.getStart().getLine();
                    int column = ctx.getStart().getCharPositionInLine();
                    int offset = ctx.getStart().getStartIndex();
                    String msg = symbol + " is a non-nullable variable. But null value is assigned to it.";
                    issues.add(new NullabilityIssue(line, column, offset, msg));
                    hasPassed = false;
                }
            }
        }
        return super.visitFieldDeclaration(ctx);
    }

    @Override
    public Void visitBlock(JPlus20Parser.BlockContext ctx) {
        currentSymbolTable = currentSymbolTable.getEnclosingSymbolTable("^block$");
        super.visitBlock(ctx);
        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(JPlus20Parser.LocalVariableDeclarationContext ctx) {
        var variableDeclarator = ctx.variableDeclaratorList().variableDeclarator();
        for (JPlus20Parser.VariableDeclaratorContext variableDeclaratorContext : variableDeclarator) {
            String symbol = Utils.getTokenString(variableDeclaratorContext.variableDeclaratorId());
            SymbolInfo symbolInfo = currentSymbolTable.resolve(symbol);
            if (symbolInfo != null) {
                TypeInfo typeInfo = symbolInfo.getTypeInfo();
                if (variableDeclaratorContext.variableInitializer() != null) {
                    String expression = Utils.getTokenString(variableDeclaratorContext.variableInitializer());
                    if (!typeInfo.isNullable() && typeInfo.getType().equals(TypeInfo.Type.Reference) && "null".equals(expression)) {
                        int line = ctx.getStart().getLine();
                        int column = ctx.getStart().getCharPositionInLine();
                        int offset = ctx.getStart().getStartIndex();
                        String msg = symbol + " is a non-nullable variable. But null value is assigned to it.";
                        issues.add(new NullabilityIssue(line, column, offset, msg));
                        hasPassed = false;
                    }
                } else {
                    if (!typeInfo.isNullable() && typeInfo.getType().equals(TypeInfo.Type.Reference)) {
                        int line = ctx.getStart().getLine();
                        int column = ctx.getStart().getCharPositionInLine();
                        int offset = ctx.getStart().getStartIndex();
                        String msg = symbol + " is a non-nullable variable. But null value is assigned to it.";
                        issues.add(new NullabilityIssue(line, column, offset, msg));
                        hasPassed = false;
                    }
                }
            }
        }

        return super.visitLocalVariableDeclaration(ctx);
    }

    @Override
    public Void visitAssignment(JPlus20Parser.AssignmentContext ctx) {
        String fullVariableName = null;
        if (ctx.leftHandSide().expressionName() != null) {
            fullVariableName = Utils.getTokenString(ctx.leftHandSide().expressionName());
        } else if (ctx.leftHandSide().fieldAccess() != null) {
            fullVariableName = Utils.getTokenString(ctx.leftHandSide().fieldAccess());
        } else if (ctx.leftHandSide().arrayAccess() != null) {
            fullVariableName = Utils.getTokenString(ctx.leftHandSide().arrayAccess());
        }

        String expression = Utils.getTokenString(ctx.expression());

        SymbolInfo symbolInfo = null;
        String variableName;
        int thisIndex = fullVariableName.indexOf("this");
        if (thisIndex != -1) {
            variableName = fullVariableName.substring(thisIndex + "this".length() + 1);
            symbolInfo = currentSymbolTable.getParent().getParent().resolve(variableName);
        } else {
            symbolInfo = currentSymbolTable.resolve(fullVariableName);
        }

        if (symbolInfo != null) {
            TypeInfo typeInfo = symbolInfo.getTypeInfo();
            if (!typeInfo.isNullable() && typeInfo.getType().equals(TypeInfo.Type.Reference)) {
                if ("null".equals(expression)) {
                    int line = ctx.getStart().getLine();
                    int column = ctx.getStart().getCharPositionInLine();
                    int offset = ctx.getStart().getStartIndex();
                    String msg = fullVariableName + " is a non-nullable variable. But null value is assigned to it.";
                    issues.add(new NullabilityIssue(line, column, offset, msg));
                    hasPassed = false;
                } else {
                    SymbolInfo rhsSymbolInfo = currentSymbolTable.resolve(expression);
                    if (rhsSymbolInfo != null) {
                        TypeInfo rhsTypeInfo = rhsSymbolInfo.getTypeInfo();
                        if (typeInfo.getType().equals(TypeInfo.Type.Reference) && rhsTypeInfo.getType().equals(TypeInfo.Type.Reference) && !typeInfo.isNullable && rhsTypeInfo.isNullable) {
                            int line = ctx.getStart().getLine();
                            int column = ctx.getStart().getCharPositionInLine();
                            int offset = ctx.getStart().getStartIndex();
                            String msg = "cannot assign " + expression + "(nullable) to " + fullVariableName + "(non-nullable).";
                            issues.add(new NullabilityIssue(line, column, offset, msg));
                            hasPassed = false;
                        }
                    }
                }
            }
        }

        return super.visitAssignment(ctx);
    }

    @Override
    public Void visitEqualityExpression(JPlus20Parser.EqualityExpressionContext ctx) {
        if (ctx.NOTEQUAL() != null) {
            String variableName = Utils.getTokenString(ctx.equalityExpression());
            String value = Utils.getTokenString(ctx.relationalExpression());
            if ("null".equals(value)) {
                SymbolInfo symbolInfo = currentSymbolTable.resolve(variableName);
                if (symbolInfo != null) {
                    TypeInfo typeInfo = symbolInfo.getTypeInfo();
                    if (typeInfo.isNullable) {
                        TypeInfo newTypeInfo = TypeInfo.copyOf(typeInfo);
                        newTypeInfo.setNullable(false);

                        SymbolInfo newSymbolInfo = SymbolInfo.copyOf(symbolInfo);
                        newSymbolInfo.setTypeInfo(newTypeInfo);

                        currentSymbolTable.declare(variableName, newSymbolInfo);
                    }
                }
            }
        }

        return super.visitEqualityExpression(ctx);
    }

    @Override
    public Void visitMethodInvocation(JPlus20Parser.MethodInvocationContext ctx) {
        if (ctx.typeName() != null) {
            String instanceName = Utils.getTokenString(ctx.typeName());
            String methodName = Utils.getTokenString(ctx.identifier());
            boolean nullsafe = ctx.NULLSAFE() != null;

            SymbolInfo symbolInfo = currentSymbolTable.resolve(instanceName);
            if (symbolInfo != null && symbolInfo.getTypeInfo().isNullable() && !nullsafe) {
                int line = ctx.start.getLine();
                int column = ctx.start.getCharPositionInLine();
                int offset = ctx.start.getStartIndex();
                String msg = instanceName + " is a nullable variable. But it directly accesses " + methodName + "(). Consider using null-safe operator(?.).";
                issues.add(new NullabilityIssue(line, column, offset, msg));
                hasPassed = false;
            }
        }
        return super.visitMethodInvocation(ctx);
    }

    @Override
    public Void visitPrimaryNoNewArray(JPlus20Parser.PrimaryNoNewArrayContext ctx) {
        if (ctx.typeName() != null) {
            if (ctx.THIS() == null) {
                String instanceName = Utils.getTokenString(ctx.typeName());
                String methodName = Utils.getTokenString(ctx.identifier());
                boolean nullsafe = ctx.NULLSAFE() != null;

                SymbolInfo symbolInfo = currentSymbolTable.resolve(instanceName);
                if (symbolInfo != null && symbolInfo.getTypeInfo().isNullable() && !nullsafe) {
                    int line = ctx.getStart().getLine();
                    int column = ctx.getStart().getCharPositionInLine();
                    int offset = ctx.getStart().getStartIndex();
                    String msg = instanceName + " is a nullable variable. But it directly accesses " + methodName + "(). Consider using null-safe operator(?.).";
                    issues.add(new NullabilityIssue(line, column, offset, msg));
                    hasPassed = false;
                }
            }
        }
        return super.visitPrimaryNoNewArray(ctx);
    }

    public boolean hasPassed() {
        return hasPassed;
    }
}
