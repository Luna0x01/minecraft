package net.minecraft.command;

public class SyntaxException extends CommandException {
	public SyntaxException() {
		this("commands.generic.snytax");
	}

	public SyntaxException(String string, Object... objects) {
		super(string, objects);
	}
}
