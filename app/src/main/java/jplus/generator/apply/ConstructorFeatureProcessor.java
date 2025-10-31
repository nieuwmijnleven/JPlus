package jplus.generator.apply;

import jplus.base.Modifier;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.generator.TextChangeRange;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        List<String> argumentList = context.getFeature().getArgumentList();
        if (argumentList.isEmpty()) {
            throw new IllegalArgumentException("constructor must be used with required, all and no arguments");
        }

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
    }

    private void processNoArgsConsturctor(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("constructor(no)")) return;

        String className = context.getTargetClass();
        SymbolTable classSymbolTable = context.getClassSymbolTable();
        String constructorName = "^constructor$_";
        if (classSymbolTable.resolve(constructorName) != null) {
            return;
        }

        TypeInfo typeInfo = new TypeInfo(constructorName, false, TypeInfo.Type.Constructor);
        SymbolInfo constructorSymInfo = SymbolInfo.builder().symbol(constructorName).typeInfo(typeInfo).build();
        classSymbolTable.declare(constructorName, constructorSymInfo);

        List<String> fieldList = context.getFieldList();
        if (fieldList.isEmpty()) return;

        boolean hasFinalField = fieldList.stream().map(classSymbolTable::resolve).anyMatch(symbolInfo -> symbolInfo.getModifierList().contains(Modifier.FINAL));
        if (hasFinalField) return;

        String constructor = "\n\n" + "public " + className + "() {}";
        context.appendConstructorPartText(constructor);
        context.addProcessedAction("constructor(no)");
    }

    public void processAllArgsConstructor(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("constructor(all)")) return;

        List<String> fieldList = context.getFieldList();
        if (fieldList.isEmpty()) return;

        SymbolTable classSymbolTable = context.getClassSymbolTable();
        List<SymbolInfo> nonStaticFieldList = fieldList.stream().map(classSymbolTable::resolve).filter(symbolInfo -> !symbolInfo.getModifierList().contains(Modifier.STATIC)).toList();
        if (nonStaticFieldList.isEmpty()) return;

        String nonoStaticTypeNameJoining = nonStaticFieldList.stream().map(SymbolInfo::getTypeInfo).map(TypeInfo::getName).collect(Collectors.joining("_"));
        String constructorName = "^constructor$_" + nonoStaticTypeNameJoining;
        if (classSymbolTable.resolve(constructorName) != null) {
            return;
        }

        TypeInfo typeInfo = new TypeInfo(constructorName, false, TypeInfo.Type.Constructor);
        SymbolInfo constructorSymInfo = SymbolInfo.builder().symbol(constructorName).typeInfo(typeInfo).build();
        classSymbolTable.declare(constructorName, constructorSymInfo);

        String indentation = context.getIndentation();

        String className = context.getTargetClass();
        String constructor = "\n\n" + "public " + className + "(";

        List<String> parameters = new ArrayList<>();
        for (SymbolInfo symbolInfo : nonStaticFieldList) {
            String typeName = symbolInfo.getTypeInfo().getName();
            String fieldName = symbolInfo.getSymbol();
            parameters.add(typeName + " " + fieldName);
        }
        constructor += String.join(", ", parameters);
        constructor += ") {\n";

        List<String> assignments = new ArrayList<>();
        for (SymbolInfo symbolInfo : nonStaticFieldList) {
            String fieldName = symbolInfo.getSymbol();
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
        List<SymbolInfo> nonStaticFieldList = fieldList.stream().map(classSymbolTable::resolve).filter(symbolInfo -> !symbolInfo.getModifierList().contains(Modifier.STATIC)).toList();
        if (nonStaticFieldList.isEmpty()) return;

        List<SymbolInfo> requiredFieldList = new ArrayList<>();
        for (SymbolInfo symbolInfo : nonStaticFieldList) {
            List<Modifier> modifierList = symbolInfo.getModifierList();
            if (modifierList != null && modifierList.contains(Modifier.FINAL)) {
                requiredFieldList.add(symbolInfo);
            }
        }

        if (requiredFieldList.isEmpty()) return;

        String requiredTypeNameJoining = requiredFieldList.stream().map(SymbolInfo::getTypeInfo).map(TypeInfo::getName).collect(Collectors.joining("_"));
        String constructorName = "^constructor$_" + requiredTypeNameJoining;
        System.out.println("classSymbolTable = " + classSymbolTable);
        System.out.println("constructorName = " + constructorName);
        if (classSymbolTable.resolve(constructorName) != null) {
            return;
        }

        TypeInfo typeInfo = new TypeInfo(constructorName, false, TypeInfo.Type.Constructor);
        SymbolInfo constructorSymInfo = SymbolInfo.builder().symbol(constructorName).typeInfo(typeInfo).build();
        classSymbolTable.declare(constructorName, constructorSymInfo);

        String indentation = context.getIndentation();

        String className = context.getTargetClass();
        String constructor = "\n\n" + "public " + className + "(";

        List<String> parameters = new ArrayList<>();
        for (SymbolInfo symInfo : requiredFieldList) {
            String typeName = symInfo.getTypeInfo().getName();
            String fieldName = symInfo.getSymbol();
            parameters.add(typeName + " " + fieldName);
        }
        constructor += String.join(", ", parameters);
        constructor += ") {\n";

        List<String> assignments = new ArrayList<>();
        for (SymbolInfo symInfo : requiredFieldList) {
            String fieldName = symInfo.getSymbol();
            assignments.add(indentation + "this." + fieldName + " = " + fieldName + ";");
        }
        constructor += String.join("\n", assignments);
        constructor += "\n}";

        context.appendConstructorPartText(constructor);
        context.addProcessedAction("constructor(required)");
    }
}
