package dev.kostromdan.mods.crash_assistant.app.gui;

import dev.kostromdan.mods.crash_assistant.app.CrashAssistantApp;
import dev.kostromdan.mods.crash_assistant.lang.LanguageProvider;
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
        LanguageProvider.updateLang();
        frame = new JFrame(LanguageProvider.get("gui.window_name"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        String titleText = CrashAssistantApp.crashed_with_report ?
                LanguageProvider.get("gui.title_crashed_with_report") :
                LanguageProvider.get("gui.title_crashed_without_report");
        JLabel titleLabel = new JLabel(titleText, SwingConstants.LEFT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(16f));

        String commentText = LanguageProvider.get("gui.comment_under_title");
        JLabel commentLabel = new JLabel("<html><div style='white-space:nowrap;'>" + commentText.replaceAll("\n", "<br>") + "</div></html>");
        commentLabel.setFont(commentLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(titleLabel);
        if (!commentText.isEmpty()) {
            labelPanel.add(commentLabel);
        }

        frame.add(labelPanel, BorderLayout.NORTH);

        fileListPanel = new FileListPanel();
        frame.add(fileListPanel.getScrollPane(), BorderLayout.CENTER);

        controlPanel = new ControlPanel(fileListPanel);
        frame.add(controlPanel.getPanel(), BorderLayout.SOUTH);

        int heightWithoutScrollPane = frame.getPreferredSize().height;

        for (Map.Entry<String, Path> entry : availableLogs.entrySet()) {
            fileListPanel.addFile(entry.getKey(), entry.getValue());
        }

        frame.setSize(Math.max(Math.max( fileListPanel.getFileListPanel().getPreferredSize().width + 12, controlPanel.getPanel().getPreferredSize().width) + 26,labelPanel.getPreferredSize().width),
                Math.min(heightWithoutScrollPane + fileListPanel.getFileListPanel().getPreferredSize().height + 39, 700));
        frame.setMinimumSize(new Dimension(frame.getSize().width, heightWithoutScrollPane + 73));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        toFront();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        toFront();
                    }
                },
                2500
        );
    }

    public void toFront() {
        frame.toFront();
        frame.requestFocus();
    }
}



