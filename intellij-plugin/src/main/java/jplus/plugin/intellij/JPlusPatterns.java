package jplus.plugin.intellij;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;

public class JPlusPatterns {

    // JPlus PSI에서 모든 위치를 기본으로 잡는 패턴
    public static ElementPattern<PsiElement> psiElement() {
        return PlatformPatterns.psiElement(PsiElement.class);
    }

    // 특정 JPlus PSI 타입만 자동완성 대상로 지정할 수도 있음
    // 예: JPlusIdentifier 타입
    /*
    public static ElementPattern<PsiElement> identifier() {
        return PlatformPatterns.psiElement(JPlusTypes.IDENTIFIER);
    }
    */
}
