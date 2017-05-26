package app;

import app.ui.gui.GuiMainWindow;

import java.awt.*;

public class Application {
    public static void main(String[] args) {
        EventQueue.invokeLater(
            () -> {
                try {
                    GuiMainWindow window = new GuiMainWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    System.err.printf("Error initializing window: %s", e.getMessage());
                }
            }
        );
    }
}
