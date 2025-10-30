package jplus.plugin.intellij;

import com.intellij.core.CoreASTFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import jplus.base.JPlus20Lexer;
import jplus.plugin.intellij.psi.IdentifierPsiElement;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.jetbrains.annotations.NotNull;

public class JPlusASTFactory extends CoreASTFactory {
	@NotNull
    @Override
    public CompositeElement createComposite(IElementType type) {
        return super.createComposite(type);
    }

	@NotNull
	@Override
	public LeafElement createLeaf(@NotNull IElementType type, CharSequence text) {
		if (type instanceof TokenIElementType tokenIElementType &&
			 tokenIElementType.getANTLRTokenType() == JPlus20Lexer.Identifier) {
			return new IdentifierPsiElement(type, text);
		}
		LeafElement leaf = super.createLeaf(type, text);
		return leaf;
    }
}
