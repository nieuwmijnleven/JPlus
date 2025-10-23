package jplus.plugin.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import jplus.plugin.intellij.JPlusLanguage;
import org.antlr.intellij.adaptor.SymtabUtils;
import org.antlr.intellij.adaptor.psi.IdentifierDefSubtree;
import org.antlr.intellij.adaptor.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodPsiElement extends IdentifierDefSubtree implements ScopeNode {

    public MethodPsiElement(@NotNull ASTNode node, @NotNull IElementType idElementType) {
        super(node, idElementType);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return super.setName(name);
    }

    @Override
    public @Nullable PsiElement resolve(PsiNamedElement element) {
        return SymtabUtils.resolve(this, JPlusLanguage.INSTANCE,
                element, "/*/methodDeclaration/methodHeader/methodDeclarator/identifier");
    }
}
