package jplus.plugin.intellij;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public class JPlusLanguageCommenter implements Commenter {
    @Override
    public String getLineCommentPrefix() {
        return "//"; // 한 줄 주석
    }

    @Override
    public String getBlockCommentPrefix() {
        return "/*"; // 블록 주석 시작
    }

    @Override
    public String getBlockCommentSuffix() {
        return "*/"; // 블록 주석 끝
    }

    @Override
    public @Nullable String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getCommentedBlockCommentSuffix() {
        return null;
    }
}
