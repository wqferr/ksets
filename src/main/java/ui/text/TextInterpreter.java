package ui.text;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.function.Function;

import jkset.DataIO;
import jkset.KIII;

public class TextInterpreter {
	
	private class Command {
		
		private final String name;
		private final Function<String[], Exception> action;
		
		private Command(String name, Function<String[], Exception> action) {
			this.name = name;
			this.action = action;
			
			TextInterpreter.this.commands.put(name, this);
		}
		
		public void execute(String[] args) throws IllegalArgumentException {
			Exception e = this.action.apply(args);
			if (e != null)
				throw new IllegalArgumentException(e);
		}
		
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
			if (!(other instanceof Command) || other == null)
				return false;
			
			Command cmd = (Command) other;
			return this.name.equals(cmd.name);
		}
	}

	
	private KIII kset = null;
	private final Hashtable<String, Command> commands = new Hashtable<>(); 

	public final Command NEW_NETWORK =  new Command("new", this::newNetwork);
	public final Command SAVE_NETWORK = new Command("save", this::saveNetwork);
	public final Command LOAD_NETWORK = new Command("load", this::loadNetwork);
	
	@Deprecated
	public final Command VIEW_NETWORK = new Command("show", this::showNetwork);

	public final Command TRAIN_NETWORK = new Command("train", this::train);
	public final Command RUN_NETWORK = new Command("run", this::run);
	
	
	public final Command SET_PARAM = new Command("set", this::setParam);
	
	public void execute(String line) throws NoSuchElementException, IllegalArgumentException {
		StringTokenizer tok = new StringTokenizer(line, " ");
		String cmdStr = tok.nextToken();
		List<String> args = new LinkedList<>();
		while (tok.hasMoreTokens())
			args.add(tok.nextToken());
		Command cmd = commands.getOrDefault(cmdStr, null);
		if (cmd == null)
			throw new NoSuchElementException(cmdStr);
		
		cmd.execute(args.toArray(new String[] {}));
	}
	
	public void execute(Command cmd, String[] args) throws IllegalArgumentException {
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
		if (args.length == 0) {
			return new NoSuchElementException("Must specify a parameter to set");
		}
		
		switch (args[0]) {
			case "layer_training":
				if (kset == null)
					return new IllegalStateException("No kset loaded");
				
				if (args.length <= kset.k3.length)
					return new NoSuchElementException(
						String.format(
							"Must specify %d boolean values", kset.k3.length
						)
					);
				
				boolean[] bools = new boolean[args.length];
				for (int i = 1; i < args.length; i++)
					bools[i-1] = Boolean.parseBoolean(args[i]);
				
				kset.switchLayerTraining(bools);
				break;
				
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
			
			case "detect_instability":
				if (kset == null)
					return new IllegalStateException("No kset loaded");
				
				if (args.length <= 1)
					return new NoSuchElementException("Must specify boolean value");
				
				kset.setDetectInstability(Boolean.parseBoolean(args[1]));
				break;
				
			case "output_layer":
				if (kset == null)
					return new IllegalStateException("No kset loaded");
				if (args.length <= 1)
					return new NoSuchElementException("Must specify layer");
				int l = 0;
				
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
				
			default:
				return new NoSuchElementException(
					String.format("No parameter by name %s", args[0])
				);
		}
		
		return null;
	}
	
	@Deprecated
	private Exception showNetwork(String[] args) {
		if (kset == null)
			return new IllegalStateException("No kset loaded");

		System.out.printf("%d %d %d\n", kset.k3[0].getSize(), kset.k3[1].getSize(), kset.k3[2].getSize());
		return null;
	}

	private Exception train(String[] args) {
		if (kset == null)
			return new IllegalStateException("No kset loaded");
		
		if (args.length == 0)
			return new NoSuchElementException("No dataset file name given");
		
		double[][] data = null;
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
		
		double[][] data = null;
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
