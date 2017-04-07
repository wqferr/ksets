package ui.text;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import jkset.KIII;

public class TextInterpreter implements Consumer<String> {
	
	public class Command implements Consumer<String[]> {
		
		private final String name;
		private final Consumer<String[]> action;
		
		private Command(String name, Consumer<String[]> action) {
			this.name = name;
			this.action = action;
			
			TextInterpreter.this.commands.put(name, this);
		}
		
		@Override
		public void accept(String[] args) {
			this.action.accept(args);
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

	public final Command NEW_NETWORK = new Command("new", this::newNetwork);
	public final Command SAVE_NETWORK = new Command("save", this::saveNetwork);
	
	private void newNetwork(String[] args) {
		System.out.println("New network with layers " + args[0] + " " + args[1] + " " + args[2]);
	}
	
	private void saveNetwork(String[] args) {
		System.out.println("Save network to " + args[0]);
	}

	@Override
	public void accept(String line) throws NoSuchElementException {
		StringTokenizer tok = new StringTokenizer(line, " ");
		String cmdStr = tok.nextToken();
		List<String> args = new LinkedList<>();
		while (tok.hasMoreTokens())
			args.add(tok.nextToken());
		Command cmd = commands.getOrDefault(cmdStr, null);
		if (cmd == null) {
			throw new NoSuchElementException("No such command");
		}
		cmd.accept(args.toArray(new String[] {}));
	}

}
