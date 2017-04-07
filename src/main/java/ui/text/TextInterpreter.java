package ui.text;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.function.Predicate;

import jkset.DataIO;
import jkset.FileFormatException;
import jkset.KIII;

public class TextInterpreter implements Predicate<String> {
	
	public class Command implements Predicate<String[]> {
		
		private final String name;
		private final Predicate<String[]> action;
		
		private Command(String name, Predicate<String[]> action) {
			this.name = name;
			this.action = action;
			
			TextInterpreter.this.commands.put(name, this);
		}
		
		public boolean execute(String[] args) {
			return this.test(args);
		}
		
		@Override
		public boolean test(String[] args) {
			return this.action.test(args);
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

	public final Command SET_LAYER_TRAINING = new Command("layertrain", this::setLayerTraining);
	public final Command TRAIN_NETWORK = new Command("train", this::train);
	public final Command RUN_NETWORK = new Command("run", this::run);
	
	@Override
	public boolean test(String line) throws NoSuchElementException {
		StringTokenizer tok = new StringTokenizer(line, " ");
		String cmdStr = tok.nextToken();
		List<String> args = new LinkedList<>();
		while (tok.hasMoreTokens())
			args.add(tok.nextToken());
		Command cmd = commands.getOrDefault(cmdStr, null);
		if (cmd == null)
			throw new NoSuchElementException("No such command");
		
		return cmd.execute(args.toArray(new String[] {}));
	}

	public boolean execute(String line) throws NoSuchElementException {
		return test(line);
	}
	
	
	
	
	
	private boolean newNetwork(String[] args) {
		try {
			kset = new KIII(
				Integer.parseInt(args[0]),
				Integer.parseInt(args[1]),
				Integer.parseInt(args[2])
			);
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	private boolean saveNetwork(String[] args) {
		if (kset == null || args.length == 0)
			return false;
			
		try {
			kset.save(args[0]);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	private boolean loadNetwork(String[] args) {
		if (args.length == 0)
			return false;
		try {
			kset = KIII.load(args[0]);
			return true;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}
	
	@Deprecated
	private boolean showNetwork(String[] args) {
		System.out.printf("%d %d %d", kset.k3[0].getSize(), kset.k3[1].getSize(), kset.k3[2].getSize());
		return true;
	}

	private boolean setLayerTraining(String[] args) {
		if (kset == null || args.length < kset.k3.length)
			return false;
		
		boolean[] bools = new boolean[args.length];
		for (int i = 0; i < args.length; i++)
			bools[i] = Boolean.parseBoolean(args[i]);
		
		kset.switchLayerTraining(bools);
		return true;
	}
	
	private boolean train(String[] args) {
		if (kset == null || args.length == 0)
			return false;
		
		double[][] data = null;
		try {
			data = DataIO.read(args[0]);
		} catch (FileFormatException | IOException e) {
			return false;
		}
		
		kset.train(data);
		return true;
	}
	
	private boolean run(String[] args) {
		if (kset == null || args.length < 2)
			return false;
		
		double[][] data = null;
		try {
			data = DataIO.read(args[0]);
		} catch (FileFormatException | IOException e) {
			return false;
		}
		
		data = kset.run(data);
		try {
			DataIO.write(data, args[1]);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
}
