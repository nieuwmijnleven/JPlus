package jplus.analyzer;

import jplus.base.JPlus20Parser;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.util.Utils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;

public class NullabilityChecker extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable symbolTable = new SymbolTable(null);

    private boolean hasPassed = true;

    // 새로 추가: 문제 정보를 담는 클래스
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

    private final List<NullabilityIssue> issues = new ArrayList<>();

    public List<NullabilityIssue> getIssues() {
        return issues;
    }

    @Override
    public Void visitLocalVariableDeclaration(JPlus20Parser.LocalVariableDeclarationContext ctx) {
        String typeName = Utils.getTokenString(ctx.localVariableType());

        var variableDeclaratorContext = ctx.variableDeclaratorList().variableDeclarator().get(0);
        String variableName = Utils.getTokenString(variableDeclaratorContext.variableDeclaratorId());

        String expression = "null";
        if (variableDeclaratorContext.variableInitializer() != null) {
            expression = Utils.getTokenString(variableDeclaratorContext.variableInitializer());
        }

        boolean nullable = typeName.endsWith("?");
        TypeInfo typeInfo = new TypeInfo(typeName, nullable, TypeInfo.Type.Unknown);
        symbolTable.declare(variableName, new SymbolInfo(variableName, typeInfo, null, null));

        if (!typeInfo.isNullable() && "null".equals(expression)) {
            int line = ctx.getStart().getLine();
            int column = ctx.getStart().getCharPositionInLine();
            int offset = ctx.getStart().getStartIndex();
            String msg = variableName + " is a non-nullable variable. But null value is assigned to it.";
            issues.add(new NullabilityIssue(line, column, offset, msg));
            hasPassed = false;
        }

        return super.visitLocalVariableDeclaration(ctx);
    }

    @Override
    public Void visitMethodInvocation(JPlus20Parser.MethodInvocationContext ctx) {
        if (ctx.typeName() != null) {
            String instanceName = Utils.getTokenString(ctx.typeName());
            String methodName = Utils.getTokenString(ctx.identifier());
            boolean nullsafe = ctx.NULLSAFE() != null;

            SymbolInfo symbolInfo = symbolTable.resolve(instanceName);
            if (symbolInfo != null && symbolInfo.getTypeInfo().isNullable() && !nullsafe) {
                int line = ctx.getStart().getLine();
                int column = ctx.getStart().getCharPositionInLine();
                int offset = ctx.getStart().getStartIndex();
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
            String instanceName = Utils.getTokenString(ctx.typeName());
            String methodName = Utils.getTokenString(ctx.identifier());
            boolean nullsafe = ctx.NULLSAFE() != null;

            SymbolInfo symbolInfo = symbolTable.resolve(instanceName);
            if (symbolInfo != null && symbolInfo.getTypeInfo().isNullable() && !nullsafe) {
                int line = ctx.getStart().getLine();
                int column = ctx.getStart().getCharPositionInLine();
                int offset = ctx.getStart().getStartIndex();
                String msg = instanceName + " is a nullable variable. But it directly accesses " + methodName + "(). Consider using null-safe operator(?.).";
                issues.add(new NullabilityIssue(line, column, offset, msg));
                hasPassed = false;
            }
        }
        return super.visitPrimaryNoNewArray(ctx);
    }

    public boolean hasPassed() {
        return hasPassed;
    }
}
