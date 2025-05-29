package org.zk.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.service.NotebookToolWindowService;
import org.zk.ip.utils.Icons;

/**
 * @author zk
 */
public class ClearNotebookAction extends AnAction {

    public ClearNotebookAction() {
        super("清空内容", "Clear Notebook Content", Icons.CLEAR);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ApplicationManager.getApplication().runWriteAction(() -> NotebookToolWindowService.updateContent(""));
    }
}
