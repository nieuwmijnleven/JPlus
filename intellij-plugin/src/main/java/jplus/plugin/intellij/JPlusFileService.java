package jplus.plugin.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import jplus.processor.JPlusProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JPlusFileService {

    private final Project project;

    public JPlusFileService(Project project) {
        this.project = project;
    }

    /**
     * .jplus 파일을 분석하여 대응되는 .java 파일 생성
     */
    public void compileAndWriteToJava(VirtualFile jplusFile, String jplusCode) {
        try {
            JPlusProcessor processor = new JPlusProcessor(jplusCode);
            processor.process();

            var issues = processor.checkNullability();
            if (!issues.isEmpty()) {
                System.err.println("❌ Nullability check failed.");
                return;
            }

            processor.analyzeSymbols();
            String javaCode = processor.generateJavaCode();
            System.out.println("javaCode = " + javaCode);

            // Step 2: IDE 내부에서 안전하게 파일 생성
            Path jplusPath = Paths.get(jplusFile.getPath());
            String javaFileName = jplusPath.getFileName().toString().replaceFirst("\\.jplus$", ".java");
            Path javaFilePath = jplusPath.resolveSibling(javaFileName);

            ApplicationManager.getApplication().invokeLater(() -> {
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    try {
                        VirtualFile javaVirtualFile = LocalFileSystem.getInstance()
                                .findFileByPath(javaFilePath.toString());
                        if (javaVirtualFile == null) {
                            javaVirtualFile = jplusFile.getParent().createChildData(this, javaFileName);
                        }

                        PsiFile psiFile = PsiManager.getInstance(project).findFile(javaVirtualFile);
                        if (psiFile != null) {
                            var doc = PsiDocumentManager.getInstance(project).getDocument(psiFile);
                            if (doc != null) {
                                doc.setText(javaCode);
                                PsiDocumentManager.getInstance(project).commitDocument(doc);
                                FileDocumentManager.getInstance().saveDocument(doc);
                            }
                        } else {
                            javaVirtualFile.setBinaryContent(javaCode.getBytes());
                        }

//                    FileDocumentManager.getInstance().saveAllDocuments();
                        System.out.println("✅ JPlus compiled → " + javaFilePath);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
