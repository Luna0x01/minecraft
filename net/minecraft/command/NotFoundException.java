package net.minecraft.command;

public class NotFoundException extends CommandException {
	public NotFoundException() {
		this("commands.generic.notFound");
	}

	public NotFoundException(String string, Object... objects) {
		super(string, objects);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
