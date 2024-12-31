package net.minecraft.command;

public class CommandException extends Exception {
	private final Object[] args;

	public CommandException(String string, Object... objects) {
		super(string);
		this.args = objects;
	}

	public Object[] getArgs() {
		return this.args;
	}
}
