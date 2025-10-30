package jplus.generator.apply;

public class DataFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        ApplyFeatureProcessor processor = new GetterFeatureProcessor();
        processor.process(context);

        processor = new SetterFeatureProcessor();
        processor.process(context);

        processor = new ToStringFeatureProcessor();
        processor.process(context);

        processor = new EqualityFeatureProcessor();
        processor.process(context);
    }
}
