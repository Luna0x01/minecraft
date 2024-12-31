package net.minecraft.server.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelProperties;

public class ToggleDownfallCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "toggledownfall";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.downfall.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		this.method_12486(minecraftServer);
		run(commandSource, this, "commands.downfall.success", new Object[0]);
	}

	protected void method_12486(MinecraftServer minecraftServer) {
		LevelProperties levelProperties = minecraftServer.worlds[0].getLevelProperties();
		levelProperties.setRaining(!levelProperties.isRaining());
	}
}
