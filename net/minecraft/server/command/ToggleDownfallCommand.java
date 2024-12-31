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
	public void execute(CommandSource source, String[] args) throws CommandException {
		this.toggleDownfall();
		run(source, this, "commands.downfall.success", new Object[0]);
	}

	protected void toggleDownfall() {
		LevelProperties levelProperties = MinecraftServer.getServer().worlds[0].getLevelProperties();
		levelProperties.setRaining(!levelProperties.isRaining());
	}
}
