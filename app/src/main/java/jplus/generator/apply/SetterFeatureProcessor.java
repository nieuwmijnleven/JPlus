package jplus.generator.apply;

import jplus.base.Modifier;
import jplus.base.SymbolInfo;
import jplus.base.SymbolTable;
import jplus.base.TypeInfo;
import jplus.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class SetterFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("setter")) return;

        SymbolTable classSymbolTable = context.getClassSymbolTable();
        for (String fieldName : context.getFieldList()) {
            SymbolInfo symbolInfo = classSymbolTable.resolve(fieldName);
            TypeInfo typeInfo = symbolInfo.getTypeInfo();
            String typeName = typeInfo.getName();

            List<Modifier> excludedModifiers = List.of(Modifier.FINAL, Modifier.STATIC);
            if (symbolInfo.getModifierList().stream().anyMatch(modifier -> excludedModifiers.contains(modifier))) {
                continue;
            }

            String methodName = "set" + Utils.convertToPascalCase(fieldName);
            String methodSymbol = "^" + methodName + "$_" + typeName;
            if (classSymbolTable.contains(methodSymbol, TypeInfo.Type.Method)) {
                continue;
            }

            StringBuffer methodPartText = new StringBuffer();
            methodPartText.append("\n");
            methodPartText.append("public ").append("void").append(" ").append(methodName).append("(").append(typeName).append(" ").append(fieldName).append(")").append(" {\n");
            methodPartText.append(context.getIndentation()).append("this.").append(fieldName).append(" = ").append(fieldName).append(";\n").append("}\n");

            context.appendMethodPartText(methodPartText.toString());
        }
//        ApplyFeatureProcessor.super.process(context);
        context.addProcessedAction("setter");
    }
}
