package org.zk.ip.window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
                g.drawImage(image, 0, 0, null);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                for (int i = 1; i <= 6; i++) {
                    float alpha = 0.02f * i;
                    if (selected) {
                        g2.setColor(new Color(0, 149, 255, (int) (alpha * 255)));
                        g2.setStroke(new BasicStroke(i * 1f));
                    } else {
                        g2.setColor(new Color(129, 129, 129, (int) (alpha * 255)));
                        g2.setStroke(new BasicStroke(i * 2f));
                    }
                    int offset = (7 - i);
                    g2.drawRoundRect(-offset, -offset, width + 2 * offset, height + 2 * offset, 0, 0);
                }

                g2.dispose();
            }
        };

        imgPanel.setOpaque(false);
        imgPanel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        // 鼠标监听：点击选中，拖拽与右键菜单
        imgPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(imgPanel, e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    selectWindow();
                    mouseOffset = e.getPoint();
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
                    setLocation(screen.x - mouseOffset.x, screen.y - mouseOffset.y);
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

        JMenuItem saveItem = new JMenuItem("另存为");
        saveItem.addActionListener(e -> {
            setAlwaysOnTop(false);
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            chooser.setDialogTitle("另存为");
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

        JMenuItem closeItem = new JMenuItem("销毁");
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

