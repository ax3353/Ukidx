package org.zk.ip.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.service.NotebookToolWindowService;

import javax.swing.*;
import java.awt.*;

/**
 * @author zk
 */
public class NotebookToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        // 创建空编辑器，初始化ToolWindow内容
        Document document = EditorFactory.getInstance().createDocument("");
        Editor editor = EditorFactory.getInstance().createEditor(document, project, PlainTextFileType.INSTANCE, false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(editor.getComponent(), BorderLayout.CENTER);
        panel.setBorder(JBUI.Borders.empty());

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);

        // 设置全局静态引用
        NotebookToolWindowService.setEditor(editor);
    }
}
