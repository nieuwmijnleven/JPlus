package jplus.analyzer;

import jplus.base.JPlus20Parser;
import jplus.base.JPlus20ParserBaseVisitor;
import jplus.base.Modifier;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.generator.TextChangeRange;
import jplus.util.Utils;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolAnalyzer extends JPlus20ParserBaseVisitor<Void> {

    private final SymbolTable topLevelSymbolTable = new SymbolTable(null);
    private SymbolTable currentSymbolTable = topLevelSymbolTable;

    private String originalText;

    public SymbolTable getTopLevelSymbolTable() {
        return topLevelSymbolTable;
    }

    @Override
    public Void visitStart_(JPlus20Parser.Start_Context ctx) {
//        System.out.println("[visitStart_] startIndex = " + ctx.start.getStartIndex());
//        System.out.println("[visitStart_] stopIndex = " + ctx.stop.getStopIndex());
//        System.out.println("length = " + ctx.start.getInputStream().toString().length());
//        System.out.println("text = " + ctx.start.getInputStream().toString());
        this.originalText = ctx.start.getInputStream().toString();
        return super.visitStart_(ctx);
    }

    @Override
    public Void visitTopLevelClassOrInterfaceDeclaration(JPlus20Parser.TopLevelClassOrInterfaceDeclarationContext ctx) {
        if (ctx.classDeclaration() != null) {
            String className = Utils.getTokenString(ctx.classDeclaration().normalClassDeclaration().typeIdentifier());
            TypeInfo typeInfo = new TypeInfo(className, false, TypeInfo.Type.Class);
            TextChangeRange range = Utils.getTextChangeRange(this.originalText, ctx);
            String rangeText = Utils.getTokenString(ctx);
            SymbolInfo symbolInfo = new SymbolInfo(className, typeInfo, range, rangeText);
            currentSymbolTable.declare("^TopLevelClass$", symbolInfo); //TopLevelClass
        } else if (ctx.interfaceDeclaration() != null) {

        }
        return super.visitTopLevelClassOrInterfaceDeclaration(ctx);
    }

    @Override
    public Void visitClassDeclaration(JPlus20Parser.ClassDeclarationContext ctx) {
        if (ctx.normalClassDeclaration() != null) {
            String className = Utils.getTokenString(ctx.normalClassDeclaration().typeIdentifier());
            TypeInfo typeInfo = new TypeInfo(className, false, TypeInfo.Type.Class);
            TextChangeRange range = Utils.getTextChangeRange(this.originalText, ctx);
            String rangeText = Utils.getTokenString(ctx);

            List<Modifier> modifierList = new ArrayList<>();
            if (ctx.normalClassDeclaration().classModifier() != null) {
                for (var classModifierContext : ctx.normalClassDeclaration().classModifier()) {
                    modifierList.add(Modifier.valueOf(Utils.getTokenString(classModifierContext).toUpperCase()));
                }
            }

            SymbolInfo symbolInfo = new SymbolInfo(className, typeInfo, range, rangeText, modifierList);
            SymbolTable classSymbolTable = currentSymbolTable.getEnclosingSymbolTable(className);
            symbolInfo.setSymbolTable(classSymbolTable);
            currentSymbolTable.declare(className, symbolInfo);
            currentSymbolTable = classSymbolTable;
        } else if (ctx.enumDeclaration() != null) {

        } else if (ctx.recordDeclaration() != null) {

        }

        super.visitClassDeclaration(ctx);
        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }

    @Override
    public Void visitConstructorDeclaration(JPlus20Parser.ConstructorDeclarationContext ctx) {
        SymbolTable constructorSymbolTable = new SymbolTable(currentSymbolTable);
        List<String> typeNameList = new ArrayList<>();

        var formalParameterList = ctx.constructorDeclarator().formalParameterList().formalParameter();
        for (var formalParameterContext : formalParameterList) {
            List<Modifier> paramModifierList = new ArrayList<>();
            if (formalParameterContext.variableModifier() != null) {
                for (var variableModifierContext : formalParameterContext.variableModifier()) {
                    paramModifierList.add(Modifier.valueOf(Utils.getTokenString(variableModifierContext).toUpperCase()));
                }
            }

            String typeName = Utils.getTokenString(formalParameterContext.unannType());
            String variableName = Utils.getTokenString(formalParameterContext.variableDeclaratorId());
            boolean nullable = formalParameterContext.unannType().QUESTION() == null ? true : false;
            TypeInfo.Type type = formalParameterContext.unannType().unannReferenceType() != null ? TypeInfo.Type.Reference : TypeInfo.Type.Primitive;
            TypeInfo typeInfo = new TypeInfo(typeName, nullable, type);
            TextChangeRange range = Utils.getTextChangeRange(this.originalText, formalParameterContext);
            String rangeText = Utils.getTokenString(formalParameterContext);
            SymbolInfo symbolInfo = new SymbolInfo(variableName, typeInfo, range, rangeText, paramModifierList);
            constructorSymbolTable.declare(variableName, symbolInfo);
            typeNameList.add(typeName);
        }

        List<Modifier> modifierList = new ArrayList<>();
        if (ctx.constructorModifier() != null) {
            for (var constructorModifierContext : ctx.constructorModifier()) {
                modifierList.add(Modifier.valueOf(Utils.getTokenString(constructorModifierContext).toUpperCase()));
            }
        }

        String symbolName = "^constructor$_" + typeNameList.stream().collect(Collectors.joining("_"));
//        System.out.println("constructor = " + symbolName);
        TypeInfo typeInfo = new TypeInfo(symbolName, false, TypeInfo.Type.Constructor);
        TextChangeRange range = Utils.getTextChangeRange(this.originalText, ctx);
        String rangeText = Utils.getTokenString(ctx);
        SymbolInfo symbolInfo = new SymbolInfo(symbolName, typeInfo, range, rangeText, modifierList);
        symbolInfo.setSymbolTable(constructorSymbolTable);
        currentSymbolTable.declare(symbolName, symbolInfo);
        currentSymbolTable = currentSymbolTable.addEnclosingSymbolTable(symbolName, constructorSymbolTable);
        super.visitConstructorDeclaration(ctx);
        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }

    @Override
    public Void visitFieldDeclaration(JPlus20Parser.FieldDeclarationContext ctx) {
        List<Modifier> modifierList = new ArrayList<>();
        if (ctx.fieldModifier() != null) {
            for (var fieldModifierContext : ctx.fieldModifier()) {
                modifierList.add(Modifier.valueOf(Utils.getTokenString(fieldModifierContext).toUpperCase()));
            }
        }

        if (ctx.unannType().unannReferenceType() != null) {
            String typeName = Utils.getTokenString(ctx.unannType().unannReferenceType().unannClassOrInterfaceType().typeIdentifier());
            boolean nullable = ctx.unannType().QUESTION() != null ? true : false;
            TypeInfo typeInfo = new TypeInfo(typeName, nullable, TypeInfo.Type.Reference);
            TextChangeRange range = Utils.getTextChangeRange(this.originalText, ctx);
            String rangeText = Utils.getTokenString(ctx);

            for (var variableDeclaratorContext : ctx.variableDeclaratorList().variableDeclarator()) {
                String name = Utils.getTokenString(variableDeclaratorContext.variableDeclaratorId().identifier());
                SymbolInfo symbolInfo = new SymbolInfo(name, typeInfo, range, rangeText, modifierList);
                currentSymbolTable.declare(name, symbolInfo);
            }
        } else if (ctx.unannType().unannPrimitiveType() != null) {
            String typeName = Utils.getTokenString(ctx.unannType().unannPrimitiveType());
            TypeInfo typeInfo = new TypeInfo(typeName, false, TypeInfo.Type.Primitive);
            TextChangeRange range = Utils.getTextChangeRange(this.originalText, ctx);
            String rangeText = Utils.getTokenString(ctx);

            for (var variableDeclaratorContext : ctx.variableDeclaratorList().variableDeclarator()) {
                String name = Utils.getTokenString(variableDeclaratorContext.variableDeclaratorId().identifier());
                SymbolInfo symbolInfo = new SymbolInfo(name, typeInfo, range, rangeText, modifierList);
                currentSymbolTable.declare(name, symbolInfo);
            }
        }

        return super.visitFieldDeclaration(ctx);
    }

//    @Override
//    public Void visitMethodDeclaration(JPlus20Parser.MethodDeclarationContext ctx) {
//        String typeName = Utils.getTokenString(ctx.methodHeader().methodDeclarator().identifier());
//        TypeInfo typeInfo = new TypeInfo(typeName, false, TypeInfo.Type.Method);
//        TextChangeRange range = Utils.getTextChangeRange(this.originalText, ctx);
//        String rangeText = Utils.getTokenString(ctx);
//
//        List<Modifier> modifierList = new ArrayList<>();
//        if (ctx.methodModifier() != null) {
//            for (var methodModifierContext : ctx.methodModifier()) {
//                modifierList.add(Modifier.valueOf(Utils.getTokenString(methodModifierContext).toUpperCase()));
//            }
//        }
//
//        SymbolInfo symbolInfo = new SymbolInfo(typeName, typeInfo, range, rangeText);
//        SymbolTable methodSymbolTable = currentSymbolTable.getEnclosingSymbolTable(typeName);
//        symbolInfo.setSymbolTable(methodSymbolTable);
//        currentSymbolTable.declare(typeName, symbolInfo);
//
//        currentSymbolTable = methodSymbolTable;
//        super.visitMethodDeclaration(ctx);
//        currentSymbolTable = currentSymbolTable.getParent();
//        return null;
//    }


    @Override
    public Void visitMethodDeclaration(JPlus20Parser.MethodDeclarationContext ctx) {
        SymbolTable methodSymbolTable = new SymbolTable(currentSymbolTable);
        List<String> typeNameList = new ArrayList<>();

        var methodDeclarator = ctx.methodHeader().methodDeclarator();
        var formalParameterList = methodDeclarator.formalParameterList() != null ? methodDeclarator.formalParameterList().formalParameter() : new ArrayList<JPlus20Parser.FormalParameterContext>();
        for (var formalParameterContext : formalParameterList) {
            List<Modifier> paramModifierList = new ArrayList<>();
            if (formalParameterContext.variableModifier() != null) {
                for (var variableModifierContext : formalParameterContext.variableModifier()) {
                    paramModifierList.add(Modifier.valueOf(Utils.getTokenString(variableModifierContext).toUpperCase()));
                }
            }

            String typeName = Utils.getTokenString(formalParameterContext.unannType());
            String variableName = Utils.getTokenString(formalParameterContext.variableDeclaratorId());
            boolean nullable = formalParameterContext.unannType().QUESTION() == null ? true : false;
            TypeInfo.Type type = formalParameterContext.unannType().unannReferenceType() != null ? TypeInfo.Type.Reference : TypeInfo.Type.Primitive;
            TypeInfo typeInfo = new TypeInfo(typeName, nullable, type);
            TextChangeRange range = Utils.getTextChangeRange(this.originalText, formalParameterContext);
            String rangeText = Utils.getTokenString(formalParameterContext);
            SymbolInfo symbolInfo = new SymbolInfo(variableName, typeInfo, range, rangeText, paramModifierList);
            methodSymbolTable.declare(variableName, symbolInfo);
            typeNameList.add(typeName);
        }

        List<Modifier> modifierList = new ArrayList<>();
        if (ctx.methodModifier() != null) {
            for (var methodModifierContext : ctx.methodModifier()) {
                modifierList.add(Modifier.valueOf(Utils.getTokenString(methodModifierContext).toUpperCase()));
            }
        }

        String methodName = Utils.getTokenString(ctx.methodHeader().methodDeclarator().identifier());
        String symbolName = "^" + methodName + "$_" + typeNameList.stream().collect(Collectors.joining("_"));
//        System.out.println("method = " + symbolName);
        TypeInfo typeInfo = new TypeInfo(symbolName, false, TypeInfo.Type.Method);
        TextChangeRange range = Utils.getTextChangeRange(this.originalText, ctx);
        String rangeText = Utils.getTokenString(ctx);
        SymbolInfo symbolInfo = new SymbolInfo(symbolName, typeInfo, range, rangeText, modifierList);
        symbolInfo.setSymbolTable(methodSymbolTable);
        currentSymbolTable.declare(symbolName, symbolInfo);
        currentSymbolTable = currentSymbolTable.addEnclosingSymbolTable(symbolName, methodSymbolTable);
        super.visitMethodDeclaration(ctx);
        currentSymbolTable = currentSymbolTable.getParent();
        return null;
    }
}
