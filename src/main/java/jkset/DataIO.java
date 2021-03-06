package jkset;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class DataIO {
	
	public static double[][] read(InputStream in) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			List<Double[]> data = new LinkedList<>();
			String line;
			List<Double> lineNum = new LinkedList<>();
			
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty())
					continue;
				
				StringTokenizer lineTok = new StringTokenizer(line, ",");
				lineNum.clear();
				
				while (lineTok.hasMoreTokens()) {
					try {
						lineNum.add(new Double(lineTok.nextToken()));
					} catch (NumberFormatException e) {
						throw new FileFormatException("Non-numeric value found in dataset");
					}
				}

				data.add(lineNum.toArray(new Double[lineNum.size()]));
			}
			
			// Convert from Double to double
			double[][] m = new double[data.size()][lineNum.size()];
			Iterator<Double[]> iter = data.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				Double[] row = iter.next();
				
				if (row.length != m[0].length)
					throw new FileFormatException("Inconsistent dataset width");
				
				for (int j = 0; j < row.length; j++)
					m[i][j] = row[j];
			}
					
			return m;
		}
	}
	
	public static double[][] read(File f) throws IOException {
		try (FileInputStream in = new FileInputStream(f)) {
			return read(in);
		}
	}
	
	public static double[][] read(String fileName) throws IOException {
		try (FileInputStream in = new FileInputStream(fileName)) {
			return read(in);
		}
	}
	
	public static void write(double[][] data, OutputStream out) {
		try (PrintWriter writer = new PrintWriter(out)) {
			for (double[] row : data)
				for (int j = 0; j < row.length; j++)
					writer.printf("%f%c", row[j], (j == row.length - 1) ? '\n' : ',');
		}
	}
	
	public static void write(double[][] data, File f) throws IOException {
		try (FileOutputStream out = new FileOutputStream(f)) {
			write(data, out);
		}
	}
	
	public static void write(double[][] data, String fileName) throws IOException {
		try (FileOutputStream out = new FileOutputStream(fileName)) {
			write(data, out);
		}
	}
	
}
