package jplus.plugin.intellij;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class JPlusFile extends PsiFileBase {
    protected JPlusFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, JPlusLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return new JPlusFileType();
    }

    @Override
    public String toString() {
        return "JPlus file";
    }
}
