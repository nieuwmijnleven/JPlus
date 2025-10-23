package jplus.plugin.intellij.adapter;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import jplus.base.JPlus20Parser;
import jplus.plugin.intellij.JPlusLanguage;
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

public class JPlusParserAdapter extends ANTLRParserAdaptor {

    public JPlusParserAdapter() {
        super(JPlusLanguage.INSTANCE, new JPlus20Parser(null));
    }

    @Override
    protected ParseTree parse(Parser parser, IElementType root) {
        if (root instanceof IFileElementType) {
            return ((JPlus20Parser) parser).start_();
        }

        throw new UnsupportedOperationException(String.format("cannot start parsing using root element %s", root));
    }
}
