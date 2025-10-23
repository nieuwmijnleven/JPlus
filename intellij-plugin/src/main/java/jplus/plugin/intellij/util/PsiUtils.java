package jplus.plugin.intellij.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.SmartList;
import jplus.plugin.intellij.psi.NormalClassDeclarationPsiElement;

import java.util.ArrayList;
import java.util.List;

public class PsiUtils {

    private PsiUtils() {}

    public static List<String> getClassNameList(PsiElement element) {
        List<String> classNameList = new ArrayList<>();
        getClassList(element, "", classNameList);
        return classNameList;
    }

    private static void getClassList(PsiElement element, String currentClassName, List<String> result) {
        if (element == null) return;

        var klass = NormalClassDeclarationPsiElement.class;
        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (klass.isInstance(child)) {
                var castedChild = klass.cast(child);
                String className = castedChild.getName();
                currentClassName += (currentClassName.length() > 0) ? "." + className : className;
                result.add(currentClassName);
            }

            getClassList(child, currentClassName, result);
        }
    }


}
