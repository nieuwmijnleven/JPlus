package jplus.plugin.intellij;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.Icons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JPlusFileType extends LanguageFileType {
    public static final JPlusFileType INSTANCE = new JPlusFileType();
    
    protected JPlusFileType() {
        super(JPlusLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "JPlus";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "JPlus";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "jplus";
    }

    @Override
    public Icon getIcon() {
        return Icons.JAVA_OUTSIDE_SOURCE_ICON;
    }
}
