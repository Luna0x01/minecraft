package net.minecraft.server.command;

import java.util.List;
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
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length > 0 && args[0].length() > 1) {
			ServerPlayerEntity serverPlayerEntity = MinecraftServer.getServer().getPlayerManager().getPlayer(args[0]);
			String string = "Kicked by an operator.";
			boolean bl = false;
			if (serverPlayerEntity == null) {
				throw new PlayerNotFoundException();
			} else {
				if (args.length >= 2) {
					string = method_4635(source, args, 1).asUnformattedString();
					bl = true;
				}

				serverPlayerEntity.networkHandler.disconnect(string);
				if (bl) {
					run(source, this, "commands.kick.success.reason", new Object[]{serverPlayerEntity.getTranslationKey(), string});
				} else {
					run(source, this, "commands.kick.success", new Object[]{serverPlayerEntity.getTranslationKey()});
				}
			}
		} else {
			throw new IncorrectUsageException("commands.kick.usage");
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length >= 1 ? method_2894(args, MinecraftServer.getServer().getPlayerNames()) : null;
	}
}
