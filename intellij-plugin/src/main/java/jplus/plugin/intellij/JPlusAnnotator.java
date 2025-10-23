package jplus.plugin.intellij;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import jplus.analyzer.NullabilityChecker;
import jplus.processor.JPlusProcessor;
import org.jetbrains.annotations.NotNull;

public class JPlusAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof JPlusFile)) return;

        System.err.println("run annotator = " + element.getClass().getSimpleName());
        PsiFile jplusFile = element.getContainingFile();
        System.err.println("jplusFile.getText() = " + jplusFile.getText());
        JPlusProcessor processor = new JPlusProcessor(jplusFile.getText());
        try {
            processor.process();
            var issues = processor.checkNullability();
            if (!issues.isEmpty()) {
                System.err.println("nullability warning");
                for (NullabilityChecker.NullabilityIssue issue : issues) {
                    System.out.println(issue);
                    holder.newAnnotation(HighlightSeverity.ERROR, issue.getMessage())
                            .range(new TextRange(issue.getOffset(), issue.getOffset()))
                            .create();
                }
            }
        } catch (Exception e) {
            //System.err.println(e.getMessage());
            //throw new RuntimeException(e);
        }
    }
}
