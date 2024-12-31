package net.minecraft.server.command;

import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class Console implements CommandSource {
	private final StringBuffer text = new StringBuffer();
	private final MinecraftServer field_13901;

	public Console(MinecraftServer minecraftServer) {
		this.field_13901 = minecraftServer;
	}

	@Override
	public String getTranslationKey() {
		return "Rcon";
	}

	@Override
	public void sendMessage(Text text) {
		this.text.append(text.asUnformattedString());
	}

	@Override
	public boolean canUseCommand(int permissionLevel, String commandLiteral) {
		return true;
	}

	@Override
	public World getWorld() {
		return this.field_13901.getWorld();
	}

	@Override
	public boolean sendCommandFeedback() {
		return true;
	}

	@Override
	public MinecraftServer getMinecraftServer() {
		return this.field_13901;
	}
}
