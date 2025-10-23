package jplus.plugin.intellij;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import jplus.base.JPlus20Lexer;
import jplus.base.JPlus20Parser;
import jplus.plugin.intellij.adapter.JPlusLexerAdapter;
import jplus.plugin.intellij.adapter.JPlusParserAdapter;
import jplus.plugin.intellij.psi.ApplyBlockPsiElement;
import jplus.plugin.intellij.psi.ApplyStatementPsiElement;
import jplus.plugin.intellij.psi.IdentifierPsiElement;
import jplus.plugin.intellij.psi.MethodPsiElement;
import jplus.plugin.intellij.psi.NormalClassDeclarationPsiElement;
import org.antlr.intellij.adaptor.lexer.RuleIElementType;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.jetbrains.annotations.NotNull;

public class JPlusParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE =
            new IFileElementType(JPlusLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new JPlusLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new JPlusParserAdapter();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return JPlusTokenTypes.WHITESPACES;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return JPlusTokenTypes.COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return JPlusTokenTypes.STRING_LITERALS;
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        IElementType iElementType = node.getElementType();
        if (iElementType instanceof TokenIElementType) {
            return new ANTLRPsiNode(node);
        }

        if (!(iElementType instanceof RuleIElementType)) {
            return new ANTLRPsiNode(node);
        }

        RuleIElementType ruleElementType = (RuleIElementType)iElementType;
        switch (ruleElementType.getRuleIndex()) {
            case JPlus20Parser.RULE_applyBlock:
                return new ApplyBlockPsiElement(node);
            case JPlus20Parser.RULE_applyStatement:
                return new ApplyStatementPsiElement(node);
            case JPlus20Parser.RULE_methodDeclaration:
                return new MethodPsiElement(node, ruleElementType);
            case JPlus20Parser.RULE_normalClassDeclaration:
                return new NormalClassDeclarationPsiElement(node);
        }

        return new ANTLRPsiNode(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
        return new JPlusFile(fileViewProvider);
    }
}
