package jplus.generator.apply;

import jplus.base.TypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToStringFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        if (context.hasProcessed("tostring")) return;

        String methodSymbol = "^toString$_";
        if (context.getClassSymbolTable().contains(methodSymbol, TypeInfo.Type.Method)) {
            return;
        }

        String toString = "\n@Override\n";
        toString += "public String toString() {\n";
        toString += context.getIndentation() + "return \"" + context.getTargetClass() + "{\"";

        List<String> lines = new ArrayList<>();
        for (String fieldName : context.getFieldList()) {
            lines.add("\"" + fieldName + "=\"" + " + " + fieldName);
        }
        toString += lines.stream().collect(Collectors.joining("+ \", \" + ", " + ", " + "));
        toString += "\"}\";\n";
        toString += "}\n";
        context.appendMethodPartText(toString);
//        ApplyFeatureProcessor.super.process(context);
        context.addProcessedAction("tostring");
    }
}
