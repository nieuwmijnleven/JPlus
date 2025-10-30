package jplus.generator.apply;

import jplus.base.TypeInfo;
import jplus.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EqualsFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("equals")) return;

        String methodSymbol = "^equals$_Object";
        if (context.getClassSymbolTable().contains(methodSymbol, TypeInfo.Type.Method)) {
            return;
        }

        List<String> primitiveTypeFieldList = context.getPrimitiveFields();
        List<String> referenceTypeFieldList = context.getReferenceFields();
        if (primitiveTypeFieldList.isEmpty() && referenceTypeFieldList.isEmpty()) {
            context.appendMethodPartText("true;\n");
        } else {
            String className = context.getTargetClass();
            String instanceName = Utils.convertToCamelCase(className);

            List<String> equalityChecks = new ArrayList<>();
            for (String primitiveTypeField : primitiveTypeFieldList) {
                equalityChecks.add(primitiveTypeField + " == " + instanceName + "." + primitiveTypeField);
            }

            for (String referenceTypeField : referenceTypeFieldList) {
                equalityChecks.add("java.util.Objects.equals(" + referenceTypeField + ", " + instanceName + "." + referenceTypeField + ")");
            }

            StringBuilder generatedText = new StringBuilder();
            String indentation = context.getIndentation();
            String doubleIndentation = indentation.repeat(2);

            generatedText.append("\n@Override\n");
            generatedText.append("public boolean equals(").append("Object o").append(") {\n");
            generatedText.append(indentation).append("if (this == o) return true;\n");
            generatedText.append(indentation).append("if (o == null || getClass() != o.getClass()) return false;\n");
            generatedText.append(indentation).append(className).append(" ").append(instanceName).append(" = ").append("(").append(className).append(") o;\n");

            generatedText.append(indentation).append("return ");
            generatedText.append(String.join("\n" + doubleIndentation + "&& ", equalityChecks));
            generatedText.append(";\n");
            generatedText.append("}\n");

            context.appendMethodPartText(generatedText.toString());
        }

//        ApplyFeatureProcessor.super.process(context);
        context.addProcessedAction("equals");
    }
}
