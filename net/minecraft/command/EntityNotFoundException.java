package net.minecraft.command;

public class EntityNotFoundException extends CommandException {
	public EntityNotFoundException(String string) {
		this("commands.generic.entity.notFound", string);
	}

	public EntityNotFoundException(String string, Object... objects) {
		super(string, objects);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
