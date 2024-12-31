package net.minecraft.server.command;

import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class ExperienceCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "xp";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.xp.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new IncorrectUsageException("commands.xp.usage");
		} else {
			String string = args[0];
			boolean bl = string.endsWith("l") || string.endsWith("L");
			if (bl && string.length() > 1) {
				string = string.substring(0, string.length() - 1);
			}

			int i = parseInt(string);
			boolean bl2 = i < 0;
			if (bl2) {
				i *= -1;
			}

			PlayerEntity playerEntity = args.length > 1 ? getPlayer(source, args[1]) : getAsPlayer(source);
			if (bl) {
				source.setStat(CommandStats.Type.QUERY_RESULT, playerEntity.experienceLevel);
				if (bl2) {
					playerEntity.incrementXp(-i);
					run(source, this, "commands.xp.success.negative.levels", new Object[]{i, playerEntity.getTranslationKey()});
				} else {
					playerEntity.incrementXp(i);
					run(source, this, "commands.xp.success.levels", new Object[]{i, playerEntity.getTranslationKey()});
				}
			} else {
				source.setStat(CommandStats.Type.QUERY_RESULT, playerEntity.totalExperience);
				if (bl2) {
					throw new CommandException("commands.xp.failure.widthdrawXp");
				}

				playerEntity.addExperience(i);
				run(source, this, "commands.xp.success", new Object[]{i, playerEntity.getTranslationKey()});
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		return args.length == 2 ? method_2894(args, this.method_3485()) : null;
	}

	protected String[] method_3485() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 1;
	}
}
