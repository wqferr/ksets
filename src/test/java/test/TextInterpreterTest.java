package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import ui.text.TextInterpreter;

public class TextInterpreterTest {

	public static void main(String[] args) {
		TextInterpreter ti = new TextInterpreter();
		String line;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			while ((line = in.readLine()) != null && !"exit".equals(line)) {
				try {
					ti.execute(line);
				} catch (IllegalArgumentException e) {
					System.out.printf("Error: %s\n", e.getMessage());
				} catch (NoSuchElementException e) {
					System.out.printf("No such command: %s\n", e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Error reading from stdin");
		}
	}

}