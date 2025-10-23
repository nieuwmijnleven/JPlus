package jplus.generator.apply;

import jplus.base.Modifier;
import jplus.base.SymbolInfo;
import jplus.base.TypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuilderFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("builder")) return;

        if (context.getClassSymbolTable().contains("Builder", TypeInfo.Type.Class)) {
            return;
        }

        String methodName = "^builder$_";
        if (context.getClassSymbolTable().contains(methodName, TypeInfo.Type.Method)) {
            return;
        }

        ConstructorFeatureProcessor processor = new ConstructorFeatureProcessor();
        processor.processAllArgsConstructor(context);

        String indentation = context.getIndentation();
        String doubleIndentation = indentation.repeat(2);

        String builder = "\npublic static class Builder {";
        List<String> fieldDeclarations = new ArrayList<>();
        List<String> methodDeclarations = new ArrayList<>();
        List<String> nonStaticFieldList = new ArrayList<>();
        for (String fieldName : context.getFieldList()) {
            SymbolInfo symbolInfo = context.getClassSymbolTable().resolve(fieldName);
            TypeInfo typeInfo = symbolInfo.getTypeInfo();

            List<Modifier> excludedModifiers = List.of(Modifier.STATIC);
            if (symbolInfo.getModifierList().stream().anyMatch(modifier -> excludedModifiers.contains(modifier))) {
                continue;
            }

            fieldDeclarations.add(indentation + "private " + typeInfo.getName() + " " + fieldName + ";");
            methodDeclarations.add(indentation + "public Builder " + fieldName + "(" + typeInfo.getName() + " " + fieldName + ") {\n" +
                    doubleIndentation + "this." + fieldName + " = " + fieldName + ";\n" +
                    doubleIndentation + "return this;\n" +
                    indentation + "}"
            );

            nonStaticFieldList.add(fieldName);
        }

        builder += fieldDeclarations.stream().collect(Collectors.joining("\n", "\n", "\n"));
        builder += methodDeclarations.stream().collect(Collectors.joining("\n\n", "\n", "\n"));
        builder += "\n" + indentation + "public " + context.getTargetClass() + " build() {\n";
        builder += doubleIndentation + "return new " + context.getTargetClass() + "(" + nonStaticFieldList.stream().collect(Collectors.joining(", ")) + ");\n";
        builder += indentation + "}\n";
        builder += "}\n";

        builder += "\npublic static Builder builder() {\n";
        builder += indentation + "return new Builder();\n";
        builder += "}\n";

        context.appendMethodPartText(builder);
//        ApplyFeatureProcessor.super.process(context);
        context.addProcessedAction("builder");
    }
}
