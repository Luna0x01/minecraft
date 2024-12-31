package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
	public PlayerNotFoundException() {
		this("commands.generic.player.notFound");
	}

	public PlayerNotFoundException(String string, Object... objects) {
		super(string, objects);
	}
}
