package org.zk.ip.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author zk
 * 支持多张截图管理，可选中并按 ESC 关闭选中的窗口，选中时显示淡蓝边框，失去选中显示淡灰边框
 */
public class FloatingImageWindow extends JWindow {
    private static FloatingImageWindow selectedWindow;

    private Point mouseOffset;
    private final BufferedImage image;
    private final KeyEventDispatcher escDispatcher;
    private boolean selected = false;

    // 发光效果相关常量
    private static final int GLOW_SIZE = 6; // 发光范围
    private static final Color SELECTED_GLOW_COLOR = new Color(118, 176, 245, 255); // 淡蓝色发光
    private static final Color UNSELECTED_GLOW_COLOR = new Color(128, 128, 128, 60); // 淡灰色发光

    public FloatingImageWindow(BufferedImage img) {
        this.image = img;
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);
        setFocusableWindowState(true);

        getRootPane().setOpaque(false);
        ((JComponent) getContentPane()).setOpaque(false);

        // 弹出菜单
        JPopupMenu popupMenu = createContextMenu();

        JPanel imgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // 启用抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // 绘制发光效果
                drawGlowEffect(g2d);

                // 绘制原始图片，位置偏移发光大小
                g2d.drawImage(image, GLOW_SIZE, GLOW_SIZE, null);

                g2d.dispose();
            }

            private void drawGlowEffect(Graphics2D g2d) {
                Color glowColor = selected ? SELECTED_GLOW_COLOR : UNSELECTED_GLOW_COLOR;

                // 计算图片区域
                int imgX = GLOW_SIZE;
                int imgY = GLOW_SIZE;
                int imgWidth = image.getWidth();
                int imgHeight = image.getHeight();
                int arc = 8; // 圆角直径

                // 绘制多层发光效果，从外到内逐渐变亮
                for (int i = GLOW_SIZE; i > 0; i--) {
                    float alpha = (float) (GLOW_SIZE - i + 1) / GLOW_SIZE * 0.2f;
                    Color layerColor = new Color(
                            glowColor.getRed(),
                            glowColor.getGreen(),
                            glowColor.getBlue(),
                            Math.min(255, (int) (glowColor.getAlpha() * alpha))
                    );
                    g2d.setColor(layerColor);

                    // 用圆角矩形实现发光边框
                    Shape outerRect = new RoundRectangle2D.Double(
                            imgX - i, imgY - i,
                            imgWidth + 2 * i, imgHeight + 2 * i,
                            arc + 2 * i, arc + 2 * i
                    );
                    Shape innerRect = new RoundRectangle2D.Double(
                            imgX, imgY,
                            imgWidth, imgHeight,
                            arc, arc
                    );

                    // 创建环形区域（外矩形减去内矩形）
                    Area glowArea = new Area(outerRect);
                    glowArea.subtract(new Area(innerRect));

                    g2d.fill(glowArea);
                }

                // 绘制圆角边框线条
                g2d.setColor(selected ? new Color(128, 128, 128, 255) : new Color(118, 176, 245, 255));
                g2d.setStroke(new BasicStroke(0.2f));
                g2d.draw(new java.awt.geom.RoundRectangle2D.Double(
                        imgX - 1, imgY - 1,
                        imgWidth + 2, imgHeight + 2,
                        arc + 2, arc + 2
                ));
            }
        };

        imgPanel.setOpaque(false);
        // 调整面板大小，包含发光效果
        imgPanel.setPreferredSize(new Dimension(
                image.getWidth() + 2 * GLOW_SIZE,
                image.getHeight() + 2 * GLOW_SIZE
        ));

        // 鼠标监听：点击选中，拖拽与右键菜单
        imgPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(imgPanel, e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    // 检查点击是否在图片区域内
                    int imgX = GLOW_SIZE;
                    int imgY = GLOW_SIZE;
                    if (e.getX() >= imgX && e.getX() <= imgX + image.getWidth() &&
                            e.getY() >= imgY && e.getY() <= imgY + image.getHeight()) {
                        selectWindow();
                        mouseOffset = new Point(e.getX() - imgX, e.getY() - imgY);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(imgPanel, e.getX(), e.getY());
                }
            }
        });

        imgPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseOffset != null) {
                    Point screen = e.getLocationOnScreen();
                    setLocation(screen.x - mouseOffset.x - GLOW_SIZE, screen.y - mouseOffset.y - GLOW_SIZE);
                }
            }
        });

        getContentPane().add(imgPanel);
        pack();
        centerOnScreen();

        // ESC 关闭选中窗口
        escDispatcher = evt -> {
            if (selected && evt.getID() == KeyEvent.KEY_PRESSED && evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                disposeWindow();
                return true;
            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(escDispatcher);

        // 初始为选中状态
        selectWindow();
    }

    private void selectWindow() {
        if (selectedWindow != null && selectedWindow != this) {
            selectedWindow.deselectWindow();
        }
        selected = true;
        selectedWindow = this;
        setAlwaysOnTop(true);
        repaint();
    }

    private void deselectWindow() {
        selected = false;
        if (selectedWindow == this) {
            selectedWindow = null;
        }
        setAlwaysOnTop(false);
        repaint();
    }

    private JPopupMenu createContextMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem saveItem = new JMenuItem("    图片另存为    ");
        saveItem.addActionListener(e -> {
            setAlwaysOnTop(false);
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.setDialogTitle("图片另存为");
            chooser.setSelectedFile(new File("screenshot.png"));
            int ret = chooser.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getParentFile(), file.getName() + ".png");
                }
                try {
                    ImageIO.write(image, "png", file);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(this, "保存失败: " + ioException.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
            setAlwaysOnTop(true);
        });
        menu.add(saveItem);

        JMenuItem closeItem = new JMenuItem("    关闭    ");
        closeItem.addActionListener(e -> disposeWindow());
        menu.add(closeItem);

        return menu;
    }

    private void centerOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2,
                (screen.height - getHeight()) / 2);
    }

    public void disposeWindow() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(escDispatcher);
        image.flush();
        dispose();
        if (selectedWindow == this) {
            selectedWindow = null;
        }
    }
}