package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.command.SyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class ScoreboardCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "scoreboard";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.scoreboard.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (!this.method_9646(source, args)) {
			if (args.length < 1) {
				throw new IncorrectUsageException("commands.scoreboard.usage");
			} else {
				if (args[0].equalsIgnoreCase("objectives")) {
					if (args.length == 1) {
						throw new IncorrectUsageException("commands.scoreboard.objectives.usage");
					}

					if (args[1].equalsIgnoreCase("list")) {
						this.method_5310(source);
					} else if (args[1].equalsIgnoreCase("add")) {
						if (args.length < 4) {
							throw new IncorrectUsageException("commands.scoreboard.objectives.add.usage");
						}

						this.method_5307(source, args, 2);
					} else if (args[1].equalsIgnoreCase("remove")) {
						if (args.length != 3) {
							throw new IncorrectUsageException("commands.scoreboard.objectives.remove.usage");
						}

						this.method_5312(source, args[2]);
					} else {
						if (!args[1].equalsIgnoreCase("setdisplay")) {
							throw new IncorrectUsageException("commands.scoreboard.objectives.usage");
						}

						if (args.length != 3 && args.length != 4) {
							throw new IncorrectUsageException("commands.scoreboard.objectives.setdisplay.usage");
						}

						this.method_5318(source, args, 2);
					}
				} else if (args[0].equalsIgnoreCase("players")) {
					if (args.length == 1) {
						throw new IncorrectUsageException("commands.scoreboard.players.usage");
					}

					if (args[1].equalsIgnoreCase("list")) {
						if (args.length > 3) {
							throw new IncorrectUsageException("commands.scoreboard.players.list.usage");
						}

						this.method_5319(source, args, 2);
					} else if (args[1].equalsIgnoreCase("add")) {
						if (args.length < 5) {
							throw new IncorrectUsageException("commands.scoreboard.players.add.usage");
						}

						this.method_5320(source, args, 2);
					} else if (args[1].equalsIgnoreCase("remove")) {
						if (args.length < 5) {
							throw new IncorrectUsageException("commands.scoreboard.players.remove.usage");
						}

						this.method_5320(source, args, 2);
					} else if (args[1].equalsIgnoreCase("set")) {
						if (args.length < 5) {
							throw new IncorrectUsageException("commands.scoreboard.players.set.usage");
						}

						this.method_5320(source, args, 2);
					} else if (args[1].equalsIgnoreCase("reset")) {
						if (args.length != 3 && args.length != 4) {
							throw new IncorrectUsageException("commands.scoreboard.players.reset.usage");
						}

						this.method_5321(source, args, 2);
					} else if (args[1].equalsIgnoreCase("enable")) {
						if (args.length != 4) {
							throw new IncorrectUsageException("commands.scoreboard.players.enable.usage");
						}

						this.method_9648(source, args, 2);
					} else if (args[1].equalsIgnoreCase("test")) {
						if (args.length != 5 && args.length != 6) {
							throw new IncorrectUsageException("commands.scoreboard.players.test.usage");
						}

						this.method_9649(source, args, 2);
					} else {
						if (!args[1].equalsIgnoreCase("operation")) {
							throw new IncorrectUsageException("commands.scoreboard.players.usage");
						}

						if (args.length != 7) {
							throw new IncorrectUsageException("commands.scoreboard.players.operation.usage");
						}

						this.method_9650(source, args, 2);
					}
				} else {
					if (!args[0].equalsIgnoreCase("teams")) {
						throw new IncorrectUsageException("commands.scoreboard.usage");
					}

					if (args.length == 1) {
						throw new IncorrectUsageException("commands.scoreboard.teams.usage");
					}

					if (args[1].equalsIgnoreCase("list")) {
						if (args.length > 3) {
							throw new IncorrectUsageException("commands.scoreboard.teams.list.usage");
						}

						this.method_5314(source, args, 2);
					} else if (args[1].equalsIgnoreCase("add")) {
						if (args.length < 3) {
							throw new IncorrectUsageException("commands.scoreboard.teams.add.usage");
						}

						this.method_5308(source, args, 2);
					} else if (args[1].equalsIgnoreCase("remove")) {
						if (args.length != 3) {
							throw new IncorrectUsageException("commands.scoreboard.teams.remove.usage");
						}

						this.method_5313(source, args, 2);
					} else if (args[1].equalsIgnoreCase("empty")) {
						if (args.length != 3) {
							throw new IncorrectUsageException("commands.scoreboard.teams.empty.usage");
						}

						this.method_5317(source, args, 2);
					} else if (args[1].equalsIgnoreCase("join")) {
						if (args.length < 4 && (args.length != 3 || !(source instanceof PlayerEntity))) {
							throw new IncorrectUsageException("commands.scoreboard.teams.join.usage");
						}

						this.method_5315(source, args, 2);
					} else if (args[1].equalsIgnoreCase("leave")) {
						if (args.length < 3 && !(source instanceof PlayerEntity)) {
							throw new IncorrectUsageException("commands.scoreboard.teams.leave.usage");
						}

						this.method_5316(source, args, 2);
					} else {
						if (!args[1].equalsIgnoreCase("option")) {
							throw new IncorrectUsageException("commands.scoreboard.teams.usage");
						}

						if (args.length != 4 && args.length != 5) {
							throw new IncorrectUsageException("commands.scoreboard.teams.option.usage");
						}

						this.method_5311(source, args, 2);
					}
				}
			}
		}
	}

	private boolean method_9646(CommandSource commandSource, String[] strings) throws CommandException {
		int i = -1;

		for (int j = 0; j < strings.length; j++) {
			if (this.isUsernameAtIndex(strings, j) && "*".equals(strings[j])) {
				if (i >= 0) {
					throw new CommandException("commands.scoreboard.noMultiWildcard");
				}

				i = j;
			}
		}

		if (i < 0) {
			return false;
		} else {
			List<String> list = Lists.newArrayList(this.method_5309().getKnownPlayers());
			String string = strings[i];
			List<String> list2 = Lists.newArrayList();

			for (String string2 : list) {
				strings[i] = string2;

				try {
					this.execute(commandSource, strings);
					list2.add(string2);
				} catch (CommandException var11) {
					TranslatableText translatableText = new TranslatableText(var11.getMessage(), var11.getArgs());
					translatableText.getStyle().setFormatting(Formatting.RED);
					commandSource.sendMessage(translatableText);
				}
			}

			strings[i] = string;
			commandSource.setStat(CommandStats.Type.AFFECTED_ENTITIES, list2.size());
			if (list2.size() == 0) {
				throw new IncorrectUsageException("commands.scoreboard.allMatchesFailed");
			} else {
				return true;
			}
		}
	}

	protected Scoreboard method_5309() {
		return MinecraftServer.getServer().getWorld(0).getScoreboard();
	}

	protected ScoreboardObjective method_5305(String string, boolean bl) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
		if (scoreboardObjective == null) {
			throw new CommandException("commands.scoreboard.objectiveNotFound", string);
		} else if (bl && scoreboardObjective.getCriterion().method_4919()) {
			throw new CommandException("commands.scoreboard.objectiveReadOnly", string);
		} else {
			return scoreboardObjective;
		}
	}

	protected Team method_5304(String string) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		Team team = scoreboard.getTeam(string);
		if (team == null) {
			throw new CommandException("commands.scoreboard.teamNotFound", string);
		} else {
			return team;
		}
	}

	protected void method_5307(CommandSource commandSource, String[] strings, int i) throws CommandException {
		String string = strings[i++];
		String string2 = strings[i++];
		Scoreboard scoreboard = this.method_5309();
		ScoreboardCriterion scoreboardCriterion = (ScoreboardCriterion)ScoreboardCriterion.OBJECTIVES.get(string2);
		if (scoreboardCriterion == null) {
			throw new IncorrectUsageException("commands.scoreboard.objectives.add.wrongType", string2);
		} else if (scoreboard.getNullableObjective(string) != null) {
			throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", string);
		} else if (string.length() > 16) {
			throw new SyntaxException("commands.scoreboard.objectives.add.tooLong", string, 16);
		} else if (string.length() == 0) {
			throw new IncorrectUsageException("commands.scoreboard.objectives.add.usage");
		} else {
			if (strings.length > i) {
				String string3 = method_4635(commandSource, strings, i).asUnformattedString();
				if (string3.length() > 32) {
					throw new SyntaxException("commands.scoreboard.objectives.add.displayTooLong", string3, 32);
				}

				if (string3.length() > 0) {
					scoreboard.method_4884(string, scoreboardCriterion).method_4846(string3);
				} else {
					scoreboard.method_4884(string, scoreboardCriterion);
				}
			} else {
				scoreboard.method_4884(string, scoreboardCriterion);
			}

			run(commandSource, this, "commands.scoreboard.objectives.add.success", new Object[]{string});
		}
	}

	protected void method_5308(CommandSource commandSource, String[] strings, int i) throws CommandException {
		String string = strings[i++];
		Scoreboard scoreboard = this.method_5309();
		if (scoreboard.getTeam(string) != null) {
			throw new CommandException("commands.scoreboard.teams.add.alreadyExists", string);
		} else if (string.length() > 16) {
			throw new SyntaxException("commands.scoreboard.teams.add.tooLong", string, 16);
		} else if (string.length() == 0) {
			throw new IncorrectUsageException("commands.scoreboard.teams.add.usage");
		} else {
			if (strings.length > i) {
				String string2 = method_4635(commandSource, strings, i).asUnformattedString();
				if (string2.length() > 32) {
					throw new SyntaxException("commands.scoreboard.teams.add.displayTooLong", string2, 32);
				}

				if (string2.length() > 0) {
					scoreboard.addTeam(string).setDisplayName(string2);
				} else {
					scoreboard.addTeam(string);
				}
			} else {
				scoreboard.addTeam(string);
			}

			run(commandSource, this, "commands.scoreboard.teams.add.success", new Object[]{string});
		}
	}

	protected void method_5311(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Team team = this.method_5304(strings[i++]);
		if (team != null) {
			String string = strings[i++].toLowerCase();
			if (!string.equalsIgnoreCase("color")
				&& !string.equalsIgnoreCase("friendlyfire")
				&& !string.equalsIgnoreCase("seeFriendlyInvisibles")
				&& !string.equalsIgnoreCase("nametagVisibility")
				&& !string.equalsIgnoreCase("deathMessageVisibility")) {
				throw new IncorrectUsageException("commands.scoreboard.teams.option.usage");
			} else if (strings.length == 4) {
				if (string.equalsIgnoreCase("color")) {
					throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(Formatting.getNames(true, false)));
				} else if (string.equalsIgnoreCase("friendlyfire") || string.equalsIgnoreCase("seeFriendlyInvisibles")) {
					throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(Arrays.asList("true", "false")));
				} else if (!string.equalsIgnoreCase("nametagVisibility") && !string.equalsIgnoreCase("deathMessageVisibility")) {
					throw new IncorrectUsageException("commands.scoreboard.teams.option.usage");
				} else {
					throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(AbstractTeam.VisibilityRule.getValuesAsArray()));
				}
			} else {
				String string2 = strings[i];
				if (string.equalsIgnoreCase("color")) {
					Formatting formatting = Formatting.byName(string2);
					if (formatting == null || formatting.isModifier()) {
						throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(Formatting.getNames(true, false)));
					}

					team.setFormatting(formatting);
					team.setPrefix(formatting.toString());
					team.setSuffix(Formatting.RESET.toString());
				} else if (string.equalsIgnoreCase("friendlyfire")) {
					if (!string2.equalsIgnoreCase("true") && !string2.equalsIgnoreCase("false")) {
						throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(Arrays.asList("true", "false")));
					}

					team.setFriendlyFireAllowed(string2.equalsIgnoreCase("true"));
				} else if (string.equalsIgnoreCase("seeFriendlyInvisibles")) {
					if (!string2.equalsIgnoreCase("true") && !string2.equalsIgnoreCase("false")) {
						throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(Arrays.asList("true", "false")));
					}

					team.setShowFriendlyInvisibles(string2.equalsIgnoreCase("true"));
				} else if (string.equalsIgnoreCase("nametagVisibility")) {
					AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRuleByName(string2);
					if (visibilityRule == null) {
						throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(AbstractTeam.VisibilityRule.getValuesAsArray()));
					}

					team.setNameTagVisibilityRule(visibilityRule);
				} else if (string.equalsIgnoreCase("deathMessageVisibility")) {
					AbstractTeam.VisibilityRule visibilityRule2 = AbstractTeam.VisibilityRule.getRuleByName(string2);
					if (visibilityRule2 == null) {
						throw new IncorrectUsageException("commands.scoreboard.teams.option.noValue", string, concat(AbstractTeam.VisibilityRule.getValuesAsArray()));
					}

					team.setDeathMessageVisibilityRule(visibilityRule2);
				}

				run(commandSource, this, "commands.scoreboard.teams.option.success", new Object[]{string, team.getName(), string2});
			}
		}
	}

	protected void method_5313(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		Team team = this.method_5304(strings[i]);
		if (team != null) {
			scoreboard.removeTeam(team);
			run(commandSource, this, "commands.scoreboard.teams.remove.success", new Object[]{team.getName()});
		}
	}

	protected void method_5314(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		if (strings.length > i) {
			Team team = this.method_5304(strings[i]);
			if (team == null) {
				return;
			}

			Collection<String> collection = team.getPlayerList();
			commandSource.setStat(CommandStats.Type.QUERY_RESULT, collection.size());
			if (collection.size() <= 0) {
				throw new CommandException("commands.scoreboard.teams.list.player.empty", team.getName());
			}

			TranslatableText translatableText = new TranslatableText("commands.scoreboard.teams.list.player.count", collection.size(), team.getName());
			translatableText.getStyle().setFormatting(Formatting.DARK_GREEN);
			commandSource.sendMessage(translatableText);
			commandSource.sendMessage(new LiteralText(concat(collection.toArray())));
		} else {
			Collection<Team> collection2 = scoreboard.getTeams();
			commandSource.setStat(CommandStats.Type.QUERY_RESULT, collection2.size());
			if (collection2.size() <= 0) {
				throw new CommandException("commands.scoreboard.teams.list.empty");
			}

			TranslatableText translatableText2 = new TranslatableText("commands.scoreboard.teams.list.count", collection2.size());
			translatableText2.getStyle().setFormatting(Formatting.DARK_GREEN);
			commandSource.sendMessage(translatableText2);

			for (Team team2 : collection2) {
				commandSource.sendMessage(
					new TranslatableText("commands.scoreboard.teams.list.entry", team2.getName(), team2.getDisplayName(), team2.getPlayerList().size())
				);
			}
		}
	}

	protected void method_5315(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		String string = strings[i++];
		Set<String> set = Sets.newHashSet();
		Set<String> set2 = Sets.newHashSet();
		if (commandSource instanceof PlayerEntity && i == strings.length) {
			String string2 = getAsPlayer(commandSource).getTranslationKey();
			if (scoreboard.addPlayerToTeam(string2, string)) {
				set.add(string2);
			} else {
				set2.add(string2);
			}
		} else {
			while (i < strings.length) {
				String string3 = strings[i++];
				if (string3.startsWith("@")) {
					for (Entity entity : getEntities(commandSource, string3)) {
						String string4 = method_10714(commandSource, entity.getUuid().toString());
						if (scoreboard.addPlayerToTeam(string4, string)) {
							set.add(string4);
						} else {
							set2.add(string4);
						}
					}
				} else {
					String string5 = method_10714(commandSource, string3);
					if (scoreboard.addPlayerToTeam(string5, string)) {
						set.add(string5);
					} else {
						set2.add(string5);
					}
				}
			}
		}

		if (!set.isEmpty()) {
			commandSource.setStat(CommandStats.Type.AFFECTED_ENTITIES, set.size());
			run(commandSource, this, "commands.scoreboard.teams.join.success", new Object[]{set.size(), string, concat(set.toArray(new String[set.size()]))});
		}

		if (!set2.isEmpty()) {
			throw new CommandException("commands.scoreboard.teams.join.failure", set2.size(), string, concat(set2.toArray(new String[set2.size()])));
		}
	}

	protected void method_5316(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		Set<String> set = Sets.newHashSet();
		Set<String> set2 = Sets.newHashSet();
		if (commandSource instanceof PlayerEntity && i == strings.length) {
			String string = getAsPlayer(commandSource).getTranslationKey();
			if (scoreboard.clearPlayerTeam(string)) {
				set.add(string);
			} else {
				set2.add(string);
			}
		} else {
			while (i < strings.length) {
				String string2 = strings[i++];
				if (string2.startsWith("@")) {
					for (Entity entity : getEntities(commandSource, string2)) {
						String string3 = method_10714(commandSource, entity.getUuid().toString());
						if (scoreboard.clearPlayerTeam(string3)) {
							set.add(string3);
						} else {
							set2.add(string3);
						}
					}
				} else {
					String string4 = method_10714(commandSource, string2);
					if (scoreboard.clearPlayerTeam(string4)) {
						set.add(string4);
					} else {
						set2.add(string4);
					}
				}
			}
		}

		if (!set.isEmpty()) {
			commandSource.setStat(CommandStats.Type.AFFECTED_ENTITIES, set.size());
			run(commandSource, this, "commands.scoreboard.teams.leave.success", new Object[]{set.size(), concat(set.toArray(new String[set.size()]))});
		}

		if (!set2.isEmpty()) {
			throw new CommandException("commands.scoreboard.teams.leave.failure", set2.size(), concat(set2.toArray(new String[set2.size()])));
		}
	}

	protected void method_5317(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		Team team = this.method_5304(strings[i]);
		if (team != null) {
			Collection<String> collection = Lists.newArrayList(team.getPlayerList());
			commandSource.setStat(CommandStats.Type.AFFECTED_ENTITIES, collection.size());
			if (collection.isEmpty()) {
				throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", team.getName());
			} else {
				for (String string : collection) {
					scoreboard.removePlayerFromTeam(string, team);
				}

				run(commandSource, this, "commands.scoreboard.teams.empty.success", new Object[]{collection.size(), team.getName()});
			}
		}
	}

	protected void method_5312(CommandSource commandSource, String string) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		ScoreboardObjective scoreboardObjective = this.method_5305(string, false);
		scoreboard.removeObjective(scoreboardObjective);
		run(commandSource, this, "commands.scoreboard.objectives.remove.success", new Object[]{string});
	}

	protected void method_5310(CommandSource commandSource) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		Collection<ScoreboardObjective> collection = scoreboard.getObjectives();
		if (collection.size() <= 0) {
			throw new CommandException("commands.scoreboard.objectives.list.empty");
		} else {
			TranslatableText translatableText = new TranslatableText("commands.scoreboard.objectives.list.count", collection.size());
			translatableText.getStyle().setFormatting(Formatting.DARK_GREEN);
			commandSource.sendMessage(translatableText);

			for (ScoreboardObjective scoreboardObjective : collection) {
				commandSource.sendMessage(
					new TranslatableText(
						"commands.scoreboard.objectives.list.entry",
						scoreboardObjective.getName(),
						scoreboardObjective.getDisplayName(),
						scoreboardObjective.getCriterion().getName()
					)
				);
			}
		}
	}

	protected void method_5318(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		String string = strings[i++];
		int j = Scoreboard.getDisplaySlotId(string);
		ScoreboardObjective scoreboardObjective = null;
		if (strings.length == 4) {
			scoreboardObjective = this.method_5305(strings[i], false);
		}

		if (j < 0) {
			throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", string);
		} else {
			scoreboard.setObjectiveSlot(j, scoreboardObjective);
			if (scoreboardObjective != null) {
				run(
					commandSource, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[]{Scoreboard.getDisplaySlotName(j), scoreboardObjective.getName()}
				);
			} else {
				run(commandSource, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[]{Scoreboard.getDisplaySlotName(j)});
			}
		}
	}

	protected void method_5319(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		if (strings.length > i) {
			String string = method_10714(commandSource, strings[i]);
			Map<ScoreboardObjective, ScoreboardPlayerScore> map = scoreboard.getPlayerObjectives(string);
			commandSource.setStat(CommandStats.Type.QUERY_RESULT, map.size());
			if (map.size() <= 0) {
				throw new CommandException("commands.scoreboard.players.list.player.empty", string);
			}

			TranslatableText translatableText = new TranslatableText("commands.scoreboard.players.list.player.count", map.size(), string);
			translatableText.getStyle().setFormatting(Formatting.DARK_GREEN);
			commandSource.sendMessage(translatableText);

			for (ScoreboardPlayerScore scoreboardPlayerScore : map.values()) {
				commandSource.sendMessage(
					new TranslatableText(
						"commands.scoreboard.players.list.player.entry",
						scoreboardPlayerScore.getScore(),
						scoreboardPlayerScore.getObjective().getDisplayName(),
						scoreboardPlayerScore.getObjective().getName()
					)
				);
			}
		} else {
			Collection<String> collection = scoreboard.getKnownPlayers();
			commandSource.setStat(CommandStats.Type.QUERY_RESULT, collection.size());
			if (collection.size() <= 0) {
				throw new CommandException("commands.scoreboard.players.list.empty");
			}

			TranslatableText translatableText2 = new TranslatableText("commands.scoreboard.players.list.count", collection.size());
			translatableText2.getStyle().setFormatting(Formatting.DARK_GREEN);
			commandSource.sendMessage(translatableText2);
			commandSource.sendMessage(new LiteralText(concat(collection.toArray())));
		}
	}

	protected void method_5320(CommandSource commandSource, String[] strings, int i) throws CommandException {
		String string = strings[i - 1];
		int j = i;
		String string2 = method_10714(commandSource, strings[i++]);
		if (string2.length() > 40) {
			throw new SyntaxException("commands.scoreboard.players.name.tooLong", string2, 40);
		} else {
			ScoreboardObjective scoreboardObjective = this.method_5305(strings[i++], true);
			int k = string.equalsIgnoreCase("set") ? parseInt(strings[i++]) : parseClampedInt(strings[i++], 0);
			if (strings.length > i) {
				Entity entity = getEntity(commandSource, strings[j]);

				try {
					NbtCompound nbtCompound = StringNbtReader.parse(method_10706(strings, i));
					NbtCompound nbtCompound2 = new NbtCompound();
					entity.writePlayerData(nbtCompound2);
					if (!NbtHelper.matches(nbtCompound, nbtCompound2, true)) {
						throw new CommandException("commands.scoreboard.players.set.tagMismatch", string2);
					}
				} catch (NbtException var12) {
					throw new CommandException("commands.scoreboard.players.set.tagError", var12.getMessage());
				}
			}

			Scoreboard scoreboard = this.method_5309();
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string2, scoreboardObjective);
			if (string.equalsIgnoreCase("set")) {
				scoreboardPlayerScore.setScore(k);
			} else if (string.equalsIgnoreCase("add")) {
				scoreboardPlayerScore.incrementScore(k);
			} else {
				scoreboardPlayerScore.method_4868(k);
			}

			run(commandSource, this, "commands.scoreboard.players.set.success", new Object[]{scoreboardObjective.getName(), string2, scoreboardPlayerScore.getScore()});
		}
	}

	protected void method_5321(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		String string = method_10714(commandSource, strings[i++]);
		if (strings.length > i) {
			ScoreboardObjective scoreboardObjective = this.method_5305(strings[i++], false);
			scoreboard.resetPlayerScore(string, scoreboardObjective);
			run(commandSource, this, "commands.scoreboard.players.resetscore.success", new Object[]{scoreboardObjective.getName(), string});
		} else {
			scoreboard.resetPlayerScore(string, null);
			run(commandSource, this, "commands.scoreboard.players.reset.success", new Object[]{string});
		}
	}

	protected void method_9648(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		String string = method_5468(commandSource, strings[i++]);
		if (string.length() > 40) {
			throw new SyntaxException("commands.scoreboard.players.name.tooLong", string, 40);
		} else {
			ScoreboardObjective scoreboardObjective = this.method_5305(strings[i], false);
			if (scoreboardObjective.getCriterion() != ScoreboardCriterion.TRIGGER) {
				throw new CommandException("commands.scoreboard.players.enable.noTrigger", scoreboardObjective.getName());
			} else {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				scoreboardPlayerScore.setLocked(false);
				run(commandSource, this, "commands.scoreboard.players.enable.success", new Object[]{scoreboardObjective.getName(), string});
			}
		}
	}

	protected void method_9649(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		String string = method_10714(commandSource, strings[i++]);
		if (string.length() > 40) {
			throw new SyntaxException("commands.scoreboard.players.name.tooLong", string, 40);
		} else {
			ScoreboardObjective scoreboardObjective = this.method_5305(strings[i++], false);
			if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
				throw new CommandException("commands.scoreboard.players.test.notFound", scoreboardObjective.getName(), string);
			} else {
				int j = strings[i].equals("*") ? Integer.MIN_VALUE : parseInt(strings[i]);
				i++;
				int k = i < strings.length && !strings[i].equals("*") ? parseClampedInt(strings[i], j) : Integer.MAX_VALUE;
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				if (scoreboardPlayerScore.getScore() >= j && scoreboardPlayerScore.getScore() <= k) {
					run(commandSource, this, "commands.scoreboard.players.test.success", new Object[]{scoreboardPlayerScore.getScore(), j, k});
				} else {
					throw new CommandException("commands.scoreboard.players.test.failed", scoreboardPlayerScore.getScore(), j, k);
				}
			}
		}
	}

	protected void method_9650(CommandSource commandSource, String[] strings, int i) throws CommandException {
		Scoreboard scoreboard = this.method_5309();
		String string = method_10714(commandSource, strings[i++]);
		ScoreboardObjective scoreboardObjective = this.method_5305(strings[i++], true);
		String string2 = strings[i++];
		String string3 = method_10714(commandSource, strings[i++]);
		ScoreboardObjective scoreboardObjective2 = this.method_5305(strings[i], false);
		if (string.length() > 40) {
			throw new SyntaxException("commands.scoreboard.players.name.tooLong", string, 40);
		} else if (string3.length() > 40) {
			throw new SyntaxException("commands.scoreboard.players.name.tooLong", string3, 40);
		} else {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			if (!scoreboard.playerHasObjective(string3, scoreboardObjective2)) {
				throw new CommandException("commands.scoreboard.players.operation.notFound", scoreboardObjective2.getName(), string3);
			} else {
				ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string3, scoreboardObjective2);
				if (string2.equals("+=")) {
					scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() + scoreboardPlayerScore2.getScore());
				} else if (string2.equals("-=")) {
					scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() - scoreboardPlayerScore2.getScore());
				} else if (string2.equals("*=")) {
					scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() * scoreboardPlayerScore2.getScore());
				} else if (string2.equals("/=")) {
					if (scoreboardPlayerScore2.getScore() != 0) {
						scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() / scoreboardPlayerScore2.getScore());
					}
				} else if (string2.equals("%=")) {
					if (scoreboardPlayerScore2.getScore() != 0) {
						scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() % scoreboardPlayerScore2.getScore());
					}
				} else if (string2.equals("=")) {
					scoreboardPlayerScore.setScore(scoreboardPlayerScore2.getScore());
				} else if (string2.equals("<")) {
					scoreboardPlayerScore.setScore(Math.min(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore()));
				} else if (string2.equals(">")) {
					scoreboardPlayerScore.setScore(Math.max(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore()));
				} else {
					if (!string2.equals("><")) {
						throw new CommandException("commands.scoreboard.players.operation.invalidOperation", string2);
					}

					int j = scoreboardPlayerScore.getScore();
					scoreboardPlayerScore.setScore(scoreboardPlayerScore2.getScore());
					scoreboardPlayerScore2.setScore(j);
				}

				run(commandSource, this, "commands.scoreboard.players.operation.success", new Object[0]);
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, new String[]{"objectives", "players", "teams"});
		} else {
			if (args[0].equalsIgnoreCase("objectives")) {
				if (args.length == 2) {
					return method_2894(args, new String[]{"list", "add", "remove", "setdisplay"});
				}

				if (args[1].equalsIgnoreCase("add")) {
					if (args.length == 4) {
						Set<String> set = ScoreboardCriterion.OBJECTIVES.keySet();
						return method_10708(args, set);
					}
				} else if (args[1].equalsIgnoreCase("remove")) {
					if (args.length == 3) {
						return method_10708(args, this.method_5306(false));
					}
				} else if (args[1].equalsIgnoreCase("setdisplay")) {
					if (args.length == 3) {
						return method_2894(args, Scoreboard.getDisplaySlotNames());
					}

					if (args.length == 4) {
						return method_10708(args, this.method_5306(false));
					}
				}
			} else if (args[0].equalsIgnoreCase("players")) {
				if (args.length == 2) {
					return method_2894(args, new String[]{"set", "add", "remove", "reset", "list", "enable", "test", "operation"});
				}

				if (!args[1].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove") && !args[1].equalsIgnoreCase("reset")) {
					if (args[1].equalsIgnoreCase("enable")) {
						if (args.length == 3) {
							return method_2894(args, MinecraftServer.getServer().getPlayerNames());
						}

						if (args.length == 4) {
							return method_10708(args, this.method_9647());
						}
					} else if (!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("test")) {
						if (args[1].equalsIgnoreCase("operation")) {
							if (args.length == 3) {
								return method_10708(args, this.method_5309().getKnownPlayers());
							}

							if (args.length == 4) {
								return method_10708(args, this.method_5306(true));
							}

							if (args.length == 5) {
								return method_2894(args, new String[]{"+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><"});
							}

							if (args.length == 6) {
								return method_2894(args, MinecraftServer.getServer().getPlayerNames());
							}

							if (args.length == 7) {
								return method_10708(args, this.method_5306(false));
							}
						}
					} else {
						if (args.length == 3) {
							return method_10708(args, this.method_5309().getKnownPlayers());
						}

						if (args.length == 4 && args[1].equalsIgnoreCase("test")) {
							return method_10708(args, this.method_5306(false));
						}
					}
				} else {
					if (args.length == 3) {
						return method_2894(args, MinecraftServer.getServer().getPlayerNames());
					}

					if (args.length == 4) {
						return method_10708(args, this.method_5306(true));
					}
				}
			} else if (args[0].equalsIgnoreCase("teams")) {
				if (args.length == 2) {
					return method_2894(args, new String[]{"add", "remove", "join", "leave", "empty", "list", "option"});
				}

				if (args[1].equalsIgnoreCase("join")) {
					if (args.length == 3) {
						return method_10708(args, this.method_5309().getTeamNames());
					}

					if (args.length >= 4) {
						return method_2894(args, MinecraftServer.getServer().getPlayerNames());
					}
				} else {
					if (args[1].equalsIgnoreCase("leave")) {
						return method_2894(args, MinecraftServer.getServer().getPlayerNames());
					}

					if (!args[1].equalsIgnoreCase("empty") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("remove")) {
						if (args[1].equalsIgnoreCase("option")) {
							if (args.length == 3) {
								return method_10708(args, this.method_5309().getTeamNames());
							}

							if (args.length == 4) {
								return method_2894(args, new String[]{"color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"});
							}

							if (args.length == 5) {
								if (args[3].equalsIgnoreCase("color")) {
									return method_10708(args, Formatting.getNames(true, false));
								}

								if (args[3].equalsIgnoreCase("nametagVisibility") || args[3].equalsIgnoreCase("deathMessageVisibility")) {
									return method_2894(args, AbstractTeam.VisibilityRule.getValuesAsArray());
								}

								if (args[3].equalsIgnoreCase("friendlyfire") || args[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
									return method_2894(args, new String[]{"true", "false"});
								}
							}
						}
					} else if (args.length == 3) {
						return method_10708(args, this.method_5309().getTeamNames());
					}
				}
			}

			return null;
		}
	}

	protected List<String> method_5306(boolean bl) {
		Collection<ScoreboardObjective> collection = this.method_5309().getObjectives();
		List<String> list = Lists.newArrayList();

		for (ScoreboardObjective scoreboardObjective : collection) {
			if (!bl || !scoreboardObjective.getCriterion().method_4919()) {
				list.add(scoreboardObjective.getName());
			}
		}

		return list;
	}

	protected List<String> method_9647() {
		Collection<ScoreboardObjective> collection = this.method_5309().getObjectives();
		List<String> list = Lists.newArrayList();

		for (ScoreboardObjective scoreboardObjective : collection) {
			if (scoreboardObjective.getCriterion() == ScoreboardCriterion.TRIGGER) {
				list.add(scoreboardObjective.getName());
			}
		}

		return list;
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		if (!args[0].equalsIgnoreCase("players")) {
			return args[0].equalsIgnoreCase("teams") ? index == 2 : false;
		} else {
			return args.length > 1 && args[1].equalsIgnoreCase("operation") ? index == 2 || index == 5 : index == 2;
		}
	}
}
