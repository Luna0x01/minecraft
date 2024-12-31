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
	public boolean method_3278(MinecraftServer server, CommandSource source) {
		return server.isSinglePlayer() || super.method_3278(server, source);
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		World world = (World)(commandSource instanceof PlayerEntity ? ((PlayerEntity)commandSource).world : minecraftServer.getWorld(0));
		commandSource.sendMessage(new TranslatableText("commands.seed.success", world.getSeed()));
	}
}
