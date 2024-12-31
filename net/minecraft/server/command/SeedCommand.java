package net.minecraft.server.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

public class SeedCommand extends AbstractCommand {
	@Override
	public boolean isAccessible(CommandSource source) {
		return MinecraftServer.getServer().isSinglePlayer() || super.isAccessible(source);
	}

	@Override
	public String getCommandName() {
		return "seed";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.seed.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		World world = (World)(source instanceof PlayerEntity ? ((PlayerEntity)source).world : MinecraftServer.getServer().getWorld(0));
		source.sendMessage(new TranslatableText("commands.seed.success", world.getSeed()));
	}
}
