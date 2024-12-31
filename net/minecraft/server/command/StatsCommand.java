package net.minecraft.server.command;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.CommandStats;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StatsCommand extends AbstractCommand {
	@Override
	public String getCommandName() {
		return "stats";
	}

	@Override
	public int getPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsageTranslationKey(CommandSource source) {
		return "commands.stats.usage";
	}

	@Override
	public void execute(CommandSource source, String[] args) throws CommandException {
		if (args.length < 1) {
			throw new IncorrectUsageException("commands.stats.usage");
		} else {
			boolean bl;
			if (args[0].equals("entity")) {
				bl = false;
			} else {
				if (!args[0].equals("block")) {
					throw new IncorrectUsageException("commands.stats.usage");
				}

				bl = true;
			}

			int i;
			if (bl) {
				if (args.length < 5) {
					throw new IncorrectUsageException("commands.stats.block.usage");
				}

				i = 4;
			} else {
				if (args.length < 3) {
					throw new IncorrectUsageException("commands.stats.entity.usage");
				}

				i = 2;
			}

			String string = args[i++];
			if ("set".equals(string)) {
				if (args.length < i + 3) {
					if (i == 5) {
						throw new IncorrectUsageException("commands.stats.block.set.usage");
					}

					throw new IncorrectUsageException("commands.stats.entity.set.usage");
				}
			} else {
				if (!"clear".equals(string)) {
					throw new IncorrectUsageException("commands.stats.usage");
				}

				if (args.length < i + 1) {
					if (i == 5) {
						throw new IncorrectUsageException("commands.stats.block.clear.usage");
					}

					throw new IncorrectUsageException("commands.stats.entity.clear.usage");
				}
			}

			CommandStats.Type type = CommandStats.Type.getByName(args[i++]);
			if (type == null) {
				throw new CommandException("commands.stats.failed");
			} else {
				World world = source.getWorld();
				CommandStats commandStats;
				if (bl) {
					BlockPos blockPos = getBlockPos(source, args, 1, false);
					BlockEntity blockEntity = world.getBlockEntity(blockPos);
					if (blockEntity == null) {
						throw new CommandException("commands.stats.noCompatibleBlock", blockPos.getX(), blockPos.getY(), blockPos.getZ());
					}

					if (blockEntity instanceof CommandBlockBlockEntity) {
						commandStats = ((CommandBlockBlockEntity)blockEntity).getCommandStats();
					} else {
						if (!(blockEntity instanceof SignBlockEntity)) {
							throw new CommandException("commands.stats.noCompatibleBlock", blockPos.getX(), blockPos.getY(), blockPos.getZ());
						}

						commandStats = ((SignBlockEntity)blockEntity).getCommandStats();
					}
				} else {
					Entity entity = getEntity(source, args[1]);
					commandStats = entity.getCommandStats();
				}

				if ("set".equals(string)) {
					String string2 = args[i++];
					String string3 = args[i];
					if (string2.length() == 0 || string3.length() == 0) {
						throw new CommandException("commands.stats.failed");
					}

					CommandStats.method_10795(commandStats, type, string2, string3);
					run(source, this, "commands.stats.success", new Object[]{type.getName(), string3, string2});
				} else if ("clear".equals(string)) {
					CommandStats.method_10795(commandStats, type, null, null);
					run(source, this, "commands.stats.cleared", new Object[]{type.getName()});
				}

				if (bl) {
					BlockPos blockPos2 = getBlockPos(source, args, 1, false);
					BlockEntity blockEntity2 = world.getBlockEntity(blockPos2);
					blockEntity2.markDirty();
				}
			}
		}
	}

	@Override
	public List<String> getAutoCompleteHints(CommandSource source, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return method_2894(args, new String[]{"entity", "block"});
		} else if (args.length == 2 && args[0].equals("entity")) {
			return method_2894(args, this.method_10268());
		} else if (args.length >= 2 && args.length <= 4 && args[0].equals("block")) {
			return method_10707(args, 1, pos);
		} else if ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))) {
			if ((args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block"))) {
				return (args.length != 6 || !args[0].equals("entity")) && (args.length != 8 || !args[0].equals("block")) ? null : method_10708(args, this.method_10269());
			} else {
				return method_2894(args, CommandStats.Type.getValues());
			}
		} else {
			return method_2894(args, new String[]{"set", "clear"});
		}
	}

	protected String[] method_10268() {
		return MinecraftServer.getServer().getPlayerNames();
	}

	protected List<String> method_10269() {
		Collection<ScoreboardObjective> collection = MinecraftServer.getServer().getWorld(0).getScoreboard().getObjectives();
		List<String> list = Lists.newArrayList();

		for (ScoreboardObjective scoreboardObjective : collection) {
			if (!scoreboardObjective.getCriterion().method_4919()) {
				list.add(scoreboardObjective.getName());
			}
		}

		return list;
	}

	@Override
	public boolean isUsernameAtIndex(String[] args, int index) {
		return args.length > 0 && args[0].equals("entity") && index == 1;
	}
}
