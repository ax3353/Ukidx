package org.zk.ip.find;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;

/**
 * @Description 搜索上一个关键字
 * @return
 * @Author kun.zhu
 * @Date 2018/9/12 11:03
 */
public class SeekPreviousKeyword extends MyAction {

	@Override
	protected Offset offset(Editor editor, String keyword) {
		SelectionModel selectionModel = editor.getSelectionModel();
		Document document = editor.getDocument();

		int start = selectionModel.getSelectionStart();
		String fragmentTxt = document.getText(new TextRange(0, start));
		int index = fragmentTxt.lastIndexOf(keyword);
		int startOffset = (index == -1) ? document.getText().lastIndexOf(keyword) : index;
		int endOffset = startOffset + keyword.length();
		if (startOffset == -1) {
			return null;
		}
		return new Offset(startOffset, endOffset);
	}
}
