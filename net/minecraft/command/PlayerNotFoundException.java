package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
	public PlayerNotFoundException(String string) {
		super(string);
	}

	public PlayerNotFoundException(String string, Object... objects) {
		super(string, objects);
	}
}
