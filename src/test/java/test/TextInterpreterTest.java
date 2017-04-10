package test;

import ui.text.TextInterpreter;

public class TextInterpreterTest {

	public static void main(String[] args) {
		TextInterpreter ti = new TextInterpreter();
		ti.execute("new 3 1 1");
		ti.execute("layertrain true false false");
		ti.execute("train test.csv");
		ti.execute("save filename.kset");
		ti.execute("load filename.kset");
		ti.execute("show");
	}

}