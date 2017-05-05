package ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class GuiHelpWindow {

    private static final class HelpPage {
        static final String PATH = "help/";

        String title;
        String filePath;
        URL url;

        HelpPage(String title, String fileName) {
            this.title = title;
            this.filePath = PATH + fileName;
            this.url = this.getClass().getResource(filePath);
        }

        public InputStream getResource() {
            return this.getClass().getResourceAsStream(this.filePath);
        }

        public String getFilePath() {
            return this.filePath;
        }

        public URL getURL() {
            return this.url;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }

    private static final HelpPage GETTING_STARTED = new HelpPage("Getting started", "getting-started.txt");

    private JFrame frame;

    private Box pagePanel;
    private JLabel lbTitle;
    private JTextArea txtContent;

    public GuiHelpWindow() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();

        pagePanel = Box.createVerticalBox();
        pagePanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        lbTitle = new JLabel("Test");
        pagePanel.add(lbTitle);
        pagePanel.add(Box.createVerticalStrut(20));
        txtContent = new JTextArea(20, 30);
        txtContent.setEditable(false);
        pagePanel.add(txtContent);

        Box hbox = Box.createHorizontalBox();
        hbox.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(hbox);

        hbox.add(new JLabel("JTree will go here"));
        hbox.add(new JSeparator(SwingConstants.VERTICAL));
        hbox.add(pagePanel);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        read(GETTING_STARTED);
    }

    private void read(HelpPage p) {
        SwingUtilities.invokeLater(
            () -> {
                txtContent.setText("");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getResource()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        txtContent.append(line);
                        txtContent.append("\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error reading help page: " + e.getMessage());
                }
            }
        );
    }


    public static void show() {
        new GuiHelpWindow().frame.setVisible(true);
    }

}
