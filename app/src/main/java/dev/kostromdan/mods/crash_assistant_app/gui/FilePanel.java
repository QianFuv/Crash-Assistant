package dev.kostromdan.mods.crash_assistant_app.gui;

import javax.swing.*;
import java.awt.*;

public class FilePanel {
    private JPanel panel;
    private String fileName;

    public FilePanel(String fileName, FileListPanel parentPanel) {
        this.fileName = fileName;

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel fileNameLabel = new JLabel(fileName);
        panel.add(fileNameLabel, BorderLayout.CENTER);

        // Используем FlowLayout для кнопок, чтобы каждая кнопка могла иметь свою ширину
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        // Кнопки с фиксированными размерами
        JButton openButton = new JButton("open");
        openButton.setPreferredSize(new Dimension(62, 25));
        openButton.addActionListener(e -> openFile());

        JButton showButton = new JButton("show in explorer");
        showButton.setPreferredSize(new Dimension(129, 25));
        showButton.addActionListener(e -> showInExplorer());

        JButton uploadButton = new JButton("upload and copy link");
        uploadButton.setPreferredSize(new Dimension(149, 25));
        uploadButton.addActionListener(e -> uploadFile());

        // Добавляем кнопки на панель
        buttonPanel.add(openButton);
        buttonPanel.add(showButton);
        buttonPanel.add(uploadButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        panel.setMinimumSize(new Dimension(0, 35));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }

    public JPanel getPanel() {
        return panel;
    }

    private void openFile() {
        System.out.println("Opening file: " + fileName);
        JOptionPane.showMessageDialog(null, "File '" + fileName + "' opened.");
    }

    private void showInExplorer() {
        System.out.println("Showing file in Explorer: " + fileName);
        JOptionPane.showMessageDialog(null, "File '" + fileName + "' shown in Explorer.");
    }

    private void uploadFile() {
        System.out.println("Uploading file: " + fileName);
        JOptionPane.showMessageDialog(null, "File '" + fileName + "' uploaded.");
    }
}


