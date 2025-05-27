package org.zk.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.ui.SimpleFloatingWindow;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.SwingUtilities;

/**
 * 悬浮剪贴板动作
 */
public class FloatingClipboardAction extends AnAction {

    public FloatingClipboardAction() {
        super("悬浮复制内容");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        // 获取剪贴板内容
        CopyPasteManager copyPasteManager = CopyPasteManager.getInstance();
        Transferable contents = copyPasteManager.getContents();

        if (contents != null) {
            try {
                if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    if (!clipboardText.trim().isEmpty()) {
                        // 创建悬浮窗显示剪贴板内容
                        SwingUtilities.invokeLater(() -> {
                            SimpleFloatingWindow floatingWindow = new SimpleFloatingWindow(clipboardText);
                            floatingWindow.show();
                        });
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只在编辑器中显示此菜单项
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getProject();

        boolean enabled = editor != null && project != null;

        // 检查剪贴板是否有内容
        if (enabled) {
            CopyPasteManager copyPasteManager = CopyPasteManager.getInstance();
            Transferable contents = copyPasteManager.getContents();
            enabled = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

            if (enabled) {
                try {
                    String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    enabled = !clipboardText.trim().isEmpty();
                } catch (Exception ex) {
                    enabled = false;
                }
            }
        }

        e.getPresentation().setEnabledAndVisible(enabled);
    }
}