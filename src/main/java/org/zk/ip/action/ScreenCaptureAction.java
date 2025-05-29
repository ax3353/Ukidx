package org.zk.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.ui.FloatingImageWindow;
import org.zk.ip.ui.ScreenCaptureOverlay;
import org.zk.ip.utils.Icons;

import java.awt.*;

/**
 * @author zk
 */
public class ScreenCaptureAction extends AnAction {
    private static FloatingImageWindow currentWindow;

    public ScreenCaptureAction() {
        super("截图", "截取屏幕区域", Icons.CAMERA);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 先销毁旧窗口
        if (currentWindow != null) {
            currentWindow.disposeWindow();
            currentWindow = null;
        }

        // 弹出区域选择覆盖层
        try {
            ScreenCaptureOverlay overlay = new ScreenCaptureOverlay();
            overlay.setVisible(true);
            overlay.requestFocus();
        } catch (AWTException ignored) {
        }
    }
}