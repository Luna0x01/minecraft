package net.minecraft.server.command;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.advancement.Achievement;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;

public class AchievementCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "achievement";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.achievement.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.achievement.usage");
		} else {
			final Stat stat = Stats.getAStat(args[1]);
			if (stat == null && !args[1].equals("*")) {
				throw new CommandException("commands.achievement.unknownAchievement", args[1]);
			} else {
				final ServerPlayerEntity serverPlayerEntity = args.length >= 3 ? getPlayer(source, args[2]) : getAsPlayer(source);
				boolean bl = args[0].equalsIgnoreCase("give");
				boolean bl2 = args[0].equalsIgnoreCase("take");
				if (bl || bl2) {
					if (stat == null) {
						if (bl) {
							for (Achievement achievement : AchievementsAndCriterions.ACHIEVEMENTS) {
								serverPlayerEntity.incrementStat(achievement);
							}

							run(source, this, "commands.achievement.give.success.all", new Object[]{serverPlayerEntity.getTranslationKey()});
						} else if (bl2) {
							for (Achievement achievement2 : Lists.reverse(AchievementsAndCriterions.ACHIEVEMENTS)) {
								serverPlayerEntity.method_11238(achievement2);
							}

							run(source, this, "commands.achievement.take.success.all", new Object[]{serverPlayerEntity.getTranslationKey()});
						}
					} else {
						if (stat instanceof Achievement) {
							Achievement achievement3 = (Achievement)stat;
							if (bl) {
								if (serverPlayerEntity.getStatHandler().hasAchievement(achievement3)) {
									throw new CommandException("commands.achievement.alreadyHave", serverPlayerEntity.getTranslationKey(), stat.method_8281());
								}

								List<Achievement> list;
								for (list = Lists.newArrayList();
									achievement3.parent != null && !serverPlayerEntity.getStatHandler().hasAchievement(achievement3.parent);
									achievement3 = achievement3.parent
								) {
									list.add(achievement3.parent);
								}

								for (Achievement achievement4 : Lists.reverse(list)) {
									serverPlayerEntity.incrementStat(achievement4);
								}
							} else if (bl2) {
								if (!serverPlayerEntity.getStatHandler().hasAchievement(achievement3)) {
									throw new CommandException("commands.achievement.dontHave", serverPlayerEntity.getTranslationKey(), stat.method_8281());
								}

								List<Achievement> list2 = Lists.newArrayList(Iterators.filter(AchievementsAndCriterions.ACHIEVEMENTS.iterator(), new Predicate<Achievement>() {
									public boolean apply(Achievement achievement) {
										return serverPlayerEntity.getStatHandler().hasAchievement(achievement) && achievement != stat;
									}
								}));
								List<Achievement> list3 = Lists.newArrayList(list2);

								for (Achievement achievement5 : list2) {
									Achievement achievement6 = achievement5;

									boolean bl3;
									for (bl3 = false; achievement6 != null; achievement6 = achievement6.parent) {
										if (achievement6 == stat) {
											bl3 = true;
										}
									}

									if (!bl3) {
										for (Achievement var23 = achievement5; var23 != null; var23 = var23.parent) {
											list3.remove(achievement5);
										}
									}
								}

								for (Achievement achievement7 : list3) {
									serverPlayerEntity.method_11238(achievement7);
								}
							}
						}

						if (bl) {
							serverPlayerEntity.incrementStat(stat);
							run(source, this, "commands.achievement.give.success.one", new Object[]{serverPlayerEntity.getTranslationKey(), stat.method_8281()});
						} else if (bl2) {
							serverPlayerEntity.method_11238(stat);
							run(source, this, "commands.achievement.take.success.one", new Object[]{stat.method_8281(), serverPlayerEntity.getTranslationKey()});
						}
					}
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, new String[]{"give", "take"});
		} else if (args.length != 2) {
			return args.length == 3 ? method_2894(args, MinecraftServer.getServer().getPlayerNames()) : null;
		} else {
			List<String> list = Lists.newArrayList();

			for (Stat stat : Stats.ALL) {
				list.add(stat.name);
			}

			return method_10708(args, list);
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 2;
	}
}
