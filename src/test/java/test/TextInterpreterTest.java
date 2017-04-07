package test;

import ui.text.TextInterpreter;

public class TextInterpreterTest {

	public static void main(String[] args) {
		TextInterpreter ti = new TextInterpreter();
		ti.accept("new 1 3 3");
		ti.accept("save filename.kset");
	}

}
