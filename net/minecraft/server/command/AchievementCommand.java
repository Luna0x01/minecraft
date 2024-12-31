package net.minecraft.server.command;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
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
	public void method_3279(MinecraftServer minecraftServer, CommandSource commandSource, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new IncorrectUsageException("commands.achievement.usage");
		} else {
			final Stat stat = Stats.getAStat(args[1]);
			if ((stat != null || "*".equals(args[1])) && (stat == null || stat.isAchievement())) {
				final ServerPlayerEntity serverPlayerEntity = args.length >= 3 ? method_4639(minecraftServer, commandSource, args[2]) : getAsPlayer(commandSource);
				boolean bl = "give".equalsIgnoreCase(args[0]);
				boolean bl2 = "take".equalsIgnoreCase(args[0]);
				if (bl || bl2) {
					if (stat == null) {
						if (bl) {
							for (Achievement achievement : AchievementsAndCriterions.ACHIEVEMENTS) {
								serverPlayerEntity.incrementStat(achievement);
							}

							run(commandSource, this, "commands.achievement.give.success.all", new Object[]{serverPlayerEntity.getTranslationKey()});
						} else if (bl2) {
							for (Achievement achievement2 : Lists.reverse(AchievementsAndCriterions.ACHIEVEMENTS)) {
								serverPlayerEntity.method_11238(achievement2);
							}

							run(commandSource, this, "commands.achievement.take.success.all", new Object[]{serverPlayerEntity.getTranslationKey()});
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
									public boolean apply(@Nullable Achievement achievement) {
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
										for (Achievement var24 = achievement5; var24 != null; var24 = var24.parent) {
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
							run(commandSource, this, "commands.achievement.give.success.one", new Object[]{serverPlayerEntity.getTranslationKey(), stat.method_8281()});
						} else if (bl2) {
							serverPlayerEntity.method_11238(stat);
							run(commandSource, this, "commands.achievement.take.success.one", new Object[]{stat.method_8281(), serverPlayerEntity.getTranslationKey()});
						}
					}
				}
			} else {
				throw new CommandException("commands.achievement.unknownAchievement", args[1]);
			}
		}
	}

	@Override
	public List<String> method_10738(MinecraftServer server, CommandSource source, String[] strings, @Nullable BlockPos pos) {
		if (strings.length == 1) {
			return method_2894(strings, new String[]{"give", "take"});
		} else if (strings.length != 2) {
			return strings.length == 3 ? method_2894(strings, server.getPlayerNames()) : Collections.emptyList();
		} else {
			List<String> list = Lists.newArrayList();

			for (Stat stat : AchievementsAndCriterions.ACHIEVEMENTS) {
				list.add(stat.name);
			}

			return method_10708(strings, list);
		}
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return index == 2;
	}
}
