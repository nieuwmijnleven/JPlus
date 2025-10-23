package jplus.plugin.intellij;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.plugin.intellij.adapter.JPlusLexerAdapter;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.intellij.lang.annotations.MagicConstant;

import java.util.List;

public class JPlusTokenTypes {
    public static IElementType BAD_TOKEN_TYPE = new IElementType("BAD_TOKEN", JPlusLanguage.INSTANCE);

    static {
        JPlusLexerAdapter.initializeElementTypeFactory();
    }

    public static final List<TokenIElementType> TOKEN_ELEMENT_TYPES =
            PSIElementTypeFactory.getTokenIElementTypes(JPlusLanguage.INSTANCE);
    public static final List<RuleIElementType> RULE_ELEMENT_TYPES =
            PSIElementTypeFactory.getRuleIElementTypes(JPlusLanguage.INSTANCE);

    public static final TokenSet COMMENTS =
            PSIElementTypeFactory.createTokenSet(
                    JPlusLanguage.INSTANCE,
                    JPlus20Lexer.COMMENT,
                    JPlus20Lexer.LINE_COMMENT);

    public static final TokenSet WHITESPACES =
            PSIElementTypeFactory.createTokenSet(
                    JPlusLanguage.INSTANCE,
                    JPlus20Lexer.WS);

    public static final TokenSet STRING_LITERALS =
            PSIElementTypeFactory.createTokenSet(
                    JPlusLanguage.INSTANCE,
                    JPlus20Lexer.StringLiteral);

    public static final TokenSet KEYWORDS =
            PSIElementTypeFactory.createTokenSet(
                    JPlusLanguage.INSTANCE,
                    JPlus20Lexer.ABSTRACT,
                    JPlus20Lexer.APPLY,
                    JPlus20Lexer.ASSERT,
                    JPlus20Lexer.BOOLEAN,
                    JPlus20Lexer.BREAK,
                    JPlus20Lexer.BYTE,
                    JPlus20Lexer.CASE,
                    JPlus20Lexer.CATCH,
                    JPlus20Lexer.CHAR,
                    JPlus20Lexer.CLASS,
                    JPlus20Lexer.CONST,
                    JPlus20Lexer.CONTINUE,
                    JPlus20Lexer.DEFAULT,
                    JPlus20Lexer.DO,
                    JPlus20Lexer.DOUBLE,
                    JPlus20Lexer.ELSE,
                    JPlus20Lexer.ENUM,
                    JPlus20Lexer.EXTENDS,
                    JPlus20Lexer.FINAL,
                    JPlus20Lexer.FINALLY,
                    JPlus20Lexer.FLOAT,
                    JPlus20Lexer.FOR,
                    JPlus20Lexer.IF,
                    JPlus20Lexer.GOTO,
                    JPlus20Lexer.IMPLEMENTS,
                    JPlus20Lexer.IMPORT,
                    JPlus20Lexer.INSTANCEOF,
                    JPlus20Lexer.INT,
                    JPlus20Lexer.INTERFACE,
                    JPlus20Lexer.LONG,
                    JPlus20Lexer.NATIVE,
                    JPlus20Lexer.NEW,
                    JPlus20Lexer.PACKAGE,
                    JPlus20Lexer.PRIVATE,
                    JPlus20Lexer.PROTECTED,
                    JPlus20Lexer.PUBLIC,
                    JPlus20Lexer.RETURN,
                    JPlus20Lexer.SHORT,
                    JPlus20Lexer.STATIC,
                    JPlus20Lexer.STRICTFP,
                    JPlus20Lexer.SUPER,
                    JPlus20Lexer.SWITCH,
                    JPlus20Lexer.SYNCHRONIZED,
                    JPlus20Lexer.THIS,
                    JPlus20Lexer.THROW,
                    JPlus20Lexer.THROWS,
                    JPlus20Lexer.TRANSIENT,
                    JPlus20Lexer.TRY,
                    JPlus20Lexer.VOID,
                    JPlus20Lexer.VOLATILE,
                    JPlus20Lexer.WHILE,
                    JPlus20Lexer.UNDER_SCORE
            );

    public static RuleIElementType getRuleElementType(@MagicConstant(valuesFromClass = JPlus20Parser.class)int ruleIndex){
        return RULE_ELEMENT_TYPES.get(ruleIndex);
    }
    public static TokenIElementType getTokenElementType(@MagicConstant(valuesFromClass = JPlus20Lexer.class)int ruleIndex){
        return TOKEN_ELEMENT_TYPES.get(ruleIndex);
    }
}
