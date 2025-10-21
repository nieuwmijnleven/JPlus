package jplus.generator.apply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplyStatement {
    private String qualifiedName;
    private final List<ApplyFeature> featureList;

    public ApplyStatement(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.featureList = new ArrayList<>();
    }

    public ApplyStatement(String qualifiedName, List<ApplyFeature> featureList) {
        this(qualifiedName);
        this.featureList.addAll(featureList);
    }

    public void addApplyFeature(ApplyFeature feature) {
        featureList.add(feature);
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public List<ApplyFeature> getFeatureList() {
        return Collections.unmodifiableList(featureList);
    }

    @Override
    public String toString() {
        return "ApplyStatement{" +
                "qualifiedName='" + qualifiedName + '\'' +
                ", featureList=" + featureList +
                '}';
    }
}
