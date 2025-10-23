package jplus.plugin.intellij;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.plugin.intellij.adapter.JPlusLexerAdapter;
import jplus.plugin.intellij.psi.MethodPsiElement;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class JPlusSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("JPlus_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey INSTANCE_METHOD_ID =
            createTextAttributesKey("JPlus_INSTANCE_METHOD_ID", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("JPlus_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("JPlus_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("JPlus_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);

    private static final TextAttributesKey[] BAD_CHAR_KEYS = pack(HighlighterColors.BAD_CHARACTER);
    private static final TextAttributesKey[] STRING_KEYS = pack(STRING);
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[] {LINE_COMMENT, COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new JPlusLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (JPlusTokenTypes.KEYWORDS.contains(tokenType)){
            return pack(KEYWORD);
        }

//        tokenType.
//
//        if (tokenType instanceof RuleIElementType ruleIElementType) {
//            switch(ruleIElementType.getRuleIndex()) {
//                case JPlus20Parser.RULE_methodDeclarator ->
//            }
//        }


        if (tokenType == JPlusTokenTypes.TOKEN_ELEMENT_TYPES.get(JPlus20Lexer.StringLiteral)) {
            return STRING_KEYS;
        }
        else if (tokenType == JPlusTokenTypes.TOKEN_ELEMENT_TYPES.get(JPlus20Lexer.COMMENT)) {
            return COMMENT_KEYS;
        }
        else if (tokenType == JPlusTokenTypes.TOKEN_ELEMENT_TYPES.get(JPlus20Lexer.LINE_COMMENT)) {
            return COMMENT_KEYS;
        }
        else if (tokenType == JPlusTokenTypes.BAD_TOKEN_TYPE) {
            return BAD_CHAR_KEYS;
        }
        else {
            return EMPTY_KEYS;
        }
    }
}
