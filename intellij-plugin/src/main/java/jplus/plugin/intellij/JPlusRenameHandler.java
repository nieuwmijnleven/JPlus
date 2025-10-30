package jplus.plugin.intellij;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.refactoring.rename.RenameProcessor;
import jplus.plugin.intellij.psi.PsiElementWrapper;
import jplus.plugin.intellij.util.JPlusUtil;
import org.jetbrains.annotations.NotNull;

public class JPlusRenameHandler implements RenameHandler {
    
    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
//        if (elements.length != 1) return;
//        PsiElement element = elements[0];
//
//        RenameDialog dialog = new RenameDialog(project, element, element.getContext(), null);
//        dialog.show();
//
//        if (dialog.isOK()) {
//            String newName = dialog.getNewName();
//
//            RenameProcessor processor = new RenameProcessor(project, element, newName, true, true);
//            processor.run();
//        }
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        PsiJavaFile psiJavaFile = JPlusUtil.createJavaPsiFromJPlus(project, file);
        PsiElement element = psiJavaFile.findElementAt(editor.getCaretModel().getOffset());
        if (element == null) return;

        RenameDialog dialog = new RenameDialog(project, element, element.getContext(), editor);
        dialog.show();

        if (dialog.isOK()) {
            String newName = dialog.getNewName();

            PsiElement target = new PsiElementWrapper(element, file);
            RenameProcessor processor = new RenameProcessor(project, target, newName, true, true);
            processor.run();
        }

    }

    @Override
    public boolean isAvailableOnDataContext(@NotNull DataContext dataContext) {
        return true;
    }

//    @Override
//    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
//        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
//        invoke(project, new PsiElement[]{element}, dataContext);
//    }
}