package org.zk.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.ui.FloatingImageWindow;
import org.zk.ip.ui.ScreenCaptureOverlay;
import org.zk.ip.utils.Icons;

import javax.swing.*;
import java.awt.*;

/**
 * @author zk
 * 截图功能分离按钮组 - 使用IntelliJ内置的SplitButtonAction
 */
public class ScreenCaptureActionGroup extends DefaultActionGroup {

    public ScreenCaptureActionGroup() {
        super("截图选项", true);

        // 设置主按钮的图标和描述
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.CAMERA);
        presentation.setText("截图");
        presentation.setDescription("截图功能");

        // 获取ActionGroup并添加子动作
        this.add(new ScreenCaptureAction());
        this.add(new ScreenCaptureHideWindowAction());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 主按钮点击时执行默认截图功能
        new ScreenCaptureAction().actionPerformed(e);
    }

    /**
     * 截取屏幕区域
     */
    public static class ScreenCaptureAction extends AnAction {
        private static FloatingImageWindow currentWindow;

        public ScreenCaptureAction() {
            super("截取屏幕区域", "截取屏幕区域", null);
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

    /**
     * 截取屏幕区域时隐藏IDEA窗口
     */
    public static class ScreenCaptureHideWindowAction extends AnAction {
        private static FloatingImageWindow currentWindow;

        public ScreenCaptureHideWindowAction() {
            super("截图时隐藏当前窗口", "截取屏幕区域时隐藏IDEA窗口", null);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            // 先销毁旧窗口
            if (currentWindow != null) {
                currentWindow.disposeWindow();
                currentWindow = null;
            }

            // 获取IDEA主窗口
            Window ideaWindow = WindowManager.getInstance().getFrame(e.getProject());

            // 弹出区域选择覆盖层
            try {
                // 隐藏IDEA窗口
                if (ideaWindow != null) {
                    ideaWindow.setVisible(false);
                }

                // 延迟一点时间确保窗口完全隐藏
                SwingUtilities.invokeLater(() -> {
                    try {
                        // 再延迟一点确保屏幕刷新
                        Thread.sleep(100);

                        ScreenCaptureOverlay overlay = new ScreenCaptureOverlay(() -> {
                            // 截图完成后恢复IDEA窗口
                            if (ideaWindow != null) {
                                ideaWindow.setVisible(true);
                                ideaWindow.toFront();
                            }
                        });
                        overlay.setVisible(true);
                        overlay.requestFocus();
                    } catch (Exception ex) {
                        // 如果出错，确保恢复窗口
                        if (ideaWindow != null) {
                            ideaWindow.setVisible(true);
                            ideaWindow.toFront();
                        }
                        ex.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                // 如果出错，确保恢复窗口
                if (ideaWindow != null) {
                    ideaWindow.setVisible(true);
                    ideaWindow.toFront();
                }
                ex.printStackTrace();
            }
        }
    }
}