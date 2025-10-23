package jplus.plugin.intellij;

import com.intellij.lang.Language;

public class JPlusLanguage extends Language {
    public static final JPlusLanguage INSTANCE = new JPlusLanguage();

    private JPlusLanguage() {
        super("JPlus");
    }
}

