package org.zk.ip.find;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.util.TextRange;

/**
 * @Description 搜索下一个关键字
 * @return
 * @Author kun.zhu
 * @Date 2018/9/12 11:04
 */
public class SeekNextKeyword extends MyAction {

	@Override
	protected Offset offset(Editor editor, String keyword) {
		SelectionModel selectionModel = editor.getSelectionModel();
		Document document = editor.getDocument();

		int end = selectionModel.getSelectionEnd();
		String fragmentTxt = document.getText(new TextRange(end, document.getTextLength()));
		int index = fragmentTxt.indexOf(keyword);
		int startOffset = (index == -1) ? document.getText().indexOf(keyword) : end + index;
		int endOffset = startOffset + keyword.length();
		if (startOffset == -1) {
			return null;
		}
		return new Offset(startOffset, endOffset);
	}
}
