package org.zk.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.zk.ip.window.FloatingImageWindow;
import org.zk.ip.window.ScreenCaptureOverlay;

import javax.swing.*;
import java.awt.*;

public class ScreenCaptureAction extends AnAction {
    private static FloatingImageWindow currentWindow;

    public ScreenCaptureAction() {
        super("截图", "截取屏幕区域", new ImageIcon(ScreenCaptureAction.class.getResource("/icons/camera.svg")));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 先销毁旧窗口
        if (currentWindow != null) {
            currentWindow.disposeWindow();
            currentWindow = null;
        }
        // 弹出区域选择覆盖层
        ScreenCaptureOverlay overlay;
        try {
            overlay = new ScreenCaptureOverlay(subImg -> {
                currentWindow = new FloatingImageWindow(subImg);
                currentWindow.setVisible(true);
            });
            overlay.setVisible(true);
            overlay.requestFocus();
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }
}