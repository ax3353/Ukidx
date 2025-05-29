package org.zk.ip.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 * @author zk
 */
public class ScreenCaptureOverlay extends JWindow {
    private final BufferedImage screenImage;
    private Point startPt;
    private Rectangle captureRect;

    public ScreenCaptureOverlay() throws AWTException {
        // 1. 预抓取全屏图像
        Robot robot = new Robot();
        Rectangle screenBounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        screenImage = robot.createScreenCapture(screenBounds);

        // 2. 窗口设置：透明窗体但会自行绘制背景
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);
        setFocusableWindowState(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setSize(screenBounds.getSize());
        setLocation(0, 0);

        // 3. 鼠标事件：按下开始，拖动绘制，松开截取＋回调
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPt = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startPt == null) {
                    return;
                }
                captureRect = makeRectangle(startPt, e.getPoint());
                // 关闭覆盖层
                dispose();

                try {
                    Robot robot = new Robot();
                    BufferedImage subImg = robot.createScreenCapture(captureRect);
                    // 在事件线程中创建并定位窗口
                    SwingUtilities.invokeLater(() -> {
                        FloatingImageWindow window = new FloatingImageWindow(subImg);
                        // 将窗口放在截取区域左上角
                        window.setLocation(captureRect.x, captureRect.y);
                        window.setVisible(true);
                    });
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPt == null) {
                    return;
                }
                captureRect = makeRectangle(startPt, e.getPoint());
                repaint();
            }
        });

        // 4. Esc 键取消 直接给 RootPane 绑定 ESC 键行为
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESC_PRESSED");
        actionMap.put("ESC_PRESSED", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        // 绘制截取时的屏幕快照
        g2.drawImage(screenImage, 0, 0, null);

        int w = getWidth(), h = getHeight();
        // 绘制半透明遮罩，但保留选区区域不被遮罩
        Area mask = new Area(new Rectangle(0, 0, w, h));
        if (captureRect != null) {
            mask.subtract(new Area(captureRect));
        }
        // 遮罩透明度可调
        g2.setColor(new Color(0, 0, 0, 82));
        g2.fill(mask);

        // 绘制选区红色边框
        if (captureRect != null) {
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(40, 130, 239));
            g2.draw(captureRect);
        }
        g2.dispose();
    }

    private Rectangle makeRectangle(Point p1, Point p2) {
        return new Rectangle(
                Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
                Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y)
        );
    }
}