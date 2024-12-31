package net.minecraft.scoreboard;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.stat.StatType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GenericScoreboardCriteria {
	public static final Map<String, GenericScoreboardCriteria> field_19879 = Maps.newHashMap();
	public static final GenericScoreboardCriteria DUMMY = new GenericScoreboardCriteria("dummy");
	public static final GenericScoreboardCriteria TRIGGER = new GenericScoreboardCriteria("trigger");
	public static final GenericScoreboardCriteria DEATH_COUNT = new GenericScoreboardCriteria("deathCount");
	public static final GenericScoreboardCriteria PLAYER_KILL_COUNT = new GenericScoreboardCriteria("playerKillCount");
	public static final GenericScoreboardCriteria TOTAL_KILL_COUNT = new GenericScoreboardCriteria("totalKillCount");
	public static final GenericScoreboardCriteria HEALTH = new GenericScoreboardCriteria("health", true, GenericScoreboardCriteria.class_4104.HEARTS);
	public static final GenericScoreboardCriteria FOOD = new GenericScoreboardCriteria("food", true, GenericScoreboardCriteria.class_4104.INTEGER);
	public static final GenericScoreboardCriteria AIR = new GenericScoreboardCriteria("air", true, GenericScoreboardCriteria.class_4104.INTEGER);
	public static final GenericScoreboardCriteria ARMOR = new GenericScoreboardCriteria("armor", true, GenericScoreboardCriteria.class_4104.INTEGER);
	public static final GenericScoreboardCriteria XP = new GenericScoreboardCriteria("xp", true, GenericScoreboardCriteria.class_4104.INTEGER);
	public static final GenericScoreboardCriteria LEVEL = new GenericScoreboardCriteria("level", true, GenericScoreboardCriteria.class_4104.INTEGER);
	public static final GenericScoreboardCriteria[] field_19891 = new GenericScoreboardCriteria[]{
		new GenericScoreboardCriteria("teamkill." + Formatting.BLACK.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.DARK_BLUE.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.DARK_GREEN.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.DARK_AQUA.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.DARK_RED.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.DARK_PURPLE.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.GOLD.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.GRAY.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.DARK_GRAY.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.BLUE.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.GREEN.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.AQUA.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.RED.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.LIGHT_PURPLE.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.YELLOW.getName()),
		new GenericScoreboardCriteria("teamkill." + Formatting.WHITE.getName())
	};
	public static final GenericScoreboardCriteria[] field_19892 = new GenericScoreboardCriteria[]{
		new GenericScoreboardCriteria("killedByTeam." + Formatting.BLACK.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.DARK_BLUE.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.DARK_GREEN.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.DARK_AQUA.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.DARK_RED.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.DARK_PURPLE.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.GOLD.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.GRAY.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.DARK_GRAY.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.BLUE.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.GREEN.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.AQUA.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.RED.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.LIGHT_PURPLE.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.YELLOW.getName()),
		new GenericScoreboardCriteria("killedByTeam." + Formatting.WHITE.getName())
	};
	private final String field_19893;
	private final boolean field_19894;
	private final GenericScoreboardCriteria.class_4104 field_19895;

	public GenericScoreboardCriteria(String string) {
		this(string, false, GenericScoreboardCriteria.class_4104.INTEGER);
	}

	protected GenericScoreboardCriteria(String string, boolean bl, GenericScoreboardCriteria.class_4104 arg) {
		this.field_19893 = string;
		this.field_19894 = bl;
		this.field_19895 = arg;
		field_19879.put(string, this);
	}

	@Nullable
	public static GenericScoreboardCriteria method_18129(String string) {
		if (field_19879.containsKey(string)) {
			return (GenericScoreboardCriteria)field_19879.get(string);
		} else {
			int i = string.indexOf(58);
			if (i < 0) {
				return null;
			} else {
				StatType<?> statType = Registry.STATS.getByIdentifier(Identifier.method_20444(string.substring(0, i), '.'));
				return statType == null ? null : method_18130(statType, Identifier.method_20444(string.substring(i + 1), '.'));
			}
		}
	}

	@Nullable
	private static <T> GenericScoreboardCriteria method_18130(StatType<T> statType, Identifier identifier) {
		Registry<T> registry = statType.method_21424();
		return registry.containsId(identifier) ? statType.method_21429(registry.getByIdentifier(identifier)) : null;
	}

	public String method_4917() {
		return this.field_19893;
	}

	public boolean method_4919() {
		return this.field_19894;
	}

	public GenericScoreboardCriteria.class_4104 method_18131() {
		return this.field_19895;
	}

	public static enum class_4104 {
		INTEGER("integer"),
		HEARTS("hearts");

		private final String field_19898;
		private static final Map<String, GenericScoreboardCriteria.class_4104> field_19899;

		private class_4104(String string2) {
			this.field_19898 = string2;
		}

		public String method_18132() {
			return this.field_19898;
		}

		public static GenericScoreboardCriteria.class_4104 method_18133(String string) {
			return (GenericScoreboardCriteria.class_4104)field_19899.getOrDefault(string, INTEGER);
		}

		static {
			Builder<String, GenericScoreboardCriteria.class_4104> builder = ImmutableMap.builder();

			for (GenericScoreboardCriteria.class_4104 lv : values()) {
				builder.put(lv.field_19898, lv);
			}

			field_19899 = builder.build();
		}
	}
}
