package com.piazentin.ml.jkset;

import java.io.IOException;

import jkset.Dataset;
import jkset.KIII;

public class DatasetReaderTest {

	public static void main(String[] args) {
		try {
			KIII kset = new KIII(3, 1, 1);
			kset.switchLayerTraining(new boolean[] {true, false, false});
			kset.setOutputLayer(0);
			kset.initialize();
			kset.train(Dataset.read("test.csv"));
			
			double[][] out = kset.run(Dataset.read("run.csv"));
			
			for (int i = 0; i < out.length; i++) {
				for (int j = 0; j < out[i].length; j++)
					System.out.print(out[i][j] + ", ");
				System.out.println();
			}
		} catch (IOException e) {
			System.out.println("Error");
		}
	}
	
}
