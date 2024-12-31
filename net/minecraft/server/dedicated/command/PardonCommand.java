package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.List;
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
	public boolean isAccessible(CommandSource source) {
		return MinecraftServer.getServer().getPlayerManager().getUserBanList().isEnabled() && super.isAccessible(source);
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 0) {
			MinecraftServer minecraftServer = MinecraftServer.getServer();
			GameProfile gameProfile = minecraftServer.getPlayerManager().getUserBanList().getBannedPlayer(args[0]);
			if (gameProfile == null) {
				throw new CommandException("commands.unban.failed", args[0]);
			} else {
				minecraftServer.getPlayerManager().getUserBanList().remove(gameProfile);
				run(source, this, "commands.unban.success", new Object[]{args[0]});
			}
		} else {
			throw new IncorrectUsageException("commands.unban.usage");
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, MinecraftServer.getServer().getPlayerManager().getUserBanList().getNames()) : null;
	}
}
