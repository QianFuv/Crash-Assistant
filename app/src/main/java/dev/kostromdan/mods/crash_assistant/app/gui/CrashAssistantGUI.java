package dev.kostromdan.mods.crash_assistant.app.gui;

import dev.kostromdan.mods.crash_assistant.app.CrashAssistantApp;
import dev.kostromdan.mods.crash_assistant.config.CrashAssistantConfig;
import gs.mclo.api.MclogsClient;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.Map;

public class CrashAssistantGUI {
    public static final MclogsClient MCLogsClient = new MclogsClient("CrashAssistant");
    private final JFrame frame;
    private final FileListPanel fileListPanel;
    private final ControlPanel controlPanel;

    public CrashAssistantGUI(Map<String, Path> availableLogs) {
        frame = new JFrame("Crash Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        String titleText = CrashAssistantApp.crashed_with_report ?
                CrashAssistantConfig.get("text.title_crashed_with_report").toString() :
                CrashAssistantConfig.get("text.title_crashed_without_report").toString();
        JLabel titleLabel = new JLabel(titleText, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        String commentText = CrashAssistantConfig.get("text.comment_under_title").toString();
        JLabel commentLabel = new JLabel("<html><div style='white-space:nowrap;'>" + commentText.replaceAll("\n", "<br>") + "</div></html>");
        commentLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(titleLabel);
        labelPanel.add(commentLabel);

        frame.add(labelPanel, BorderLayout.NORTH);

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
}



