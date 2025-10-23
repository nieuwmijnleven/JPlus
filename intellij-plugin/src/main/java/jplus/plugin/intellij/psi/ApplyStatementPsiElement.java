package jplus.plugin.intellij.psi;

import com.intellij.lang.ASTNode;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class ApplyStatementPsiElement extends ANTLRPsiNode {
    public ApplyStatementPsiElement(@NotNull ASTNode node) {
        super(node);
    }
}
