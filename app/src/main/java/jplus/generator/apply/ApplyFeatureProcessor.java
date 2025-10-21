package jplus.generator.apply;

public interface ApplyFeatureProcessor {
    default void process(ApplyFeatureProcessingContext context) {
        context.addProcessedAction(context.getFeature().getAction().toLowerCase());
    }
}