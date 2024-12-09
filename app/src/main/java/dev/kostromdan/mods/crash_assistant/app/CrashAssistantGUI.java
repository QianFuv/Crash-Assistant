package dev.kostromdan.mods.crash_assistant.app;

import dev.kostromdan.mods.crash_assistant.app.gui.ControlPanel;
import dev.kostromdan.mods.crash_assistant.app.gui.FileListPanel;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.Map;

public class CrashAssistantGUI {
    private final JFrame frame;
    private final FileListPanel fileListPanel;
    private final ControlPanel controlPanel;

    public CrashAssistantGUI(Map<String, Path> availableLogs) {
        frame = new JFrame("Crash Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                runBeforeClose();
            }
        });

        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        String titleText = CrashAssistantApp.crashed_with_report ?
                CrashAssistantConfig.get("text.title_crashed_with_report").toString() :
                CrashAssistantConfig.get("text.title_crashed_without_report").toString();
        JLabel titleLabel = new JLabel(titleText, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(titleLabel, BorderLayout.NORTH);

        fileListPanel = new FileListPanel();
        frame.add(fileListPanel.getScrollPane(), BorderLayout.CENTER);

        controlPanel = new ControlPanel(fileListPanel);
        frame.add(controlPanel.getPanel(), BorderLayout.SOUTH);

        for (Map.Entry<String, Path> entry : availableLogs.entrySet()) {
            fileListPanel.addFile(entry.getKey(), entry.getValue());
        }

        frame.setSize(Math.max(fileListPanel.getFileListPanel().getPreferredSize().width, controlPanel.getPanel().getPreferredSize().width) + 26, frame.getHeight());

        frame.setVisible(true);

        frame.toFront();
        frame.requestFocus();
    }

    private void runBeforeClose() {
        CrashAssistantConfig.onExit();
    }
}

