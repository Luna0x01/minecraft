package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class PardonCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "pardon";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.unban.usage";
	}

	@Override
	public boolean method_3278(MinecraftServer server, CommandSource source) {
		return server.getPlayerManager().getUserBanList().isEnabled() && super.method_3278(server, source);
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 0) {
			GameProfile gameProfile = minecraftServer.getPlayerManager().getUserBanList().getBannedPlayer(args[0]);
			if (gameProfile == null) {
				throw new CommandException("commands.unban.failed", args[0]);
			} else {
				minecraftServer.getPlayerManager().getUserBanList().remove(gameProfile);
				run(commandSource, this, "commands.unban.success", new Object[]{args[0]});
			}
		} else {
			throw new IncorrectUsageException("commands.unban.usage");
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length == 1 ? method_2894(strings, server.getPlayerManager().getUserBanList().getNames()) : Collections.emptyList();
	}
}
