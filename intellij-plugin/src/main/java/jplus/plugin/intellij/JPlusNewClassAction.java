//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package jplus.plugin.intellij;

import com.intellij.codeInsight.daemon.JavaErrorBundle;
import com.intellij.core.JavaPsiBundle;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.JavaCreateTemplateInPackageAction;
import com.intellij.ide.fileTemplates.CreateFromTemplateHandler;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.fileTemplates.JavaCreateFromTemplateHandler;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.java.JavaBundle;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.JavaFeature;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiRecordHeader;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.IconManager;
import com.intellij.ui.PlatformIcons;
import com.intellij.util.IncorrectOperationException;

import java.io.IOException;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class JPlusNewClassAction extends JavaCreateTemplateInPackageAction<PsiClass> implements DumbAware {
    public JPlusNewClassAction() {
        super("", "JPlus Class", IconManager.getInstance().getPlatformIcon(PlatformIcons.Class), true);
    }

    protected void buildDialog(@NotNull final Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("New JPlus Class").addKind(JavaPsiBundle.message("node.class.tooltip", new Object[0]), IconManager.getInstance().getPlatformIcon(PlatformIcons.Class), "Class").addKind(JavaPsiBundle.message("node.interface.tooltip", new Object[0]), com.intellij.util.PlatformIcons.INTERFACE_ICON, "Interface");
        final LanguageLevel level = PsiUtil.getLanguageLevel(directory);
        if (JavaFeature.RECORDS.isSufficient(level)) {
            builder.addKind(JavaPsiBundle.message("node.record.tooltip", new Object[0]), com.intellij.util.PlatformIcons.RECORD_ICON, "Record");
        }

        if (JavaFeature.ENUMS.isSufficient(level)) {
            builder.addKind(JavaPsiBundle.message("node.enum.tooltip", new Object[0]), com.intellij.util.PlatformIcons.ENUM_ICON, "Enum");
        }

        if (JavaFeature.ANNOTATIONS.isSufficient(level)) {
            builder.addKind(JavaPsiBundle.message("node.annotation.tooltip", new Object[0]), com.intellij.util.PlatformIcons.ANNOTATION_TYPE_ICON, "AnnotationType");
        }

        builder.addKind(JavaPsiBundle.message("node.exception.tooltip", new Object[0]), com.intellij.util.PlatformIcons.EXCEPTION_CLASS_ICON, "Exception");
        PsiDirectory[] dirs = new PsiDirectory[]{directory};

        for(FileTemplate template : FileTemplateManager.getInstance(project).getAllTemplates()) {
            CreateFromTemplateHandler handler = FileTemplateUtil.findHandler(template);
            if (handler instanceof JavaCreateFromTemplateHandler && handler.handlesTemplate(template) && handler.canCreate(dirs)) {
                builder.addKind(template.getName(), JavaFileType.INSTANCE.getIcon(), template.getName());
            }
        }

        builder.setValidator(new InputValidatorEx() {
            public String getErrorText(String inputString) {
                if (!inputString.isEmpty() && !PsiNameHelper.getInstance(project).isQualifiedName(inputString)) {
                    return JavaErrorBundle.message("create.class.action.this.not.valid.java.qualified.name", new Object[0]);
                } else {
                    String shortName = StringUtil.getShortName(inputString);
                    return PsiTypesUtil.isRestrictedIdentifier(shortName, level) ? JavaErrorBundle.message("restricted.identifier", new Object[]{shortName}) : null;
                }
            }

            public boolean checkInput(String inputString) {
                return true;
            }

            public boolean canClose(String inputString) {
                return !StringUtil.isEmptyOrSpaces(inputString) && this.getErrorText(inputString) == null;
            }
        });
    }

    protected String removeExtension(String templateName, String className) {
        return StringUtil.trimEnd(className, ".jplus");
    }

    protected @NotNull String getErrorTitle() {
        return JavaBundle.message("title.cannot.create.class", new Object[0]);
    }

    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(directory);
        return JavaBundle.message("progress.creating.class", new Object[]{StringUtil.getQualifiedName(psiPackage == null ? "" : psiPackage.getQualifiedName(), newName)});
    }

    public boolean startInWriteAction() {
        return false;
    }

    protected final PsiClass doCreate(PsiDirectory dir, String className, String templateName) throws IncorrectOperationException {
        PsiClass psiClass = JavaDirectoryService.getInstance().createClass(dir, className, templateName, true);
        return psiClass;
    }

    protected PsiElement getNavigationElement(@NotNull PsiClass createdElement) {
        if (createdElement.isRecord()) {
            PsiRecordHeader header = createdElement.getRecordHeader();
            if (header != null) {
                return header.getLastChild();
            }
        }

        return createdElement.getLBrace();
    }

    protected void postProcess(@NotNull PsiClass createdElement, String templateName, Map<String, String> customProperties) {
        super.postProcess(createdElement, templateName, customProperties);

        PsiFile originalFile = createdElement.getContainingFile();
        VirtualFile originalVFile = originalFile.getVirtualFile();

        if (originalVFile != null) {
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
                        ApplicationManager.getApplication().invokeLater(() -> {
                            FileEditorManager.getInstance(project).openFile(finalJplusFile, true);
                        });

//                        originalVFile.delete(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }
}
