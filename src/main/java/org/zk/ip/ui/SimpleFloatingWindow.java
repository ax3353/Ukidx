package org.zk.ip.ui;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * 简化版悬浮窗口
 */
public class SimpleFloatingWindow extends JFrame {

    private final String content;
    private Point mouseDownPoint;

    public SimpleFloatingWindow(String content) {
        this.content = content;
        initUI();
        addMouseListeners();
    }

    private void initUI() {
        // 设置窗口属性
        setUndecorated(true);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(JBColor.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 创建文本区域
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setBackground(JBColor.WHITE);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // 计算窗口大小
        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        String[] lines = content.split("\n");
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(line));
        }

        int width = Math.min(Math.max(maxWidth + 100, 200), 500);
        int height = Math.min(Math.max(lines.length * fm.getHeight() + 100, 100), 400);

        JBScrollPane scrollPane = new JBScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(width, height - 50));
        scrollPane.setBorder(null);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(JBColor.WHITE);

        JButton copyButton = new JButton("复制");
        copyButton.addActionListener(e -> {
            CopyPasteManager.getInstance().setContents(new StringSelection(content));
            copyButton.setText("已复制");
            Timer timer = new Timer(1000, evt -> copyButton.setText("复制"));
            timer.setRepeats(false);
            timer.start();
        });

        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(copyButton);
        buttonPanel.add(closeButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();

        // 设置窗口位置
        centerOnScreen();
    }

    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 3;
        setLocation(x, y);
    }

    private void addMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDownPoint = null;
            }
        };

        MouseMotionAdapter motionAdapter = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mouseDownPoint != null) {
                    Point locationOnScreen = e.getLocationOnScreen();
                    setLocation(locationOnScreen.x - mouseDownPoint.x,
                            locationOnScreen.y - mouseDownPoint.y);
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(motionAdapter);

        // 为子组件也添加拖拽
        addDragListeners(getContentPane(), mouseAdapter, motionAdapter);
    }

    private void addDragListeners(Container container, MouseAdapter mouseAdapter, MouseMotionAdapter motionAdapter) {
        for (Component component : container.getComponents()) {
            if (!(component instanceof JButton)) {
                component.addMouseListener(mouseAdapter);
                component.addMouseMotionListener(motionAdapter);

                if (component instanceof Container) {
                    addDragListeners((Container) component, mouseAdapter, motionAdapter);
                }
            }
        }
    }

}