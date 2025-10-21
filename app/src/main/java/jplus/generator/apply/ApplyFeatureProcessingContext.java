package jplus.generator.apply;

import jplus.base.SymbolTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ApplyFeatureProcessingContext {
    private ApplyFeature feature;
    private final SymbolTable classSymbolTable;
    private final StringBuilder constructorPartText;
    private final StringBuilder methodPartText;
    private final List<String> fieldList;
    private final List<String> primitiveFields;
    private final List<String> referenceFields;
    private final String targetClass;
    private final String indentation;
    private final Set<String> processed = new HashSet<>();

    public ApplyFeatureProcessingContext(ApplyFeature feature,
                                         SymbolTable classSymbolTable,
                                         StringBuilder constructorPartText,
                                         StringBuilder methodPartText,
                                         List<String> fieldList,
                                         List<String> primitiveFields,
                                         List<String> referenceFields,
                                         String targetClass,
                                         String indentation) {
        this.feature = feature;
        this.classSymbolTable = classSymbolTable;
        this.constructorPartText = constructorPartText;
        this.methodPartText = methodPartText;
        this.fieldList = fieldList;
        this.primitiveFields = primitiveFields;
        this.referenceFields = referenceFields;
        this.targetClass = targetClass;
        this.indentation = indentation;
    }

    public void addProcessedAction(String action) {
        processed.add(action);
    }

    public void resetProcessedAction() {
        processed.remove(feature.getAction().toLowerCase());
    }

    public boolean hasProcessed() {
        return processed.contains(feature.getAction().toLowerCase());
    }

    public boolean hasProcessed(String action) {
        return processed.contains(action);
    }

    public void setFeature(ApplyFeature feature) {
        this.feature = feature;
    }

    // Getter 메서드들...
    public ApplyFeature getFeature() { return feature; }
    public SymbolTable getClassSymbolTable() { return classSymbolTable; }
    public void appendConstructorPartText(String text) { constructorPartText.append(text); }
    public void appendMethodPartText(String text) { methodPartText.append(text); }
    public List<String> getFieldList() { return fieldList; }
    public List<String> getPrimitiveFields() { return primitiveFields; }
    public List<String> getReferenceFields() { return referenceFields; }
    public String getTargetClass() { return targetClass; }
    public String getIndentation() { return indentation; }

    // Builder 클래스
    public static class Builder {
        private ApplyFeature feature;
        private SymbolTable classSymbolTable;
        private StringBuilder constructorPartText;
        private StringBuilder methodPartText;
        private List<String> fieldList;
        private List<String> primitiveFields;
        private List<String> referenceFields;
        private String targetClass;
        private String indentation;

        public Builder feature(ApplyFeature feature) {
            this.feature = feature;
            return this;
        }

        public Builder classSymbolTable(SymbolTable classSymbolTable) {
            this.classSymbolTable = classSymbolTable;
            return this;
        }

        public Builder constructorPartText(StringBuilder constructorPartText) {
            this.constructorPartText = constructorPartText;
            return this;
        }

        public Builder methodPartText(StringBuilder methodPartText) {
            this.methodPartText = methodPartText;
            return this;
        }

        public Builder fieldList(List<String> fieldList) {
            this.fieldList = fieldList;
            return this;
        }

        public Builder primitiveFields(List<String> primitiveFields) {
            this.primitiveFields = primitiveFields;
            return this;
        }

        public Builder referenceFields(List<String> referenceFields) {
            this.referenceFields = referenceFields;
            return this;
        }

        public Builder targetClass(String targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public Builder indentation(String indentation) {
            this.indentation = indentation;
            return this;
        }

        public ApplyFeatureProcessingContext build() {
            return new ApplyFeatureProcessingContext(
                feature,
                classSymbolTable,
                constructorPartText,
                methodPartText,
                fieldList,
                primitiveFields,
                referenceFields,
                targetClass,
                indentation
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
