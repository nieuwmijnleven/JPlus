package jplus.generator;

import jplus.base.JPlus20Parser.*;
import jplus.util.FragmentedText;
import jplus.util.Utils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JPlusParserRuleContext extends ParserRuleContext {

    private final Map<TextChangeRange, String> textChangeRangeStringMap = new HashMap<>();

    public JPlusParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    public Optional<JPlusParserRuleContext> _getParent() {
        return Optional.ofNullable((JPlusParserRuleContext) this.parent);
    }

    @Override
    public String getText() {
        if (this instanceof ApplyDeclarationContext ApplyDeclarationCtx) {
            String originalText = ApplyDeclarationCtx.start.getInputStream().toString();
            TextChangeRange range = Utils.getTextChangeRange(originalText, ApplyDeclarationCtx);
            String replaced = Utils.getTokenString(ApplyDeclarationCtx).replaceFirst("^", "//").replaceAll("\n", "\n//");
            _getParent().ifPresent(parent -> parent.addTextChangeRange(range, replaced));
            return null;
        } else if (this instanceof UnannTypeContext unannTypeCtx && unannTypeCtx.unannReferenceType() != null) {
            return replaceNullType(unannTypeCtx);
        } else if (this instanceof NullCoalescingExpressionContext nullCoalescingCtx && nullCoalescingCtx.ELVIS() != null) {
            return replaceElvisOperator(nullCoalescingCtx);
        } else if (this instanceof PrimaryNoNewArrayContext primaryCtx && primaryCtx.NULLSAFE() != null) {
            return replaceNullsafeOperator(primaryCtx);
        } else if (this instanceof FieldAccessContext fieldAccessCtx && fieldAccessCtx.NULLSAFE() != null) {
            return replaceNullsafeOperator(fieldAccessCtx);
        } else if (this instanceof MethodInvocationContext methodInvocationCtx && methodInvocationCtx.NULLSAFE() != null) {
            return replaceNullsafeOperator(methodInvocationCtx);
        }

        return processDefaultText();
    }

    private String replaceNullType(UnannTypeContext ctx) {
        String original = Utils.getTokenString(ctx);
        if (ctx.QUESTION() != null) {
            String replaced = original.substring(0, original.length()-1);
            String originalText = ctx.start.getInputStream().toString();
            TextChangeRange range = Utils.getTextChangeRange(originalText, ctx);
            _getParent().ifPresent(parent -> parent.addTextChangeRange(range, replaced));
            return replaced;
        }
        return original;
    }

    private String replaceElvisOperator(NullCoalescingExpressionContext ctx) {
        String conditionalOrExpression = ctx.conditionalOrExpression().getText();
        String expression = "null";

        if (ctx.nullCoalescingExpression() != null) {
            expression = ctx.nullCoalescingExpression().getText();
        } else if (ctx.lambdaExpression() != null) {
            expression = ctx.lambdaExpression().getText();
        }

        String replaced = "(" +
                "((" + conditionalOrExpression + ")!=null)?" +
                "(" + conditionalOrExpression + "):" +
                "(" + expression + ")" +
                ")";

        String originalText = ctx.start.getInputStream().toString();
        TextChangeRange range = Utils.getTextChangeRange(originalText, ctx);
        _getParent().ifPresent(parent -> parent.addTextChangeRange(range, replaced));

        return replaced;
    }

    private String replaceNullsafeOperator(ParserRuleContext ctx) {
        String tokenString = Utils.getTokenString(ctx).replace("?.", ".");
        String variableName = tokenString.split("\\.")[0];

        String replaced = "(" +
                "((" + variableName + ")!=null)?" +
                "(" + tokenString + "):" +
                "null" +
                ")";

        String originalText = this.start.getInputStream().toString();
        TextChangeRange range = Utils.getTextChangeRange(originalText, ctx);
        _getParent().ifPresent(parent -> parent.addTextChangeRange(range, replaced));

        return replaced;
    }

    private String processDefaultText() {
        String originalString = Utils.getTokenString(this);

        // force children to compute text and update map if needed
        for (int i = 0; i < getChildCount(); i++) {
            getChild(i).getText();
        }

        if (textChangeRangeStringMap.isEmpty()) {
            return originalString;
        }

        String originalText = this.start.getInputStream().toString();
        TextChangeRange range = Utils.getTextChangeRange(originalText, this);
        FragmentedText fragmentedText = new FragmentedText(range, originalString);
        textChangeRangeStringMap.forEach(fragmentedText::update);
        _getParent().ifPresent(parent -> parent.addTextChangeRange(range, fragmentedText.toString()));
        return fragmentedText.toString();
    }

    public void addTextChangeRange(TextChangeRange range, String replaced) {
        this.textChangeRangeStringMap.put(range, replaced);
    }

    public String getFlattenedText() {
        return super.getText();
    }
}