package net.minecraft.server.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ListCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "list";
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.players.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		int i = MinecraftServer.getServer().getCurrentPlayerCount();
		source.sendMessage(new TranslatableText("commands.players.list", i, MinecraftServer.getServer().getMaxPlayerCount()));
		source.sendMessage(new LiteralText(MinecraftServer.getServer().getPlayerManager().method_8226(args.length > 0 && "uuids".equalsIgnoreCase(args[0]))));
		source.setStat(CommandStats.Type.QUERY_RESULT, i);
	}
}
