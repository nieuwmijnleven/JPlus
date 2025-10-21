package jplus.generator.apply;

public class DataFeatureProcessor implements ApplyFeatureProcessor {
    @Override
    public void process(ApplyFeatureProcessingContext context) {
        ApplyFeatureProcessor processor = new GetterFeatureProcessor();
        processor.process(context);

        context.resetProcessedAction();
        processor = new SetterFeatureProcessor();
        processor.process(context);

        context.resetProcessedAction();
        processor = new ToStringFeatureProcessor();
        processor.process(context);

        context.resetProcessedAction();
        processor = new EqualityFeatureProcessor();
        processor.process(context);

    }
}
