package jplus.generator.apply;

import jplus.base.Modifier;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.generator.TextChangeRange;

import java.util.ArrayList;
import java.util.List;

public class ConstructorFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        List<String> argumentList = context.getFeature().getArgumentList();
        for (String argument : argumentList) {
            if ("no".equalsIgnoreCase(argument)) {
                processNoArgsConsturctor(context);
            } else if ("all".equalsIgnoreCase(argument)) {
                processAllArgsConstructor(context);
            } else if ("required".equalsIgnoreCase(argument)) {
                processRequiredArgsConstructor(context);
            } else {
                throw new IllegalArgumentException(argument + "is not valid");
            }
        }

//        ApplyFeatureProcessor.super.process(context);
    }

    private void processNoArgsConsturctor(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("constructor(no)")) return;

        String className = context.getTargetClass();
        SymbolTable classSymbolTable = context.getClassSymbolTable();
        if (classSymbolTable.resolve("^constructor$") != null) {
            return;
        }

        List<String> fieldList = context.getFieldList();
        if (fieldList.isEmpty()) return;

        List<String> nonStaticFieldList = fieldList.stream().map(classSymbolTable::resolve).filter(symbolInfo -> !symbolInfo.getModifierList().contains(Modifier.STATIC)).map(SymbolInfo::getSymbol).toList();

        if (nonStaticFieldList.isEmpty()) return;

        SymbolInfo symbolInfo = classSymbolTable.resolve(fieldList.get(fieldList.size()-1));
        int endLine = symbolInfo.getRange().endLine();
        int endIndex = symbolInfo.getRange().inclusiveEndIndex() + 1;
        TextChangeRange range = new TextChangeRange(endLine, endIndex, endLine, endIndex);

        String constructor = "\n\n" + "public " + className + "() {}";
        context.appendConstructorPartText(constructor);
        context.addProcessedAction("constructor(no)");
    }

    public void processAllArgsConstructor(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("constructor(all)")) return;

        List<String> fieldList = context.getFieldList();
        if (fieldList.isEmpty()) return;

        SymbolTable classSymbolTable = context.getClassSymbolTable();
        List<String> nonStaticFieldList = fieldList.stream().map(classSymbolTable::resolve).filter(symbolInfo -> !symbolInfo.getModifierList().contains(Modifier.STATIC)).map(SymbolInfo::getSymbol).toList();
        if (nonStaticFieldList.isEmpty()) return;

        String className = context.getTargetClass();
        String constructorName = "^constructor$_" + String.join("_", fieldList);
        if (classSymbolTable.resolve("constructorName") != null) {
            return;
        }

        SymbolInfo symbolInfo = classSymbolTable.resolve(fieldList.get(fieldList.size()-1));
        int startIndex = symbolInfo.getRange().startIndex();
        int endLine = symbolInfo.getRange().endLine();
        int endIndex = symbolInfo.getRange().inclusiveEndIndex() + 1;
        TextChangeRange range = new TextChangeRange(endLine, endIndex, endLine, endIndex);
        String indentation = context.getIndentation();

        String constructor = "\n\n" + "public " + className + "(";

        List<String> parameters = new ArrayList<>();
        for (String fieldName : nonStaticFieldList) {
            TypeInfo typeInfo = classSymbolTable.resolve(fieldName).getTypeInfo();
            String typeName = typeInfo.getName();
            parameters.add(typeName + " " + fieldName);
        }
        constructor += String.join(", ", parameters);
        constructor += ") {\n";

        List<String> assignments = new ArrayList<>();
        for (String fieldName : nonStaticFieldList) {
            TypeInfo typeInfo = classSymbolTable.resolve(fieldName).getTypeInfo();
            String typeName = typeInfo.getName();
            assignments.add(indentation + "this." + fieldName + " = " + fieldName + ";");
        }
        constructor += String.join("\n", assignments);
        constructor += "\n}";

        context.appendConstructorPartText(constructor);
        context.addProcessedAction("constructor(all)");
    }

    public void processRequiredArgsConstructor(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("constructor(required)")) return;

        List<String> fieldList = context.getFieldList();
        if (fieldList.isEmpty()) return;

        SymbolTable classSymbolTable = context.getClassSymbolTable();
        List<String> nonStaticFieldList = fieldList.stream().map(classSymbolTable::resolve).filter(symbolInfo -> !symbolInfo.getModifierList().contains(Modifier.STATIC)).map(SymbolInfo::getSymbol).toList();
        if (nonStaticFieldList.isEmpty()) return;

        List<String> requiredFieldList = new ArrayList<>();
        for (String fieldName : nonStaticFieldList) {
            SymbolInfo symbolInfo = classSymbolTable.resolve(fieldName);
            List<Modifier> modifierList = symbolInfo.getModifierList();
            if (modifierList != null && modifierList.contains(Modifier.FINAL)) {
                requiredFieldList.add(fieldName);
            }
        }

        if (requiredFieldList.isEmpty()) return;

        String constructorName = "^constructor$_" + String.join("_", fieldList);
        if (classSymbolTable.resolve("constructorName") != null) {
            return;
        }

        SymbolInfo symbolInfo = classSymbolTable.resolve(fieldList.get(fieldList.size()-1));
        int startIndex = symbolInfo.getRange().startIndex();
        int endLine = symbolInfo.getRange().endLine();
        int endIndex = symbolInfo.getRange().inclusiveEndIndex() + 1;
        TextChangeRange range = new TextChangeRange(endLine, endIndex, endLine, endIndex);
        String indentation = context.getIndentation();

        String className = context.getTargetClass();
        String constructor = "\n\n" + "public " + className + "(";

        List<String> parameters = new ArrayList<>();
        for (String fieldName : requiredFieldList) {
            TypeInfo typeInfo = classSymbolTable.resolve(fieldName).getTypeInfo();
            String typeName = typeInfo.getName();
            parameters.add(typeName + " " + fieldName);
        }
        constructor += String.join(", ", parameters);
        constructor += ") {\n";

        List<String> assignments = new ArrayList<>();
        for (String fieldName : requiredFieldList) {
            TypeInfo typeInfo = classSymbolTable.resolve(fieldName).getTypeInfo();
            String typeName = typeInfo.getName();
            assignments.add(indentation + "this." + fieldName + " = " + fieldName + ";");
        }
        constructor += String.join("\n", assignments);
        constructor += "\n}";

        context.appendConstructorPartText(constructor);
        context.addProcessedAction("constructor(required)");
    }
}
