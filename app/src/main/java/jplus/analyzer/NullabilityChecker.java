package jplus.analyzer;

import jplus.base.JPlus20Parser;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

public class NullabilityChecker extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable symbolTable = new SymbolTable(null);

    private boolean hasPassed = true;

    private String getTokenString(ParserRuleContext ctx) {
        return ctx.start.getTokenSource().getInputStream().getText(Interval.of(ctx.start.getStartIndex(), ctx.stop.getStopIndex()));
    }

    @Override
    public Void visitLocalVariableDeclaration(JPlus20Parser.LocalVariableDeclarationContext ctx) {
        String typeName = getTokenString(ctx.localVariableType());

        var variableDeclaratorContext = ctx.variableDeclaratorList().variableDeclarator().get(0);
        String variableName = getTokenString(variableDeclaratorContext.variableDeclaratorId());

        String expression = "null";
        if (variableDeclaratorContext.variableInitializer() != null) {
            expression = getTokenString(variableDeclaratorContext.variableInitializer());
        }

        boolean nullable = typeName.endsWith("?");
        TypeInfo typeInfo = new TypeInfo(typeName, nullable, TypeInfo.Type.Unknown);
        symbolTable.declare(variableName, new SymbolInfo(variableName, typeInfo, null, null));

        if (!typeInfo.isNullable() && "null".equals(expression)) {
            int line = ctx.getStart().getLine();
            int column = ctx.getStart().getCharPositionInLine();
            System.out.printf("Error: (line:%d, column:%d) %s is a non-nullable variable. But null value is assigned to it.\n", line, column, variableName);
            hasPassed = false;
        }

        return super.visitLocalVariableDeclaration(ctx);
    }

    @Override
    public Void visitMethodInvocation(JPlus20Parser.MethodInvocationContext ctx) {
        if (ctx.typeName() != null) {
            String instanceName = getTokenString(ctx.typeName());
            String methodName = getTokenString(ctx.identifier());
            boolean nullsafe = ctx.NULLSAFE() != null;

            SymbolInfo symbolInfo = symbolTable.resolve(instanceName);
            if (symbolInfo != null && symbolInfo.getTypeInfo().isNullable() && !nullsafe) {
                int line = ctx.getStart().getLine();
                int column = ctx.getStart().getCharPositionInLine();
                System.out.printf("Error: (line:%d, column:%d) %s is a nullable variable. But it direct accesses to %s(). You must consider to use null-safe operator(?.)\n", line, column, instanceName, methodName);
                hasPassed = false;
            }
        } else {
            ;
        }

        return super.visitMethodInvocation(ctx);
    }

    @Override
    public Void visitPrimaryNoNewArray(JPlus20Parser.PrimaryNoNewArrayContext ctx) {
        if (ctx.typeName() != null) {
            String instanceName = getTokenString(ctx.typeName());
            String methodName = getTokenString(ctx.identifier());
            boolean nullsafe = ctx.NULLSAFE() != null;

            SymbolInfo symbolInfo = symbolTable.resolve(instanceName);
            if (symbolInfo != null && symbolInfo.getTypeInfo().isNullable() && !nullsafe) {
                int line = ctx.getStart().getLine();
                int column = ctx.getStart().getCharPositionInLine();
                System.out.printf("Error: (line:%d, column:%d) %s is a nullable variable. But it direct accesses to %s(). You must consider to use null-safe operator(?.)\n", line, column, instanceName, methodName);
                hasPassed = false;
            }
        } else {
            ;
        }

        return super.visitPrimaryNoNewArray(ctx);
    }

    public boolean hasPassed() {
        return hasPassed;
    }
}
