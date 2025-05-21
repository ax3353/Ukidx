package org.zk.ip.window;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class FloatingImageWindow extends JWindow {
    private Point mouseOffset;
    private final BufferedImage image;
    private final KeyEventDispatcher escDispatcher;

    public FloatingImageWindow(BufferedImage img) {
        this.image = img;
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);
        setFocusableWindowState(true);

        // 确保内容面板透明
        getRootPane().setOpaque(false);
        ((JComponent) getContentPane()).setOpaque(false);

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
                g2.setColor(new Color(135, 206, 250, 128)); // 半透明淡蓝色
                g2.setStroke(new BasicStroke(4f)); // 边框粗细

                int arc = 10; // 圆角半径
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
                g2.dispose();
            }
        };

        imgPanel.setOpaque(false);
        imgPanel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        getContentPane().add(imgPanel);

        imgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseOffset = e.getPoint();
            }
        });
        imgPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point screen = e.getLocationOnScreen();
                setLocation(screen.x - mouseOffset.x, screen.y - mouseOffset.y);
            }
        });

        getContentPane().add(imgPanel);
        pack();
        centerOnScreen();

        escDispatcher = evt -> {
            if (evt.getID() == KeyEvent.KEY_PRESSED &&
                    evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                disposeWindow();
                return true;
            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(escDispatcher);
    }

    private void centerOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    public void disposeWindow() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .removeKeyEventDispatcher(escDispatcher);
        if (image != null) {
            image.flush();
        }
        dispose();
    }
}