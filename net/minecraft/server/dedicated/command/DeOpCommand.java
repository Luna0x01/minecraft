package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class DeOpCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "deop";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.deop.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 0) {
			MinecraftServer minecraftServer = MinecraftServer.getServer();
			GameProfile gameProfile = minecraftServer.getPlayerManager().getOpList().getOperatorPlayer(args[0]);
			if (gameProfile == null) {
				throw new CommandException("commands.deop.failed", args[0]);
			} else {
				minecraftServer.getPlayerManager().deop(gameProfile);
				run(source, this, "commands.deop.success", new Object[]{args[0]});
			}
		} else {
			throw new IncorrectUsageException("commands.deop.usage");
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 1 ? method_2894(args, MinecraftServer.getServer().getPlayerManager().getOpNames()) : null;
	}
}
