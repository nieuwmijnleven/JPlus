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

    private final CodeGeneratorContext codeGeneratorCtx = CodeGeneratorContext.getInstance();

    public JPlusParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    public Optional<JPlusParserRuleContext> _getParent() {
        return Optional.ofNullable((JPlusParserRuleContext) this.parent);
    }

    @Override
    public String getText() {
        if (this instanceof Start_Context startContextCtx) {
            String original = getTokenString(startContextCtx);
            codeGeneratorCtx.setOriginal(original);

            TextChangeRange range = Utils.getTextChangeRange(codeGeneratorCtx.getOriginal(), startContextCtx);
            String generated = processDefaultText();
            codeGeneratorCtx.setFragmentedText(new FragmentedText(range, generated));
            return generated;
        } else if (this instanceof ApplyDeclarationContext ApplyDeclarationCtx) {
            TextChangeRange range = Utils.getTextChangeRange(codeGeneratorCtx.getOriginal(), ApplyDeclarationCtx);
            String replaced = getTokenString(ApplyDeclarationCtx).replaceFirst("^", "//").replaceAll("\n", "\n//");
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
        String original = getTokenString(ctx);
        if (ctx.QUESTION() != null) {
            String replaced = original.substring(0, original.length()-1);
            TextChangeRange range = Utils.getTextChangeRange(codeGeneratorCtx.getOriginal(), ctx);
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

        TextChangeRange range = Utils.getTextChangeRange(codeGeneratorCtx.getOriginal(), ctx);
        _getParent().ifPresent(parent -> parent.addTextChangeRange(range, replaced));

        return replaced;
    }

    private String replaceNullsafeOperator(ParserRuleContext ctx) {
        String tokenString = getTokenString(ctx).replace("?.", ".");
        String variableName = tokenString.split("\\.")[0];

        String replaced = "(" +
                "((" + variableName + ")!=null)?" +
                "(" + tokenString + "):" +
                "null" +
                ")";

//        System.out.println("replaced = " + replaced);

        TextChangeRange range = Utils.getTextChangeRange(codeGeneratorCtx.getOriginal(), ctx);
        _getParent().ifPresent(parent -> parent.addTextChangeRange(range, replaced));

        return replaced;
    }

    private String processDefaultText() {
        String originalString = getTokenString(this);

        // force children to compute text and update map if needed
        for (int i = 0; i < getChildCount(); i++) {
            getChild(i).getText();
        }

        if (textChangeRangeStringMap.isEmpty()) {
            return originalString;
        }

        TextChangeRange range = Utils.getTextChangeRange(codeGeneratorCtx.getOriginal(), this);
        FragmentedText fragmentedText = new FragmentedText(range, originalString);
//        FragmentedText fragmentedText = codeGeneratorCtx.getFragmentedText();
        textChangeRangeStringMap.forEach(fragmentedText::update);
        _getParent().ifPresent(parent -> parent.addTextChangeRange(range, fragmentedText.toString()));
        return fragmentedText.toString();
    }

    private String getTokenString(ParserRuleContext ctx) {
        return ctx.start.getTokenSource().getInputStream().getText(
                Interval.of(ctx.start.getStartIndex(), ctx.stop.getStopIndex()));
    }

    public void addTextChangeRange(TextChangeRange range, String replaced) {
        this.textChangeRangeStringMap.put(range, replaced);
    }

    public String getFlattenedText() {
        return super.getText();
    }
}