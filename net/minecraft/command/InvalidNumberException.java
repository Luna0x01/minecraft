package net.minecraft.command;

public class InvalidNumberException extends CommandException {
	public InvalidNumberException() {
		this("commands.generic.num.invalid");
	}

	public InvalidNumberException(String string, Object... objects) {
		super(string, objects);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
