package org.zk.ip.find;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2018-11-27 15:41
 **/
public abstract class MyAction extends AnAction {

	protected static String cachaKeyword = "";

	@Override
	public void update(final AnActionEvent e) {
		final Project project = e.getProject();
		final Editor editor = e.getData(CommonDataKeys.EDITOR);
		// 如果没有项目打开或没有文本被选中则不显示action
		e.getPresentation().setVisible((project != null && editor != null
				&& editor.getSelectionModel().hasSelection()));
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		Editor editor = e.getData(PlatformDataKeys.EDITOR);
		if (editor == null) {
			return;
		}

		SelectionModel selectionModel = editor.getSelectionModel();
		ScrollingModel scrollingModel = editor.getScrollingModel();
		CaretModel caretModel = editor.getCaretModel();

		boolean hasSelection = editor.getSelectionModel().hasSelection();
		if (hasSelection) {
			cachaKeyword = selectionModel.getSelectedText();
		}

		if (cachaKeyword == null || "".equals(cachaKeyword)) {
			return;
		}

		Offset offset = this.offset(editor, cachaKeyword);
		if (offset == null) {
			return;
		}

		int startOffset = offset.getStartOffset();
		int endOffset = offset.getEndOffset();

		selectionModel.setSelection(startOffset, endOffset);
		caretModel.moveToOffset(endOffset);
		scrollingModel.scrollToCaret(ScrollType.CENTER);
	}

	protected abstract Offset offset(Editor editor, String keyword);
}
