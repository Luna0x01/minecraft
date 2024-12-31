package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CommandStats {
	private static final int SIZE = CommandStats.Type.values().length;
	private static final String[] BLANK = new String[SIZE];
	private String[] names = BLANK;
	private String[] objectives = BLANK;

	public void execute(CommandSource source, CommandStats.Type type, int value) {
		String string = this.names[type.getIndex()];
		if (string != null) {
			CommandSource commandSource = new CommandSource() {
				@Override
				public String getTranslationKey() {
					return source.getTranslationKey();
				}

				@Override
				public Text getName() {
					return source.getName();
				}

				@Override
				public void sendMessage(Text text) {
					source.sendMessage(text);
				}

				@Override
				public boolean canUseCommand(int permissionLevel, String commandLiteral) {
					return true;
				}

				@Override
				public BlockPos getBlockPos() {
					return source.getBlockPos();
				}

				@Override
				public Vec3d getPos() {
					return source.getPos();
				}

				@Override
				public World getWorld() {
					return source.getWorld();
				}

				@Override
				public Entity getEntity() {
					return source.getEntity();
				}

				@Override
				public boolean sendCommandFeedback() {
					return source.sendCommandFeedback();
				}

				@Override
				public void setStat(CommandStats.Type statsType, int value) {
					source.setStat(statsType, value);
				}
			};

			String string2;
			try {
				string2 = AbstractCommand.method_10714(commandSource, string);
			} catch (EntityNotFoundException var11) {
				return;
			}

			String string4 = this.objectives[type.getIndex()];
			if (string4 != null) {
				Scoreboard scoreboard = source.getWorld().getScoreboard();
				ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string4);
				if (scoreboardObjective != null) {
					if (scoreboard.playerHasObjective(string2, scoreboardObjective)) {
						ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string2, scoreboardObjective);
						scoreboardPlayerScore.setScore(value);
					}
				}
			}
		}
	}

	public void fromNbt(NbtCompound nbt) {
		if (nbt.contains("CommandStats", 10)) {
			NbtCompound nbtCompound = nbt.getCompound("CommandStats");

			for (CommandStats.Type type : CommandStats.Type.values()) {
				String string = type.getName() + "Name";
				String string2 = type.getName() + "Objective";
				if (nbtCompound.contains(string, 8) && nbtCompound.contains(string2, 8)) {
					String string3 = nbtCompound.getString(string);
					String string4 = nbtCompound.getString(string2);
					method_10795(this, type, string3, string4);
				}
			}
		}
	}

	public void toNbt(NbtCompound nbt) {
		NbtCompound nbtCompound = new NbtCompound();

		for (CommandStats.Type type : CommandStats.Type.values()) {
			String string = this.names[type.getIndex()];
			String string2 = this.objectives[type.getIndex()];
			if (string != null && string2 != null) {
				nbtCompound.putString(type.getName() + "Name", string);
				nbtCompound.putString(type.getName() + "Objective", string2);
			}
		}

		if (!nbtCompound.isEmpty()) {
			nbt.put("CommandStats", nbtCompound);
		}
	}

	public static void method_10795(CommandStats stats, CommandStats.Type type, String name, String objective) {
		if (name != null && name.length() != 0 && objective != null && objective.length() != 0) {
			if (stats.names == BLANK || stats.objectives == BLANK) {
				stats.names = new String[SIZE];
				stats.objectives = new String[SIZE];
			}

			stats.names[type.getIndex()] = name;
			stats.objectives[type.getIndex()] = objective;
		} else {
			method_10794(stats, type);
		}
	}

	private static void method_10794(CommandStats stats, CommandStats.Type type) {
		if (stats.names != BLANK && stats.objectives != BLANK) {
			stats.names[type.getIndex()] = null;
			stats.objectives[type.getIndex()] = null;
			boolean bl = true;

			for (CommandStats.Type type2 : CommandStats.Type.values()) {
				if (stats.names[type2.getIndex()] != null && stats.objectives[type2.getIndex()] != null) {
					bl = false;
					break;
				}
			}

			if (bl) {
				stats.names = BLANK;
				stats.objectives = BLANK;
			}
		}
	}

	public void setAllStats(CommandStats stats) {
		for (CommandStats.Type type : CommandStats.Type.values()) {
			method_10795(this, type, stats.names[type.getIndex()], stats.objectives[type.getIndex()]);
		}
	}

	public static enum Type {
		SUCCESS_COUNT(0, "SuccessCount"),
		AFFECTED_BLOCKS(1, "AffectedBlocks"),
		AFFECTED_ENTITIES(2, "AffectedEntities"),
		AFFECTED_ITEMS(3, "AffectedItems"),
		QUERY_RESULT(4, "QueryResult");

		final int index;
		final String name;

		private Type(int j, String string2) {
			this.index = j;
			this.name = string2;
		}

		public int getIndex() {
			return this.index;
		}

		public String getName() {
			return this.name;
		}

		public static String[] getValues() {
			String[] strings = new String[values().length];
			int i = 0;

			for (CommandStats.Type type : values()) {
				strings[i++] = type.getName();
			}

			return strings;
		}

		public static CommandStats.Type getByName(String name) {
			for (CommandStats.Type type : values()) {
				if (type.getName().equals(name)) {
					return type;
				}
			}

			return null;
		}
	}
}
