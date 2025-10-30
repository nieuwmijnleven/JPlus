package jplus.plugin.intellij;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.JavaCompletionContributor;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import jplus.plugin.intellij.psi.ApplyBlockPsiElement;
import jplus.plugin.intellij.psi.ApplyStatementPsiElement;
import jplus.plugin.intellij.util.JPlusUtil;
import jplus.plugin.intellij.util.PsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class JPlusCompletionContributor extends CompletionContributor {

    private static final List<String> APPLY_KEYWORDS = Arrays.asList(
            "getter", "setter", "data", "equality", "constructor", "builder", "tostring", "equals", "hashcode"
    );

    public JPlusCompletionContributor() {
        extend(CompletionType.BASIC, JPlusPatterns.psiElement(), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters,
                                          @NotNull ProcessingContext context,
                                          @NotNull CompletionResultSet result) {
                Project project = parameters.getPosition().getProject();
                PsiElement psiElement = parameters.getPosition();
                PsiFile jplusFile = psiElement.getContainingFile();

                collectApplyCandidates(psiElement, result);

                PsiFile javaPsiFile = JPlusUtil.createJavaPsiFromJPlus(project, jplusFile);
                if (javaPsiFile != null) {
                    invokeJavaCompletion(javaPsiFile, parameters, result);
                }
            }
        });
    }

    private void collectApplyCandidates(PsiElement element, CompletionResultSet result) {
        PsiElement parent = element.getParent();
        if (parent == null) return;

        boolean isInAppyStatement = PsiTreeUtil.getParentOfType(element, ApplyStatementPsiElement.class) != null;
        boolean isInAppyBlock = PsiTreeUtil.getParentOfType(element, ApplyBlockPsiElement.class) != null;
        boolean isInApply = isInAppyStatement || isInAppyBlock;
        if (!isInApply) return;

        if (isInAppyBlock) {
            PsiFile file = element.getContainingFile();
            PsiUtils.getClassNameList(file).forEach(className -> {
                result.addElement(LookupElementBuilder.create(className));
            });
        }

        for (String keyword : APPLY_KEYWORDS) {
            result.addElement(LookupElementBuilder.create(keyword));
        }
    }

    private void invokeJavaCompletion(PsiFile javaPsiFile,
                                      CompletionParameters originalParameters,
                                      CompletionResultSet result) {
        PsiElement javaElementAtCursor = javaPsiFile.findElementAt(originalParameters.getOffset());
        CompletionParameters javaParams = originalParameters.withPosition(javaElementAtCursor, originalParameters.getOffset());

        JavaCompletionContributor javaContributor = new JavaCompletionContributor();
        javaContributor.fillCompletionVariants(javaParams, result);
    }
}
