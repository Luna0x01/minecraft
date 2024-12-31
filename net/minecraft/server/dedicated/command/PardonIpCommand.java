package net.minecraft.server.dedicated.command;

import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.SyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class PardonIpCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "pardon-ip";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public boolean isAccessible(CommandSource source) {
		return MinecraftServer.getServer().getPlayerManager().getIpBanList().isEnabled() && super.isAccessible(source);
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.unbanip.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 1) {
			Matcher matcher = BanIpCommand.field_2725.matcher(args[0]);
			if (matcher.matches()) {
				MinecraftServer.getServer().getPlayerManager().getIpBanList().remove(args[0]);
				run(source, this, "commands.unbanip.success", new Object[]{args[0]});
			} else {
				throw new SyntaxException("commands.unbanip.invalid");
			}
		} else {
			throw new IncorrectUsageException("commands.unbanip.usage");
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, MinecraftServer.getServer().getPlayerManager().getIpBanList().getNames()) : null;
	}
}
