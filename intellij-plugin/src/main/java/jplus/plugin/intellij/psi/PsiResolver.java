package jplus.plugin.intellij.psi;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PsiResolver {
    @Nullable
    public static PsiElement resolveSymbol(PsiJavaFile javaFile, String symbol) {
        Collection<PsiClass> classes = PsiTreeUtil.findChildrenOfType(javaFile, PsiClass.class);
        for (PsiClass cls : classes) {
            if (symbol.equals(cls.getName())) return cls;

            PsiElement member = resolveInsideClass(cls, symbol);
            if (member != null) return member;
        }

        Collection<PsiField> fields = PsiTreeUtil.findChildrenOfType(javaFile, PsiField.class);
        for (PsiField field : fields) {
            if (symbol.equals(field.getName())) return field;
        }

        Collection<PsiMethod> methods = PsiTreeUtil.findChildrenOfType(javaFile, PsiMethod.class);
        for (PsiMethod method : methods) {
            if (symbol.equals(method.getName())) return method;
        }

        Collection<PsiTypeElement> typeElements = PsiTreeUtil.findChildrenOfType(javaFile, PsiTypeElement.class);
        for (PsiTypeElement typeElement : typeElements) {
            if (symbol.equals(typeElement.getText())) return typeElement;
        }

        return null;
    }

    @Nullable
    private static PsiElement resolveInsideClass(PsiClass psiClass, String symbol) {
        for (PsiClass innerClass : psiClass.getInnerClasses()) {
            if (symbol.equals(innerClass.getName())) return innerClass;

            PsiElement member = resolveInsideClass(innerClass, symbol);
            if (member != null) return member;
        }

        for (PsiField field : psiClass.getFields()) {
            if (symbol.equals(field.getName())) return field;

            PsiTypeElement typeElement = field.getTypeElement();
            if (typeElement != null && symbol.equals(typeElement.getText())) return typeElement;
        }

        for (PsiMethod method : psiClass.getMethods()) {
            if (symbol.equals(method.getName())) return method;

            PsiTypeElement returnType = method.getReturnTypeElement();
            if (returnType != null && symbol.equals(returnType.getText())) return returnType;

            for (PsiParameter param : method.getParameterList().getParameters()) {
                if (symbol.equals(param.getName())) return param;

                PsiTypeElement paramType = param.getTypeElement();
                if (paramType != null && symbol.equals(paramType.getText())) return paramType;
            }

            PsiCodeBlock body = method.getBody();
            if (body != null) {
                PsiElement localVar = findLocalVariable(body, symbol);
                if (localVar != null) return localVar;
            }
        }

        return null;
    }

    @Nullable
    private static PsiElement findLocalVariable(PsiElement element, String symbol) {
        Collection<PsiLocalVariable> locals = PsiTreeUtil.findChildrenOfType(element, PsiLocalVariable.class);
        for (PsiLocalVariable local : locals) {
            if (symbol.equals(local.getName())) return local;

            PsiTypeElement typeElement = local.getTypeElement();
            if (typeElement != null && symbol.equals(typeElement.getText())) return typeElement;
        }
        return null;
    }
}
