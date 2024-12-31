package net.minecraft.command;

public interface CommandProvider {
	void run(CommandSource sender, Command command, int permissionLevel, String label, Object... args);
}
