package org.zk.ip.ui;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.action.ClearNotebookAction;
import org.zk.ip.service.NotebookToolWindowService;

import javax.swing.*;
import java.awt.*;

/**
 * @author zk
 */
public class NotebookToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // 支持 Java 高亮 + 折叠
        LanguageFileType fileType = JavaFileType.INSTANCE;
        PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("Notebook.java", fileType, "");
        Document document = psiFile.getViewProvider().getDocument();
        if (document == null) {
            return;
        }

        Editor editor = EditorFactory.getInstance().createEditor(document, project, fileType, false);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(editor.getComponent(), BorderLayout.CENTER);
        // 设置边框
        contentPanel.setBorder(JBUI.Borders.empty(1, -1, -1, -1));

        // 创建 ActionGroup
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ClearNotebookAction());

        // 创建 ToolBar
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("NotebookToolbar", actionGroup, true);
        JComponent toolbarComponent = actionToolbar.getComponent();

        // 添加到顶部
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(toolbarComponent, BorderLayout.NORTH);
        wrapperPanel.add(contentPanel, BorderLayout.CENTER);

        // 创建内容
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(wrapperPanel, "", false);
        toolWindow.getContentManager().addContent(content);

        // 注册资源释放逻辑，避免内存泄漏
        Disposer.register(content, () -> EditorFactory.getInstance().releaseEditor(editor));

        // 设置全局静态引用
        NotebookToolWindowService.setEditor(editor);
    }
}
