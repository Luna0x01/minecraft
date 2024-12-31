package net.minecraft.server.dedicated.command;

import com.google.common.collect.Lists;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length == 1 && args[0].length() > 0) {
			GameProfile gameProfile = minecraftServer.getUserCache().findByName(args[0]);
			if (gameProfile == null) {
				throw new CommandException("commands.op.failed", args[0]);
			} else {
				minecraftServer.getPlayerManager().op(gameProfile);
				run(commandSource, this, "commands.op.success", new Object[]{args[0]});
			}
		} else {
			throw new IncorrectUsageException("commands.op.usage");
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			String string = strings[strings.length - 1];
			List<String> list = Lists.newArrayList();

			for (GameProfile gameProfile : server.getProfiles()) {
				if (!server.getPlayerManager().isOperator(gameProfile) && method_2883(string, gameProfile.getName())) {
					list.add(gameProfile.getName());
				}
			}

			return list;
		} else {
			return Collections.emptyList();
		}
	}
}
