package org.zk.ip.find;

/**
 * @description:
 * @author: kun.zhu
 * @create: 2018-11-27 17:47
 **/
public class Offset {

	int startOffset;

	int endOffset;

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public Offset() {
	}

	public Offset(int startOffset, int endOffset) {
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}
}
