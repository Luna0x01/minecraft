package net.minecraft.command;

public class IncorrectUsageException extends SyntaxException {
	public IncorrectUsageException(String string, Object... objects) {
		super(string, objects);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
