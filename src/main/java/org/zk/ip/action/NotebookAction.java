package org.zk.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.zk.ip.service.NotebookToolWindowService;

import java.util.Objects;

/**
 * @author zk
 */
public class NotebookAction extends AnAction {

    public NotebookAction() {
        super("复制到Notebook");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        // 获取当前选中内容
        String selectedText = e.getData(CommonDataKeys.EDITOR) != null
                ? Objects.requireNonNull(e.getData(CommonDataKeys.EDITOR)).getSelectionModel().getSelectedText()
                : null;

        if (selectedText == null || selectedText.isEmpty()) {
            return;
        }

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notebook");
        if (toolWindow == null) {
            return;
        }

        // 更新内容
        NotebookToolWindowService.updateContent(selectedText);
    }

    @Override
    public void update(AnActionEvent e) {
        boolean hasSelection = e.getData(CommonDataKeys.EDITOR) != null &&
                Objects.requireNonNull(e.getData(CommonDataKeys.EDITOR)).getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(hasSelection);
    }
}
