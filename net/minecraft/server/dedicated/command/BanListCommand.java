package net.minecraft.server.dedicated.command;

import java.util.List;
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
	public boolean isAccessible(CommandSource source) {
		return (
				MinecraftServer.getServer().getPlayerManager().getIpBanList().isEnabled() || MinecraftServer.getServer().getPlayerManager().getUserBanList().isEnabled()
			)
			&& super.isAccessible(source);
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.banlist.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length >= 1 && args[0].equalsIgnoreCase("ips")) {
			source.sendMessage(new TranslatableText("commands.banlist.ips", MinecraftServer.getServer().getPlayerManager().getIpBanList().getNames().length));
			source.sendMessage(new LiteralText(concat(MinecraftServer.getServer().getPlayerManager().getIpBanList().getNames())));
		} else {
			source.sendMessage(new TranslatableText("commands.banlist.players", MinecraftServer.getServer().getPlayerManager().getUserBanList().getNames().length));
			source.sendMessage(new LiteralText(concat(MinecraftServer.getServer().getPlayerManager().getUserBanList().getNames())));
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, new String[]{"players", "ips"}) : null;
	}
}
