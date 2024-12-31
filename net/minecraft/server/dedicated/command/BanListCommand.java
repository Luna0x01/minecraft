package net.minecraft.server.dedicated.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class BanListCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "banlist";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public boolean method_3278(MinecraftServer server, CommandSource source) {
		return (server.getPlayerManager().getIpBanList().isEnabled() || server.getPlayerManager().getUserBanList().isEnabled()) && super.method_3278(server, source);
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.banlist.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length >= 1 && args[0].equalsIgnoreCase("ips")) {
			commandSource.sendMessage(new TranslatableText("commands.banlist.ips", minecraftServer.getPlayerManager().getIpBanList().getNames().length));
			commandSource.sendMessage(new LiteralText(concat(minecraftServer.getPlayerManager().getIpBanList().getNames())));
		} else {
			commandSource.sendMessage(new TranslatableText("commands.banlist.players", minecraftServer.getPlayerManager().getUserBanList().getNames().length));
			commandSource.sendMessage(new LiteralText(concat(minecraftServer.getPlayerManager().getUserBanList().getNames())));
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, new String[]{"players", "ips"}) : Collections.emptyList();
	}
}
