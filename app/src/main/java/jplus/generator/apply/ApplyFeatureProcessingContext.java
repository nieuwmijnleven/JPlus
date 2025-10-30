package jplus.generator.apply;

import jplus.base.SymbolTable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ApplyFeatureProcessingContext {
    private ApplyFeature feature;
    private final SymbolTable classSymbolTable;
    private final StringBuilder constructorPartText;
    private final StringBuilder methodPartText;
    private final Set<String> processedActionList;
    private final List<String> fieldList;
    private final List<String> primitiveFields;
    private final List<String> referenceFields;
    private final String targetClass;
    private final String qualifiedName;
    private final String indentation;

    public ApplyFeatureProcessingContext(ApplyFeature feature,
                                         SymbolTable classSymbolTable,
                                         StringBuilder constructorPartText,
                                         StringBuilder methodPartText, Set<String> processedActionList,
                                         List<String> fieldList,
                                         List<String> primitiveFields,
                                         List<String> referenceFields,
                                         String targetClass, String qualifiedName,
                                         String indentation) {
        this.feature = feature;
        this.classSymbolTable = classSymbolTable;
        this.constructorPartText = constructorPartText;
        this.methodPartText = methodPartText;
        this.processedActionList = processedActionList;
        this.fieldList = fieldList;
        this.primitiveFields = primitiveFields;
        this.referenceFields = referenceFields;
        this.targetClass = targetClass;
        this.qualifiedName = qualifiedName;
        this.indentation = indentation;
    }

    public void addProcessedAction(String detailedAction) {
        processedActionList.add(detailedAction);
    }

    public void removeProcessedAction(String detailedAction) {
        processedActionList.remove(detailedAction);
    }

    public boolean hasProcessed(String detailedAction) {
        return processedActionList.contains(detailedAction);
    }

    public boolean hasProcessed() {
        return processedActionList.contains(feature.getAction().toLowerCase());
    }

    public void setFeature(ApplyFeature feature) {
        this.feature = feature;
    }

    public ApplyFeature getFeature() { return feature; }
    public SymbolTable getClassSymbolTable() { return classSymbolTable; }
    public void appendConstructorPartText(String text) { constructorPartText.append(text);
    }
    public void appendMethodPartText(String text) { methodPartText.append(text); }
    public String getConstructorPartText() {
        return constructorPartText.toString();
    }
    public String getMethodPartText() {
        return methodPartText.toString();
    }

    public Set<String> getProcessedActionList() {
        return Collections.unmodifiableSet(processedActionList);
    }

    public List<String> getFieldList() { return fieldList; }
    public List<String> getPrimitiveFields() { return primitiveFields; }
    public List<String> getReferenceFields() { return referenceFields; }
    public String getTargetClass() { return targetClass; }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getIndentation() { return indentation; }

    public static class Builder {
        private ApplyFeature feature;
        private SymbolTable classSymbolTable;
        private StringBuilder constructorPartText;
        private StringBuilder methodPartText;
        private Set<String> processedActionList;
        private List<String> fieldList;
        private List<String> primitiveFields;
        private List<String> referenceFields;
        private String targetClass;
        private String qualifiedName;
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

        public Builder processedActionList(Set<String> processedActionList) {
            this.processedActionList = processedActionList;
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

        public Builder qualifiedName(String qualifiedName) {
            this.qualifiedName = qualifiedName;
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
                processedActionList,
                fieldList,
                primitiveFields,
                referenceFields,
                targetClass,
                qualifiedName,
                indentation
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
