package jplus.plugin.intellij.psi;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiNamedElementWrapper extends PsiElementWrapper implements PsiNamedElement {

    public PsiNamedElementWrapper(PsiNamedElement psiElement, PsiFile psiFile) {
        super(psiElement, psiFile);
    }

    @Override
    public @Nullable @NlsSafe String getName() {
        return ((PsiNamedElement)this.psiElement).getName();
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String s) throws IncorrectOperationException {
        return ((PsiNamedElement)this.psiElement).setName(s);
    }
}
