package jplus.plugin.intellij.psi;

import com.intellij.lang.ASTNode;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class ApplyBlockPsiElement extends ANTLRPsiNode {
    public ApplyBlockPsiElement(@NotNull ASTNode node) {
        super(node);
    }
}
