package jplus.plugin.intellij;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiReference;
import com.jetbrains.cef.remote.thrift.annotation.Nullable;

public class JPlusGotoDefinitionHandler implements GotoDeclarationHandler {
    @Override
    public  @Nullable PsiElement[] getGotoDeclarationTargets(
            @Nullable PsiElement sourceElement, int offset, Editor editor) {

        if (sourceElement == null) return PsiElement.EMPTY_ARRAY;

        PsiReference ref = sourceElement.getReference();
        if (ref != null) {
            PsiElement resolved = ref.resolve();
            if (resolved != null) {
                return new PsiElement[]{resolved};
            }
        }

        return PsiElement.EMPTY_ARRAY;
    }

    private @Nullable PsiElement findCorrespondingJavaElement(PsiJavaFile javaPsiFile, int offset) {
        return javaPsiFile.findElementAt(offset);
    }

    @Override
    public @Nullable String getActionText(@Nullable com.intellij.openapi.actionSystem.DataContext context) {
        return "Go to Definition";
    }
}