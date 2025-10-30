package jplus.plugin.intellij;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class JPlusSaveListener implements FileDocumentManagerListener {

    private final Project project;
    private final JPlusFileService fileService;

    public JPlusSaveListener(Project project) {
        this.project = project;
        this.fileService = new JPlusFileService(project);
    }

    @Override
    public void beforeDocumentSaving(@NotNull com.intellij.openapi.editor.Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file != null && file.getName().endsWith(".jplus")) {
            fileService.compileAndWriteToJava(file, document.getText());
        }
    }
}
