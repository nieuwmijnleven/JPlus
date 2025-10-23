package jplus.plugin.intellij.context;

import com.intellij.psi.PsiFile;
import com.intellij.openapi.project.Project;

public class JPlusContext {

    private static JPlusContext instance = new JPlusContext();

    private PsiFile JPlusFile;

    private Project project;

    private JPlusContext() {}

    public static JPlusContext getInstance() {
        return instance;
    }

    public PsiFile getJPlusFile() {
        return JPlusFile;
    }

    public void setJPlusFile(PsiFile JPlusFile) {
        this.JPlusFile = JPlusFile;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
