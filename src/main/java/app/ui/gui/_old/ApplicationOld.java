package app.ui.gui._old;

import app.ui.text.TextInterpreter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

@Deprecated
public class ApplicationOld {
    public static void main(String[] args) {
        boolean textMode = false;
        for (String arg : args) {
            if ("-t".equals(arg) || "--text".equals(arg))
                textMode = true;
        }

        if (textMode) {
            TextInterpreter ti = new TextInterpreter();
            String line;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
                while ((line = in.readLine()) != null && !"exit".equals(line)) {
                    try {
                        ti.execute(line);
                    } catch (IllegalArgumentException e) {
                        System.out.printf("Error: %s\n", e.getMessage());
                    } catch (NoSuchElementException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading from stdin");
            }
        } else {
            EventQueue.invokeLater(
                () -> {
                    try {
                        GuiMainWindowOld window = new GuiMainWindowOld();
                        window.frame.setVisible(true);
                    } catch (Exception e) {
                        System.err.printf("Error initializing window: %s", e.getMessage());
                    }
                }
            );
        }
    }
}
