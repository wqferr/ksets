package com.piazentin.ml.jkset;

import java.io.File;
import java.io.IOException;

import jkset.Dataset;

public class DatasetReaderTest {

	public static void main(String[] args) {
		File f = new File("test.csv");
		try {
			double[][] data = Dataset.read(f);
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++)
					System.out.print(data[i][j] + ", ");
				System.out.println();
			}
		} catch (IOException e) {
			System.out.println("Error");
		}
	}
	
}
