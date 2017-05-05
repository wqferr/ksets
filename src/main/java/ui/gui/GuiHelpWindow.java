package ui.gui;

import javax.swing.*;
import java.io.*;
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

    private static final HelpPage gettingStarted = new HelpPage("Getting started", "getting-started.txt");

    private JFrame frame;

    public GuiHelpWindow() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        JTextArea txtTest = new JTextArea();
        txtTest.setEditable(false);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(gettingStarted.getResource()))) {
            String line;
            while ((line = in.readLine()) != null) {
                txtTest.append(line);
                txtTest.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.getContentPane().add(txtTest);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void show() {
        new GuiHelpWindow().frame.setVisible(true);
    }

}
