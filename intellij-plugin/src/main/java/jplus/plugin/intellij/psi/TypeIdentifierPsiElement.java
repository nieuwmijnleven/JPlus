package jplus.plugin.intellij.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class TypeIdentifierPsiElement extends ANTLRPsiNode implements PsiNamedElement {
    public TypeIdentifierPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String s) throws IncorrectOperationException {
        return null;
    }

//    @Override
//    public PsiReference getReference() {
//        PsiElement parent = getParent();
//        IElementType iElementType = parent.getNode().getElementType();
//        // do not return a reference for the ID nodes in a definition
//        if ( iElementType instanceof RuleIElementType ruleIElementType) {
//            switch ( ruleIElementType.getRuleIndex() ) {
//                case RULE_normalClassDeclaration:
////                case RULE_expr :
////                case RULE_primary :
////                    return new NormalClassDeclarationPsiReference(this);
////                case RULE_call_expr :
////                    return new FunctionRef(this);
//            }
//        }
//        return null;
//    }
}
