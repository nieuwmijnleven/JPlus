package jplus.plugin.intellij.adapter;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.plugin.intellij.JPlus20IntellijLexer;
import jplus.plugin.intellij.JPlusLanguage;
import jplus.plugin.intellij.JPlusTokenTypes;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Nullable;

public class JPlusLexerAdapter extends ANTLRLexerAdaptor {

    static {
        initializeElementTypeFactory();
    }

    public static void initializeElementTypeFactory() {
        PSIElementTypeFactory.defineLanguageIElementTypes(
                JPlusLanguage.INSTANCE,
                JPlus20Lexer.tokenNames,
                JPlus20Parser.ruleNames
        );
    }

    /**
     * Constructs a new instance of {@link ANTLRLexerAdaptor} with
     * the specified {@link Language} and underlying ANTLR {@link
     * Lexer}.
     *
     * @param language The language.
     * @param lexer    The underlying ANTLR lexer.
     */
    public JPlusLexerAdapter() {
        super(JPlusLanguage.INSTANCE, new JPlus20IntellijLexer(null));
    }

//    @Override
//    public @Nullable IElementType getTokenType(int antlrTokenType) {
//        if (antlrTokenType == Token.EOF) {
//            return JPlusTokenTypes.BAD_TOKEN_TYPE;
//        }
//
//        IElementType tokenType = super.getTokenType(antlrTokenType);
//        return (tokenType != null) ? tokenType : JPlusTokenTypes.BAD_TOKEN_TYPE;
//    }
}
