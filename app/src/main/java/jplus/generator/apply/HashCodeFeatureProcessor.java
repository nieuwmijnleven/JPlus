package jplus.generator.apply;

import jplus.base.TypeInfo;
import jplus.util.Utils;

public class HashCodeFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("hashcode")) return;

        String methodSymbol = "^hashCode$_";
        if (context.getClassSymbolTable().contains(methodSymbol, TypeInfo.Type.Method)) {
            return;
        }

        StringBuilder generatedText = new StringBuilder();
        generatedText.append("\n@Override\n");
        generatedText.append("public int hashCode() {\n");
        generatedText.append(context.getIndentation()).append("return ").append("java.util.Objects.hash(").append(String.join(", ", context.getFieldList())).append(");\n");
        generatedText.append("}\n");

        context.appendMethodPartText(generatedText.toString());
//        ApplyFeatureProcessor.super.process(context);
        context.addProcessedAction("hashcode");
    }
}
