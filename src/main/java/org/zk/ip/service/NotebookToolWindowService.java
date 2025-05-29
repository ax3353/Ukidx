package org.zk.ip.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;

/**
 * @author zk
 */
public class NotebookToolWindowService {
    private static Editor editor;

    public static void setEditor(Editor ed) {
        editor = ed;
    }

    public static void updateContent(String text) {
        if (editor == null) {
            return;
        }

        ApplicationManager.getApplication().runWriteAction(() -> {
            editor.getDocument().setText(text);
            editor.getCaretModel().moveToOffset(text.length());
        });
    }
}
