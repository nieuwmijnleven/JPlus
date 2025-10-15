package jplus.generator;

import jplus.base.JPlus20Parser.NullCoalescingExpressionContext;
import jplus.base.JPlus20Parser.UnannTypeContext;
import jplus.base.JPlus20Parser.PrimaryNoNewArrayContext;
import jplus.util.FragmentedText;
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
        return Optional.ofNullable((JPlusParserRuleContext)this.parent);
    }

    @Override
    public String getText() {
        if (this instanceof UnannTypeContext) {
            UnannTypeContext ctx = (UnannTypeContext)this;
            if (ctx.unannReferenceType() != null) {
                TextChangeRange range = getTextChangeRange(ctx);
                String replaced = ctx.unannReferenceType().getText();
                _getParent().ifPresent(parent ->{
                    parent.addTextChangeRange(range, replaced);
                });
                return ctx.unannReferenceType().getText();
            }
        } else if (this instanceof NullCoalescingExpressionContext) {
            NullCoalescingExpressionContext ctx = (NullCoalescingExpressionContext) this;
            if (ctx.ELVIS() != null) {
                StringBuilder sb = new StringBuilder();
                String conditionalOrExpression = ctx.conditionalOrExpression().getText();
                String expression = "null";
                if (ctx.nullCoalescingExpression() != null) {
                    expression = ctx.nullCoalescingExpression().getText();
                } else if (ctx.lambdaExpression() != null) {
                    expression = ctx.lambdaExpression().getText();
                }

                sb.append("(");
                sb.append("(").append("(").append(conditionalOrExpression).append(")").append("!=").append("null").append(")").append("?").append("(").append(conditionalOrExpression).append(")").append(":").append("(").append(expression).append(")");
                sb.append(")");

                TextChangeRange range = getTextChangeRange(ctx);
                String replaced = sb.toString();
                _getParent().ifPresent(parent -> {
                    parent.addTextChangeRange(range, replaced);
                });
                return replaced;
            }
        } else if (this instanceof PrimaryNoNewArrayContext) {
            PrimaryNoNewArrayContext ctx = (PrimaryNoNewArrayContext)this;
            if (ctx.NULLSAFE() != null) {
                StringBuilder sb = new StringBuilder();
                String tokenString = getTokenString(ctx).replace("?.", ".");
                String variableName = tokenString.split("\\.")[0];
                sb.append("(");
                sb.append("(").append('(').append(variableName).append(')').append("!=").append("null").append(")").append("?");
                sb.append('(').append(tokenString).append(')').append(":").append("null");
                sb.append(")");

                TextChangeRange range = getTextChangeRange(ctx);
                String replaced = sb.toString();
                _getParent().ifPresent(parent -> {
                    parent.addTextChangeRange(range, replaced);
                });
                return replaced;
            }
        }

        String originalString = this.start.getTokenSource().getInputStream().getText(Interval.of(this.start.getStartIndex(), this.stop.getStopIndex()));


        for (int i = 0; i < this.getChildCount(); i++) {
            this.getChild(i).getText();
        }

        if (textChangeRangeStringMap.isEmpty()) {
            return originalString;
        }

        TextChangeRange range = getTextChangeRange(this);
        FragmentedText fragmentedText = new FragmentedText(range, originalString);
        textChangeRangeStringMap.forEach((textChangeRange, replaced) -> {
            fragmentedText.update(textChangeRange, replaced);
        });
        _getParent().ifPresent(parent -> {
            parent.addTextChangeRange(range, fragmentedText.toString());
        });
        return fragmentedText.toString();
    }

    private TextChangeRange getTextChangeRange(ParserRuleContext ctx) {
        int startLine = ctx.start.getLine();
        int startIndex = ctx.start.getCharPositionInLine();
        int endLine = ctx.stop.getLine();
        int endIndex = ctx.stop.getCharPositionInLine() + this.stop.getText().length() - 1;
        return new TextChangeRange(startLine, startIndex, endLine, endIndex);
    }

    private String getTokenString(ParserRuleContext ctx) {
        return ctx.start.getTokenSource().getInputStream().getText(Interval.of(ctx.start.getStartIndex(), ctx.stop.getStopIndex()));
    }

    public void addTextChangeRange(TextChangeRange range, String replaced) {
        this.textChangeRangeStringMap.put(range, replaced);
    }

    public String getFlattenedText() {
        return super.getText();
    }
}

