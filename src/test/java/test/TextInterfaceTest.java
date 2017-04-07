package test;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import jkset.Dataset;
import jkset.FileFormatException;
import jkset.KIII;

public class TextInterfaceTest {

	public static void main(String[] args) {
		String line;
		Scanner in = new Scanner(System.in);
		KIII kset = null;
		
		while (in.hasNextLine()) {
			line = in.nextLine();
			StringTokenizer tok = new StringTokenizer(line, " \n");
			
			switch (tok.nextToken()) {
				case "new":
					if (!tok.hasMoreTokens()) {
						System.out.println("No layer size given. Must specify 1 or 3 integers");
						break;
					}
					
					int size1 = 0;
					try {
						size1 = Integer.parseInt(tok.nextToken());
					} catch (NumberFormatException e) {
						System.out.println("Invalid argument. Layer size must be an int");
						break;
					}
					int size2 = size1, size3 = size1;

					if (tok.hasMoreTokens()) {
						size2 = Integer.parseInt(tok.nextToken());
						if (tok.hasMoreTokens()) {
							size3 = Integer.parseInt(tok.nextToken());
						} else {
							System.out.println("Must use either 1 or 3 integers.");
							break;
						}
					}
					
					kset = new KIII(size1, size2, size3);
					kset.switchLayerTraining(new boolean[] {false, false, false});
					kset.setOutputLayer(0);
					kset.initialize();
					break;
					
				case "view":
					if (kset == null)
						System.out.println("No kset loaded");
					else
						System.out.printf(
							"%d, %d, %d\n",
							kset.k3[0].getSize(),
							kset.k3[1].getSize(),
							kset.k3[2].getSize()
						);
					break;
					
				case "layertrain":
					boolean[] train = new boolean[3];
					for (int i = 0; i < train.length; i++) {
						try {
							train[i] = Boolean.parseBoolean(tok.nextToken());
						} catch (NoSuchElementException e) {
							System.out.println("Must provide 3 boolean values");
							break;
						}
					}
					kset.switchLayerTraining(train);
					break;
					
				case "train":
					if (!tok.hasMoreTokens()) {
						System.out.println("Must specify dataset to train with");
						break;
					}
					String fileName = tok.nextToken();
					double[][] data = null;
					try {
						data = Dataset.read(fileName);
					} catch (IOException e) {
						System.out.println("Unable to read file");
					} catch (FileFormatException e) {
						System.out.println("Invalid file format");
					}
					
					if (data != null) {
						kset.train(data);
						System.out.println("Trained kset successfully");
					}
					break;
					
				case "run":
					if (!tok.hasMoreTokens()) {
						System.out.println("Must specify dataset to run with");
						break;
					}
					fileName = tok.nextToken();
					
					String outputFileName = null;
					if (tok.hasMoreTokens())
						outputFileName = tok.nextToken();
					
					data = null;
					
					try {
						data = Dataset.read(fileName);
					} catch (IOException e) {
						System.out.println("Unable to read file");
					} catch (FileFormatException e) {
						System.out.println("Invalid file format");
					}
					
					if (data != null) {
						data = kset.run(data);
						Dataset.write(data, System.out);
						if (outputFileName != null) {
							try {
								Dataset.write(data, outputFileName);
								System.out.printf("Saved output to %s\n", outputFileName);
							} catch (IOException e) {
								System.out.println("Unable to write to file");
								break;
							}
						}
					}
					break;
			}
		}
	}

}
