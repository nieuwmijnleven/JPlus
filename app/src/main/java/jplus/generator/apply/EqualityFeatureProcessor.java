package jplus.generator.apply;

public class EqualityFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        ApplyFeatureProcessor processor = new EqualsFeatureProcessor();
        processor.process(context);

        processor = new HashCodeFeatureProcessor();
        processor.process(context);
    }
}
