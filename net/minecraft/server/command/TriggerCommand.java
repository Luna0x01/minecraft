package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 3) {
			throw new IncorrectUsageException("commands.trigger.usage");
		} else {
			ServerPlayerEntity serverPlayerEntity;
			if (commandSource instanceof ServerPlayerEntity) {
				serverPlayerEntity = (ServerPlayerEntity)commandSource;
			} else {
				Entity entity = commandSource.getEntity();
				if (!(entity instanceof ServerPlayerEntity)) {
					throw new CommandException("commands.trigger.invalidPlayer");
				}

				serverPlayerEntity = (ServerPlayerEntity)entity;
			}

			Scoreboard scoreboard = minecraftServer.getWorld(0).getScoreboard();
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
							run(commandSource, this, "commands.trigger.success", new Object[]{args[0], args[1], args[2]});
						}
					}
				}
			} else {
				throw new CommandException("commands.trigger.invalidObjective", args[0]);
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			Scoreboard scoreboard = server.getWorld(0).getScoreboard();
			List<String> list = Lists.newArrayList();

			for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
				if (scoreboardObjective.getCriterion() == ScoreboardCriterion.TRIGGER) {
					list.add(scoreboardObjective.getName());
				}
			}

			return method_2894(strings, (String[])list.toArray(new String[list.size()]));
		} else {
			return strings.length == 2 ? method_2894(strings, new String[]{"add", "set"}) : Collections.emptyList();
		}
	}
}
