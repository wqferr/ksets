package test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import jkset.KIII;

public class InitialTest {
	
	private static final int N_ELEMENTS_PER_CLASS = 50;
	private static final int N_TEST_POINTS_PER_CLASS = N_ELEMENTS_PER_CLASS;
	
	private static final float CLASS_ELEMENT_NOISE = 1.2f;
	private static final float TEST_POINT_NOISE = 1f;
	
	/**
	 * Finds the closest class to the given point.
	 * @param classes list of classes.
	 * @param point point to be classified.
	 * @return index of the class closest to point.
	 */
	private static int minSqr(double[][] classes, double[] point) {
		double min = Double.POSITIVE_INFINITY;
		int minIdx = -1;
		for (int i = 0; i < classes.length; i++) {
			double s = 0;
			for (int j = 0; j < point.length; j++) {
				double d = classes[i][j] - point[j];
				s += d*d;
			}
			if (s < min) {
				min = s;
				minIdx = i;
			}
		}
		return minIdx;
	}
	
	/**
	 * Shuffles the rows of a matrix.
	 * @param data the matrix to be shuffled.
	 */
	private static void shuffle(double[][] data) {
		shuffle(data, null);
	}
	
	/**
	 * Shuffles the rows of a matrix, applying the same swaps to the given array.
	 * @param data the matrix to be shuffled.
	 * @param metadata the array to be shuffled.
	 */
	private static void shuffle(double[][] data, int[] metadata) {
		Random r = ThreadLocalRandom.current();
		
		for (int i = 0; i < data.length; i++) {
			int j = r.nextInt(i+1);
			
			double[] p = data[i];
			data[i] = data[j];
			data[j] = p;
			
			if (metadata != null) {
				int m = metadata[i];
				metadata[i] = metadata[j];
				metadata[j] = m;	
			}
		}
	}
	
	/**
	 * Creates a random element in the given class.
	 * @param cls the center point of the class.
	 * @param delta the maximum distance from the center point to the generated one.
	 * @return the generated point.
	 */
	private static double[] createClassElement(double[] cls, double delta) {
		double[] p = Arrays.copyOf(cls, cls.length);
		Random r = ThreadLocalRandom.current();
		for (int i = 0; i < p.length; i++) {
			p[i] += (1-r.nextGaussian()) * delta;
			//p[i] += delta * (r.nextInt() / (double) Integer.MAX_VALUE);
		}
		
		return p;
	}

	/**
	 * Displays point in standard output.
	 * @param p The point to be displayed.
	 */
	private static void printPoint(double[] p) {
		System.out.print("[ ");
		for (int i = 0; i < p.length; i++)
			System.out.printf("%+2.4f ", p[i]);
		System.out.print("]");
	}
	
	public static void main(String[] args) {

		// Points around which the classes will be generated
		double[][] classes = {
			{0, 10},
			{5, 6.5},
			{10, 0},
			{-2, -4}
		};
		
		// Initialize the K-set
		
		KIII kset = new KIII(classes[0].length, 1, 1);
		kset.switchLayerTraining(new boolean[] {true, false, false});
		kset.setOutputLayer(0);
		kset.initialize();		
		
		// Create dataset randomly
		double[][] data = new double[N_ELEMENTS_PER_CLASS*classes.length][classes[0].length];
		
		for (int i = 0; i < classes.length; i++) {
			System.out.printf("Class %d elements: \n", i);
			for (int j = 0; j < N_ELEMENTS_PER_CLASS; j++) {
				data[i*N_ELEMENTS_PER_CLASS + j] = createClassElement(classes[i], CLASS_ELEMENT_NOISE);

				printPoint(data[i*N_ELEMENTS_PER_CLASS + j]);
				System.out.println();
			}
			System.out.println();
		}
		
		shuffle(data);
		
		System.out.println("=========");
		
		kset.train(data);
		
		// Every class has a value associated with it.
		// This stores each value for later comparison, as this is
		// what determines the class of a tested point.
		
		// Every class is run in a separate iteration so as to
		// avoid interference from one classification to the other.
		double[][] classTable = new double[classes.length][classes[0].length];
		
		for (int i = 0; i < classTable.length; i++) {
			// Only one row in input
			double[][] r = kset.run(new double[][] { classes[i] });
			
			// Only one row in result
			classTable[i] = r[0];
			
			System.out.printf("%d: ", i);
			printPoint(classTable[i]);
			System.out.println();
		}
		
		System.out.println("\n\n============\n\n");
		
		
		// Create test cases
		double[][] test = new double[N_TEST_POINTS_PER_CLASS * classes.length][classes[0].length];
		int[] expected = new int[N_TEST_POINTS_PER_CLASS * classes.length];
		int correctClassifications = 0;
		
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < N_TEST_POINTS_PER_CLASS; j++) {
				test[i*N_TEST_POINTS_PER_CLASS + j] = createClassElement(classes[i], TEST_POINT_NOISE);
				expected[i*N_TEST_POINTS_PER_CLASS + j] = i;
			}
		}
		
		shuffle(test, expected);
		
		double[][] result;
		
		for (int i = 0; i < test.length; i++) {
			// Run each test independently
			result = kset.run(
				new double[][] {
					test[i]
				}
			);
			
			int pointClass = minSqr(classTable, result[0]);
			if (pointClass == expected[i])
				correctClassifications++;
			
			printPoint(test[i]);
			System.out.print("\t-> ");
			printPoint(result[0]);
			System.out.printf("\t-> got %d (expected: %d)\t%d\n",
				pointClass,
				expected[i],
				pointClass == expected[i] ? 1 : 0
			);
		}
		
		System.out.printf("\nAccuracy: %.2f%%\n", correctClassifications * 100.f/test.length);
	}

}
