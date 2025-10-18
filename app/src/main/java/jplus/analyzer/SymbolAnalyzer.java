package jplus.analyzer;

import jplus.base.JPlus20Parser;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.generator.TextChangeRange;
import jplus.util.Utils;

public class SymbolAnalyzer extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable topLevelSymbolTable = new SymbolTable(null);
    private SymbolTable currentSymbolTable = topLevelSymbolTable;

    public SymbolTable getTopLevelSymbolTable() {
        return topLevelSymbolTable;
    }

    @Override
    public Void visitCompilationUnit(JPlus20Parser.CompilationUnitContext ctx) {
        return super.visitCompilationUnit(ctx);
    }

    @Override
    public Void visitTopLevelClassOrInterfaceDeclaration(JPlus20Parser.TopLevelClassOrInterfaceDeclarationContext ctx) {
        if (ctx.classDeclaration() != null) {
            String className = Utils.getTokenString(ctx.classDeclaration().normalClassDeclaration().typeIdentifier());
            TypeInfo typeInfo = new TypeInfo(className, false, TypeInfo.Type.Class);
            TextChangeRange range = Utils.getTextChangeRange(ctx);
            String rangeText = Utils.getTokenString(ctx);
            SymbolInfo symbolInfo = new SymbolInfo(typeInfo, range, rangeText);
            currentSymbolTable.declare("TopLevelClass", symbolInfo);
        } else if (ctx.interfaceDeclaration() != null) {

        }
        return super.visitTopLevelClassOrInterfaceDeclaration(ctx);
    }

    @Override
    public Void visitClassDeclaration(JPlus20Parser.ClassDeclarationContext ctx) {
        if (ctx.normalClassDeclaration() != null) {
            String className = Utils.getTokenString(ctx.normalClassDeclaration().typeIdentifier());
            TypeInfo typeInfo = new TypeInfo(className, false, TypeInfo.Type.Class);
            TextChangeRange range = Utils.getTextChangeRange(ctx);
            String rangeText = Utils.getTokenString(ctx);
            SymbolInfo symbolInfo = new SymbolInfo(typeInfo, range, rangeText);
            currentSymbolTable.declare(className, symbolInfo);
            currentSymbolTable = currentSymbolTable.addEnclosingSymbolTable(className, new SymbolTable(currentSymbolTable));
        } else if (ctx.enumDeclaration() != null) {

        } else if (ctx.recordDeclaration() != null) {

        }

        super.visitClassDeclaration(ctx);

        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }

    @Override
    public Void visitFieldDeclaration(JPlus20Parser.FieldDeclarationContext ctx) {
        if (ctx.unannType().unannReferenceType() != null) {
            String typeName = Utils.getTokenString(ctx.unannType().unannReferenceType().unannClassOrInterfaceType().typeIdentifier());
            boolean nullable = ctx.unannType().QUESTION() != null ? true : false;
            TypeInfo typeInfo = new TypeInfo(typeName, nullable, TypeInfo.Type.Reference);
            TextChangeRange range = Utils.getTextChangeRange(ctx);
            String rangeText = Utils.getTokenString(ctx);

            ctx.variableDeclaratorList().variableDeclarator().forEach(variableDeclaratorContext -> {
                String name = Utils.getTokenString(variableDeclaratorContext.variableDeclaratorId().identifier());
                SymbolInfo symbolInfo = new SymbolInfo(typeInfo, range, rangeText);
                currentSymbolTable.declare(name, symbolInfo);
            });
        } else if (ctx.unannType().unannPrimitiveType() != null) {
            String typeName = Utils.getTokenString(ctx.unannType().unannPrimitiveType());
            TypeInfo typeInfo = new TypeInfo(typeName, false, TypeInfo.Type.Primitive);
            TextChangeRange range = Utils.getTextChangeRange(ctx);
            String rangeText = Utils.getTokenString(ctx);

            ctx.variableDeclaratorList().variableDeclarator().forEach(variableDeclaratorContext -> {
                String name = Utils.getTokenString(variableDeclaratorContext.variableDeclaratorId().identifier());
                SymbolInfo symbolInfo = new SymbolInfo(typeInfo, range, rangeText);
                currentSymbolTable.declare(name, symbolInfo);
            });
        }

        return super.visitFieldDeclaration(ctx);
    }

    @Override
    public Void visitMethodDeclaration(JPlus20Parser.MethodDeclarationContext ctx) {
        String typeName = Utils.getTokenString(ctx.methodHeader().methodDeclarator().identifier());
        TypeInfo typeInfo = new TypeInfo(typeName, false, TypeInfo.Type.Method);
        TextChangeRange range = Utils.getTextChangeRange(ctx);
        String rangeText = Utils.getTokenString(ctx);
        SymbolInfo symbolInfo = new SymbolInfo(typeInfo, range, rangeText);
        currentSymbolTable.declare(typeName, symbolInfo);
        currentSymbolTable = currentSymbolTable.addEnclosingSymbolTable(typeName, new SymbolTable(currentSymbolTable));

        super.visitMethodDeclaration(ctx);

        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }
}
