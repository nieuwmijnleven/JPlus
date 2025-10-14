package jplus.analyzer;

import jplus.base.JPlus20Parser;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.base.VariableInfo;

public class NullabilityChecker extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable symbolTable = new SymbolTable();

    private boolean hasPassed = true;

    @Override
    public Void visitLocalVariableDeclaration(JPlus20Parser.LocalVariableDeclarationContext ctx) {
        String typeName = ctx.localVariableType().getText();

        var variableDeclaratorContext = ctx.variableDeclaratorList().variableDeclarator().get(0);
        String variableName = variableDeclaratorContext.variableDeclaratorId().getText();

        String expression = "null";
        if (variableDeclaratorContext.variableInitializer() != null) {
            expression = variableDeclaratorContext.variableInitializer().getText();
        }

        boolean nullable = typeName.endsWith("?");
        TypeInfo typeInfo = new TypeInfo(typeName, nullable);
        VariableInfo variableInfo = new VariableInfo(typeInfo, variableName);
        symbolTable.declare(variableName, variableInfo);

        if (!variableInfo.isNullable() && "null".equals(expression)) {
            int line = ctx.getStart().getLine();
            int column = ctx.getStart().getCharPositionInLine();
            System.out.printf("Error: (line:%d, column:%d) %s is a non-nullable variable. But null value is assigned to it.\n", line, column, variableInfo.getName());
            hasPassed = false;
        }

        return super.visitLocalVariableDeclaration(ctx);
    }

    @Override
    public Void visitMethodInvocation(JPlus20Parser.MethodInvocationContext ctx) {
        if (ctx.typeName() != null) {
            String instanceName = ctx.typeName().getText();
            String methodName = ctx.identifier().getText();
            boolean nullsafe = ctx.NULLSAFE() != null;

            VariableInfo variableInfo = symbolTable.resolve(instanceName);
            if (variableInfo != null && variableInfo.isNullable() && !nullsafe) {
                int line = ctx.getStart().getLine();
                int column = ctx.getStart().getCharPositionInLine();
                System.out.printf("Error: (line:%d, column:%d) %s is a nullable variable. But it direct accesses to %s(). You must consider to use null-safe operator(?.)\n", line, column, variableInfo.getName(), methodName);
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
            String instanceName = ctx.typeName().getText();
            String methodName = ctx.identifier().getText();
            boolean nullsafe = ctx.NULLSAFE() != null;

            VariableInfo variableInfo = symbolTable.resolve(instanceName);
            if (variableInfo != null && variableInfo.isNullable() && !nullsafe) {
                int line = ctx.getStart().getLine();
                int column = ctx.getStart().getCharPositionInLine();
                System.out.printf("Error: (line:%d, column:%d) %s is a nullable variable. But it direct accesses to %s(). You must consider to use null-safe operator(?.)\n", line, column, variableInfo.getName(), methodName);
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
