package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class TriggerCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "trigger";
	}

	@Override
	public int getPermissionLevel() {
		return 0;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.trigger.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 3) {
			throw new IncorrectUsageException("commands.trigger.usage");
		} else {
			ServerPlayerEntity serverPlayerEntity;
			if (source instanceof ServerPlayerEntity) {
				serverPlayerEntity = (ServerPlayerEntity)source;
			} else {
				Entity entity = source.getEntity();
				if (!(entity instanceof ServerPlayerEntity)) {
					throw new CommandException("commands.trigger.invalidPlayer");
				}

				serverPlayerEntity = (ServerPlayerEntity)entity;
			}

			Scoreboard scoreboard = MinecraftServer.getServer().getWorld(0).getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(args[0]);
			if (scoreboardObjective != null && scoreboardObjective.getCriterion() == ScoreboardCriterion.TRIGGER) {
				int i = parseInt(args[2]);
				if (!scoreboard.playerHasObjective(serverPlayerEntity.getTranslationKey(), scoreboardObjective)) {
					throw new CommandException("commands.trigger.invalidObjective", args[0]);
				} else {
					ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(serverPlayerEntity.getTranslationKey(), scoreboardObjective);
					if (scoreboardPlayerScore.isLocked()) {
						throw new CommandException("commands.trigger.disabled", args[0]);
					} else {
						if ("set".equals(args[1])) {
							scoreboardPlayerScore.setScore(i);
						} else {
							if (!"add".equals(args[1])) {
								throw new CommandException("commands.trigger.invalidMode", args[1]);
							}

							scoreboardPlayerScore.incrementScore(i);
						}

						scoreboardPlayerScore.setLocked(true);
						if (serverPlayerEntity.interactionManager.isCreative()) {
							run(source, this, "commands.trigger.success", new Object[]{args[0], args[1], args[2]});
						}
					}
				}
			} else {
				throw new CommandException("commands.trigger.invalidObjective", args[0]);
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			Scoreboard scoreboard = MinecraftServer.getServer().getWorld(0).getScoreboard();
			List<String> list = Lists.newArrayList();

			for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
				if (scoreboardObjective.getCriterion() == ScoreboardCriterion.TRIGGER) {
					list.add(scoreboardObjective.getName());
				}
			}

			return method_2894(args, (String[])list.toArray(new String[list.size()]));
		} else {
			return args.length == 2 ? method_2894(args, new String[]{"add", "set"}) : null;
		}
	}
}
