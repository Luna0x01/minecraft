package net.minecraft.server.dedicated.command;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class OpCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "op";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.op.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 0) {
			MinecraftServer minecraftServer = MinecraftServer.getServer();
			GameProfile gameProfile = minecraftServer.getUserCache().findByName(args[0]);
			if (gameProfile == null) {
				throw new CommandException("commands.op.failed", args[0]);
			} else {
				minecraftServer.getPlayerManager().op(gameProfile);
				run(source, this, "commands.op.success", new Object[]{args[0]});
			}
		} else {
			throw new IncorrectUsageException("commands.op.usage");
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			String string = args[args.length - 1];
			List<String> list = Lists.newArrayList();

			for (GameProfile gameProfile : MinecraftServer.getServer().getProfiles()) {
				if (!MinecraftServer.getServer().getPlayerManager().isOperator(gameProfile) && method_2883(string, gameProfile.getName())) {
					list.add(gameProfile.getName());
				}
			}

			return list;
		} else {
			return null;
		}
	}
}
