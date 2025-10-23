package jplus.generator.apply;

import jplus.base.Modifier;
import jplus.base.SymbolInfo;
import jplus.base.TypeInfo;
import jplus.util.Utils;

import java.util.List;

public class GetterFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("getter")) return;

        for (String fieldName : context.getFieldList()) {
            String methodName = "get" + Utils.convertToPascalCase(fieldName);
            String methodSymbol = "^" + methodName + "$_";
            if (context.getClassSymbolTable().contains(methodSymbol, TypeInfo.Type.Method)) {
                continue;
            }

            SymbolInfo symbolInfo = context.getClassSymbolTable().resolve(fieldName);
            TypeInfo typeInfo = symbolInfo.getTypeInfo();
            String typeName = typeInfo.getName();

            List<Modifier> excludedModifiers = List.of(Modifier.STATIC);
            if (symbolInfo.getModifierList().stream().anyMatch(modifier -> excludedModifiers.contains(modifier))) {
                continue;
            }

            StringBuilder methodPartText = new StringBuilder();
            methodPartText.append("\n");
            methodPartText.append("public ").append(typeName).append(" ").append(methodName).append("() {\n");
            methodPartText.append(context.getIndentation()).append("return ").append(fieldName).append(";\n");
            methodPartText.append("}\n");

            context.appendMethodPartText(methodPartText.toString());
        }
//        ApplyFeatureProcessor.super.process(context);
        context.addProcessedAction("getter");
    }
}