package jplus.plugin.intellij.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NormalClassDeclarationPsiReference extends PsiReferenceBase<IdentifierPsiElement> {

    public NormalClassDeclarationPsiReference(@NotNull IdentifierPsiElement element) {
        super(element, new TextRange(0, element.getText().length()));
    }

    @Override
    public @Nullable PsiElement resolve() {
        Project project = myElement.getProject();
        String symbol = getValue();

        // Java 클래스 검색
        PsiClass psiClass = JavaPsiFacade.getInstance(project)
                .findClass(symbol, GlobalSearchScope.allScope(project));
        return psiClass;
    }
}
