package net.minecraft.server.dedicated.command;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.Nullable;
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
	public boolean method_3278(MinecraftServer server, CommandSource source) {
		return server.getPlayerManager().getIpBanList().isEnabled() && super.method_3278(server, source);
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.unbanip.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 1) {
			Matcher matcher = BanIpCommand.field_2725.matcher(args[0]);
			if (matcher.matches()) {
				minecraftServer.getPlayerManager().getIpBanList().remove(args[0]);
				run(commandSource, this, "commands.unbanip.success", new Object[]{args[0]});
			} else {
				throw new SyntaxException("commands.unbanip.invalid");
			}
		} else {
			throw new IncorrectUsageException("commands.unbanip.usage");
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, server.getPlayerManager().getIpBanList().getNames()) : Collections.emptyList();
	}
}
