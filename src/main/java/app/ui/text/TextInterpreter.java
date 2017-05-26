package app.ui.text;

import com.sun.istack.internal.NotNull;
import jkset.DataIO;
import jkset.KIII;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class TextInterpreter {
	
	public final class Command {
		
		private final String name;
		private final Function<String[], Exception> action;
		
		private Command(@NotNull String name, @NotNull Function<String[], Exception> action) {
			this.name = name;
			this.action = action;
			
			TextInterpreter.this.commands.put(name, this);
		}

		@SuppressWarnings("WeakerAccess")
		public void execute(@NotNull String... args) throws IllegalArgumentException {
			Exception e = this.action.apply(args);
			if (e != null)
				throw new IllegalArgumentException(e.getMessage());
		}
		
		@NotNull
		@Override
		public String toString() {
			return this.name;
		}
		
		@Override
		public int hashCode() {
			return this.name.hashCode();
		}
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof Command))
				return false;
			
			Command cmd = (Command) other;
			return this.name.equals(cmd.name);
		}
	}

	
	public KIII kset = null;
	private final Hashtable<String, Command> commands = new Hashtable<>(); 


	@SuppressWarnings("unused")
	public final Command NEW_NETWORK =  new Command("new", this::newNetwork);

	@SuppressWarnings("unused")
	public final Command SAVE_NETWORK = new Command("save", this::saveNetwork);

	@SuppressWarnings("unused")
	public final Command LOAD_NETWORK = new Command("load", this::loadNetwork);


	@SuppressWarnings("unused")
	public final Command VIEW_NETWORK = new Command("disp_network", this::showNetwork);

	@SuppressWarnings("unused")
	public final Command VIEW_DATASET = new Command("disp_dataset", this::showDataset);


	@SuppressWarnings("unused")
	public final Command TRAIN_NETWORK = new Command("train", this::train);

	@SuppressWarnings("unused")
	public final Command RUN_NETWORK = new Command("run", this::run);
	
	
	public final Command SET_PARAM = new Command("set", this::setParam);
	
	public void execute(@NotNull String line) throws NoSuchElementException, IllegalArgumentException {
		StringTokenizer tok = new StringTokenizer(line, " ");
		String cmdStr = tok.nextToken();

		if (!commands.containsKey(cmdStr))
			throw new NoSuchElementException("No such command: " + cmdStr);
		
		List<String> args = new LinkedList<>();
		while (tok.hasMoreTokens())
			args.add(tok.nextToken());
		Command cmd = commands.get(cmdStr);
		
		cmd.execute(args.toArray(new String[] {}));
	}
	
	public void execute(@NotNull Command cmd, @NotNull String... args) throws IllegalArgumentException {
		cmd.execute(args);
	}
	
	
	
	
	
	private Exception newNetwork(String[] args) {
		try {
			kset = new KIII(
				Integer.parseInt(args[0]),
				Integer.parseInt(args[1]),
				Integer.parseInt(args[2])
			);
			return null;
		} catch (ArrayIndexOutOfBoundsException e) {
			return new NoSuchElementException("Must provide sizes for all 3 layers of the kset");
		}
	}
	
	private Exception saveNetwork(String[] args) {
		if (kset == null)
			return new IllegalStateException("No kset loaded");
		if (args.length == 0)
			return new NoSuchElementException("File name not specified");
			
		try {
			kset.save(args[0]);
		} catch (IOException e) {
			return e;
		}
		
		return null;
	}
	
	private Exception loadNetwork(String[] args) {
		if (args.length == 0)
			return new NoSuchElementException("File name not specified");
		
		try {
			kset = KIII.load(args[0]);
			return null;
		} catch (IOException | ClassNotFoundException e) {
			return e;
		}
	}
	
	private Exception setParam(String[] args) {
		if (args.length == 0)
			return new NoSuchElementException("Must specify a parameter to set");
		
		switch (args[0]) {
			//<editor-fold desc="Layer training">
			case "layer_training":
				if (kset == null)
					return new IllegalStateException("No kset loaded");
				
				if (args.length <= kset.k3.length)
					return new NoSuchElementException(
						String.format(
							"Must specify %d boolean values", kset.k3.length
						)
					);
				
				boolean[] bools = new boolean[kset.k3.length];
				for (int i = 1; i < args.length; i++)
					bools[i-1] = Boolean.parseBoolean(args[i]);

				kset.switchLayerTraining(bools);
				break;
			//</editor-fold>
			//<editor-fold desc="Learning rate">
			case "learning_rate":
				if (kset == null)
					return new IllegalStateException("No kset loaded");
				
				if (args.length <= 2)
					return new NoSuchElementException("Must specify the layer to set and the new learning rate value");
				
				int layer;
				double alpha;
				try {
					layer = Integer.parseInt(args[1]);
					alpha = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					return e;
				}
				
				kset.setLearningRate(layer, alpha);
				break;
			//</editor-fold>
			//<editor-fold desc="Detect instability">
			case "detect_instability":
				if (kset == null)
					return new IllegalStateException("No kset loaded");
				
				if (args.length <= 1)
					return new NoSuchElementException("Must specify boolean value");
				
				kset.setDetectInstability(Boolean.parseBoolean(args[1]));
				break;
			//</editor-fold>
			//<editor-fold desc="Output layer">
			case "output_layer":
				if (kset == null)
					return new IllegalStateException("No kset loaded");
				if (args.length <= 1)
					return new NoSuchElementException("Must specify layer");
				int l;
				
				try {
					l = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					return new NumberFormatException("\"" + args[1] + "\" is not a valid integer");
				}
				
				if (l < 0 || l >= kset.k3.length)
					return new IndexOutOfBoundsException(
						String.format("Layer number must be between 0 and %d", kset.k3.length-1)
					);
				kset.setOutputLayer(l);
				break;
			//</editor-fold>
			//<editor-fold desc="Unknown command">
			default:
				return new NoSuchElementException(
					String.format("No parameter by name %s", args[0])
				);
			//</editor-fold>
		}
		
		return null;
	}
	
	private Exception showNetwork(@SuppressWarnings("unused") String[] args) {
		if (kset == null)
			return new IllegalStateException("No kset loaded");

		for (int i = 0; i < kset.k3.length; i++) {
			System.out.printf("Layer %d:%s\n\tLayerID: %d\n\tLength: %d\n",
				i,
				i == kset.getOutputLayer() ? " [OUTPUT LAYER]" : "",
				kset.k3[i].getId(),
				kset.k3[i].getSize()
			);
			System.out.printf("\tLearning rate: %f\n", kset.k3[i].getLearningRate());
			
			System.out.print("\tWeights:\n");
			double[] w = kset.k3[i].getWeights();
			for (int j = 0; j < w.length; j++)
				System.out.printf("\t\t%d\t%f\n", j, w[j]);
			
			System.out.println("===========");
		}
		return null;
	}

	private Exception showDataset(String[] args) {
		if (args.length == 0)
			return new NoSuchElementException("No dataset file name given");
		
		double[][] data;
		try {
			data = DataIO.read(args[0]);
		} catch (IOException e) {
			return e;
		}
		
		System.out.printf("%d rows of %d columns each\n", data.length, data[0].length);
		DataIO.write(data, System.out);
		
		return null;
	}
	
	private Exception train(String[] args) {
		if (kset == null)
			return new IllegalStateException("No kset loaded");
		
		if (args.length == 0)
			return new NoSuchElementException("No dataset file name given");
		
		double[][] data;
		try {
			data = DataIO.read(args[0]);
		} catch (IOException e) {
			return e;
		}
		
		kset.train(data);
		return null;
	}

	private Exception run(String[] args) {
		if (kset == null)
			return new IllegalStateException("No kset loaded");
		if (args.length < 2)
			return new NoSuchElementException("Must provide both an input dataset file name and an output file name");
		
		double[][] data;
		try {
			data = DataIO.read(args[0]);
		} catch (IOException e) {
			return e;
		}
		
		data = kset.run(data);
		try {
			DataIO.write(data, args[1]);
		} catch (IOException e) {
			return e;
		}
		
		return null;
	}
	
}
