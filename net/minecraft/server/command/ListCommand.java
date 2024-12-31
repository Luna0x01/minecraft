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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		int i = minecraftServer.getCurrentPlayerCount();
		commandSource.sendMessage(new TranslatableText("commands.players.list", i, minecraftServer.getMaxPlayerCount()));
		commandSource.sendMessage(new LiteralText(minecraftServer.getPlayerManager().method_8226(args.length > 0 && "uuids".equalsIgnoreCase(args[0]))));
		commandSource.setStat(CommandStats.Type.QUERY_RESULT, i);
	}
}
