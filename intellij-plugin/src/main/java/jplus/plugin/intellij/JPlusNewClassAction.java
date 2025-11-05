package jplus.plugin.intellij;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JPlusNewClassAction extends CreateFileFromTemplateAction implements DumbAware {

    public JPlusNewClassAction() {
        super("JPlus Class", "Create a new JPlus Class", AllIcons.Nodes.Class);
    }

    @Override
    protected void buildDialog(@NotNull final Project project, @NotNull PsiDirectory directory,
                               @NotNull CreateFileFromTemplateDialog.Builder builder) {
        builder
                .setTitle("New JPlus Class")
                .addKind("Class", AllIcons.Nodes.Class, "Class")
                .addKind("Interface", AllIcons.Nodes.Interface, "Interface")
                .addKind("Enum", AllIcons.Nodes.Enum, "Enum")
                .addKind("Annotation", AllIcons.Nodes.Annotationtype, "AnnotationType");

        builder.setValidator(new InputValidatorEx() {
            @Override
            public String getErrorText(String inputString) {
                if (!inputString.isEmpty() && !PsiNameHelper.getInstance(project).isQualifiedName(inputString)) {
                    return "Invalid Java qualified name";
                }
                String shortName = StringUtil.getShortName(inputString);
                if (!PsiNameHelper.getInstance(project).isIdentifier(shortName)) {
                    return "Invalid Java identifier";
                }
                return null;
            }

            @Override
            public boolean checkInput(String inputString) {
                return true;
            }

            @Override
            public boolean canClose(String inputString) {
                return !StringUtil.isEmptyOrSpaces(inputString) && getErrorText(inputString) == null;
            }
        });
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(directory);
        String packageName = (psiPackage != null) ? psiPackage.getQualifiedName() : "";
        return "Creating class " + StringUtil.getQualifiedName(packageName, newName);
    }

    @Override
    protected PsiFile createFileFromTemplate(@NotNull String name, @NotNull FileTemplate template, @NotNull PsiDirectory dir) {
        PsiElement created = null;
        try {
            created = FileTemplateUtil.createFromTemplate(template, name, null, dir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (created instanceof PsiClass) {
            postProcess((PsiClass) created);
        }
        return created.getContainingFile();
    }

    private void postProcess(@NotNull PsiClass createdElement) {
        PsiFile originalFile = createdElement.getContainingFile();
        VirtualFile originalVFile = originalFile.getVirtualFile();

        if (originalVFile == null) return;
        Project project = originalFile.getProject();

        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    VirtualFile parent = originalVFile.getParent();
                    String newFileName = originalVFile.getNameWithoutExtension() + ".jplus";
                    VirtualFile jplusFile = parent.findChild(newFileName);

                    if (jplusFile == null) {
                        jplusFile = parent.createChildData(this, newFileName);
                        byte[] content = originalVFile.contentsToByteArray();
                        jplusFile.setBinaryContent(content);
                    }

                    VirtualFile finalJplusFile = jplusFile;
                    ApplicationManager.getApplication().invokeLater(() ->
                            FileEditorManager.getInstance(project).openFile(finalJplusFile, true));

                } catch (IOException e) {
                    Messages.showErrorDialog(project, e.getMessage(), "Error Creating .jplus File");
                }
            });
        });
    }
}
