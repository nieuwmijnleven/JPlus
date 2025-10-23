package jplus.plugin.intellij.psi;

import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.antlr.intellij.adaptor.psi.ANTLRPsiLeafNode;
import org.jetbrains.annotations.NotNull;

public class IdentifierPsiElement extends ANTLRPsiLeafNode implements PsiNamedElement {

    public IdentifierPsiElement(IElementType type, CharSequence text) {
        super(type, text);
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiReference getReference() {
        return new JPlusPsiReference(this, new TextRange(0, getTextLength()));
    }
}
