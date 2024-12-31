package net.minecraft.server.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class KickCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "kick";
	}

	@Override
	public int getPermissionLevel() {
		return 3;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.kick.usage";
	}

	@Override
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length > 0 && args[0].length() > 1) {
			ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(args[0]);
			String string = "Kicked by an operator.";
			boolean bl = false;
			if (serverPlayerEntity == null) {
				throw new PlayerNotFoundException("commands.generic.player.notFound", args[0]);
			} else {
				if (args.length >= 2) {
					string = method_4635(commandSource, args, 1).asUnformattedString();
					bl = true;
				}

				serverPlayerEntity.networkHandler.disconnect(string);
				if (bl) {
					run(commandSource, this, "commands.kick.success.reason", new Object[]{serverPlayerEntity.getTranslationKey(), string});
				} else {
					run(commandSource, this, "commands.kick.success", new Object[]{serverPlayerEntity.getTranslationKey()});
				}
			}
		} else {
			throw new IncorrectUsageException("commands.kick.usage");
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		return strings.length >= 1 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
	}
}
