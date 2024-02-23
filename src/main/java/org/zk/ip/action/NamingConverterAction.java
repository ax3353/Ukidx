package org.zk.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.zk.ip.utils.NamingConverter;

/**
 * @author kun.zhu
 */
public class NamingConverterAction extends AnAction {

    @Override
    public void update(final AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        // 如果没有项目打开或没有文本被选中则不显示action
        e.getPresentation().setVisible((project != null && editor != null
                && editor.getSelectionModel().hasSelection()));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            Messages.showMessageDialog(project, "Please select a field name to convert.", "Field Name Converter", Messages.getInformationIcon());
            return;
        }

        String convertedName = NamingConverter.convertFieldName(selectedText);
        Document document = editor.getDocument();
        WriteCommandAction.runWriteCommandAction(project,
                () -> document.replaceString(editor.getSelectionModel().getSelectionStart(),
                        editor.getSelectionModel().getSelectionEnd(),
                        convertedName));
    }
}