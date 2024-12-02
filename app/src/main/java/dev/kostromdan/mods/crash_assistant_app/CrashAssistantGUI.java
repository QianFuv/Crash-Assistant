package dev.kostromdan.mods.crash_assistant_app;

import dev.kostromdan.mods.crash_assistant_app.gui.ControlPanel;
import dev.kostromdan.mods.crash_assistant_app.gui.FileListPanel;

import javax.swing.*;
import java.awt.*;

public class CrashAssistantGUI {
    private JFrame frame;
    private FileListPanel fileListPanel;
    private ControlPanel controlPanel;

    public CrashAssistantGUI() {
        frame = new JFrame("Crash Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Oops, Minecraft crashed!", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);

        fileListPanel = new FileListPanel();
        frame.add(fileListPanel.getScrollPane(), BorderLayout.CENTER);

        controlPanel = new ControlPanel(fileListPanel);
        frame.add(controlPanel.getPanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CrashAssistantGUI::new);
    }
}
