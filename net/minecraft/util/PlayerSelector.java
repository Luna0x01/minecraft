package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class PlayerSelector {
	private static final Pattern field_4946 = Pattern.compile("^@([pare])(?:\\[([^ ]*)\\])?$");
	private static final Splitter field_15390 = Splitter.on(',').omitEmptyStrings();
	private static final Splitter field_15391 = Splitter.on('=').limit(2);
	private static final Set<String> field_15392 = Sets.newHashSet();
	private static final String R = method_13916("r");
	private static final String RM = method_13916("rm");
	private static final String L = method_13916("l");
	private static final String LM = method_13916("lm");
	private static final String X = method_13916("x");
	private static final String Y = method_13916("y");
	private static final String Z = method_13916("z");
	private static final String DX = method_13916("dx");
	private static final String DY = method_13916("dy");
	private static final String DZ = method_13916("dz");
	private static final String RX = method_13916("rx");
	private static final String RXM = method_13916("rxm");
	private static final String RY = method_13916("ry");
	private static final String RYM = method_13916("rym");
	private static final String C = method_13916("c");
	private static final String M = method_13916("m");
	private static final String TEAM = method_13916("team");
	private static final String NAME = method_13916("name");
	private static final String TYPE = method_13916("type");
	private static final String TAG = method_13916("tag");
	private static final Predicate<String> field_15413 = new Predicate<String>() {
		public boolean apply(@Nullable String string) {
			return string != null && (PlayerSelector.field_15392.contains(string) || string.length() > "score_".length() && string.startsWith("score_"));
		}
	};
	private static final Set<String> positionsPattern = Sets.newHashSet(new String[]{X, Y, Z, DX, DY, DZ, RM, R});

	private static String method_13916(String string) {
		field_15392.add(string);
		return string;
	}

	@Nullable
	public static ServerPlayerEntity selectPlayer(CommandSource sender, String string) throws CommandException {
		return selectEntity(sender, string, ServerPlayerEntity.class);
	}

	@Nullable
	public static <T extends Entity> T selectEntity(CommandSource source, String string, Class<? extends T> entityClass) throws CommandException {
		List<T> list = method_10866(source, string, entityClass);
		return (T)(list.size() == 1 ? list.get(0) : null);
	}

	@Nullable
	public static Text method_6362(CommandSource source, String string) throws CommandException {
		List<Entity> list = method_10866(source, string, Entity.class);
		if (list.isEmpty()) {
			return null;
		} else {
			List<Text> list2 = Lists.newArrayList();

			for (Entity entity : list) {
				list2.add(entity.getName());
			}

			return AbstractCommand.concat(list2);
		}
	}

	public static <T extends Entity> List<T> method_10866(CommandSource source, String string, Class<? extends T> class_) throws CommandException {
		Matcher matcher = field_4946.matcher(string);
		if (matcher.matches() && source.canUseCommand(1, "@")) {
			Map<String, String> map = method_4098(matcher.group(2));
			if (!method_10867(source, map)) {
				return Collections.emptyList();
			} else {
				String string2 = matcher.group(1);
				BlockPos blockPos = method_10864(map, source.getBlockPos());
				Vec3d vec3d = method_12890(map, source.getPos());
				List<World> list = method_10862(source, map);
				List<T> list2 = Lists.newArrayList();

				for (World world : list) {
					if (world != null) {
						List<Predicate<Entity>> list3 = Lists.newArrayList();
						list3.addAll(method_10859(map, string2));
						list3.addAll(method_10863(map));
						list3.addAll(method_10868(map));
						list3.addAll(method_10869(map));
						list3.addAll(method_12891(source, map));
						list3.addAll(method_10871(map));
						list3.addAll(method_12892(map));
						list3.addAll(method_12888(map, vec3d));
						list3.addAll(method_10872(map));
						list2.addAll(method_10858(map, class_, list3, string2, world, blockPos));
					}
				}

				return method_10856(list2, map, source, class_, string2, vec3d);
			}
		} else {
			return Collections.emptyList();
		}
	}

	private static List<World> method_10862(CommandSource commandSource, Map<String, String> map) {
		List<World> list = Lists.newArrayList();
		if (method_10873(map)) {
			list.add(commandSource.getWorld());
		} else {
			Collections.addAll(list, commandSource.getMinecraftServer().worlds);
		}

		return list;
	}

	private static <T extends Entity> boolean method_10867(CommandSource commandSource, Map<String, String> map) {
		String string = method_10865(map, TYPE);
		if (string == null) {
			return true;
		} else {
			Identifier identifier = new Identifier(string.startsWith("!") ? string.substring(1) : string);
			if (EntityType.isValid(identifier)) {
				return true;
			} else {
				TranslatableText translatableText = new TranslatableText("commands.generic.entity.invalidType", identifier);
				translatableText.getStyle().setFormatting(Formatting.RED);
				commandSource.sendMessage(translatableText);
				return false;
			}
		}
	}

	private static List<Predicate<Entity>> method_10859(Map<String, String> map, String string) {
		String string2 = method_10865(map, TYPE);
		if (string2 == null || !string.equals("e") && !string.equals("r")) {
			return !string.equals("e") ? Collections.singletonList(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					return entity instanceof PlayerEntity;
				}
			}) : Collections.emptyList();
		} else {
			final boolean bl = string2.startsWith("!");
			final Identifier identifier = new Identifier(bl ? string2.substring(1) : string2);
			return Collections.singletonList(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					return EntityType.method_13943(entity, identifier) != bl;
				}
			});
		}
	}

	private static List<Predicate<Entity>> method_10863(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final int i = method_10860(map, LM, -1);
		final int j = method_10860(map, L, -1);
		if (i > -1 || j > -1) {
			list.add(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					if (!(entity instanceof ServerPlayerEntity)) {
						return false;
					} else {
						ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
						return (i <= -1 || serverPlayerEntity.experienceLevel >= i) && (j <= -1 || serverPlayerEntity.experienceLevel <= j);
					}
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_10868(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		String string = method_10865(map, M);
		if (string == null) {
			return list;
		} else {
			final boolean bl = string.startsWith("!");
			if (bl) {
				string = string.substring(1);
			}

			GameMode gameMode;
			try {
				int i = Integer.parseInt(string);
				gameMode = GameMode.method_11494(i, GameMode.NOT_SET);
			} catch (Throwable var6) {
				gameMode = GameMode.method_11495(string, GameMode.NOT_SET);
			}

			final GameMode gameMode3 = gameMode;
			list.add(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					if (!(entity instanceof ServerPlayerEntity)) {
						return false;
					} else {
						ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
						GameMode gameMode = serverPlayerEntity.interactionManager.getGameMode();
						return bl ? gameMode != gameMode3 : gameMode == gameMode3;
					}
				}
			});
			return list;
		}
	}

	private static List<Predicate<Entity>> method_10869(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final String string = method_10865(map, TEAM);
		final boolean bl = string != null && string.startsWith("!");
		if (bl) {
			string = string.substring(1);
		}

		if (string != null) {
			list.add(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					if (!(entity instanceof LivingEntity)) {
						return false;
					} else {
						LivingEntity livingEntity = (LivingEntity)entity;
						AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
						String string = abstractTeam == null ? "" : abstractTeam.getName();
						return string.equals(string) != bl;
					}
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_12891(CommandSource commandSource, Map<String, String> map) {
		final Map<String, Integer> map2 = method_4728(map);
		return (List<Predicate<Entity>>)(map2.isEmpty() ? Collections.emptyList() : Lists.newArrayList(new Predicate[]{new Predicate<Entity>() {
			public boolean apply(@Nullable Entity entity) {
				if (entity == null) {
					return false;
				} else {
					Scoreboard scoreboard = commandSource.getMinecraftServer().getWorld(0).getScoreboard();

					for (Entry<String, Integer> entry : map2.entrySet()) {
						String string = (String)entry.getKey();
						boolean bl = false;
						if (string.endsWith("_min") && string.length() > 4) {
							bl = true;
							string = string.substring(0, string.length() - 4);
						}

						ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
						if (scoreboardObjective == null) {
							return false;
						}

						String string2 = entity instanceof ServerPlayerEntity ? entity.getTranslationKey() : entity.getEntityName();
						if (!scoreboard.playerHasObjective(string2, scoreboardObjective)) {
							return false;
						}

						ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string2, scoreboardObjective);
						int i = scoreboardPlayerScore.getScore();
						if (i < (Integer)entry.getValue() && bl) {
							return false;
						}

						if (i > (Integer)entry.getValue() && !bl) {
							return false;
						}
					}

					return true;
				}
			}
		}}));
	}

	private static List<Predicate<Entity>> method_10871(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final String string = method_10865(map, NAME);
		final boolean bl = string != null && string.startsWith("!");
		if (bl) {
			string = string.substring(1);
		}

		if (string != null) {
			list.add(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					return entity != null && entity.getTranslationKey().equals(string) != bl;
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_12892(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		String string = method_10865(map, TAG);
		final boolean bl = string != null && string.startsWith("!");
		if (bl) {
			string = string.substring(1);
		}

		if (string != null) {
			final String string2 = string;
			list.add(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					if (entity == null) {
						return false;
					} else {
						return "".equals(string2) ? entity.getScoreboardTags().isEmpty() != bl : entity.getScoreboardTags().contains(string2) != bl;
					}
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_12888(Map<String, String> map, Vec3d vec3d) {
		double d = (double)method_10860(map, RM, -1);
		double e = (double)method_10860(map, R, -1);
		final boolean bl = d < -0.5;
		final boolean bl2 = e < -0.5;
		if (bl && bl2) {
			return Collections.emptyList();
		} else {
			double f = Math.max(d, 1.0E-4);
			final double g = f * f;
			double h = Math.max(e, 1.0E-4);
			final double i = h * h;
			return Lists.newArrayList(new Predicate[]{new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					if (entity == null) {
						return false;
					} else {
						double d = vec3d.method_12126(entity.x, entity.y, entity.z);
						return (bl || d >= g) && (bl2 || d <= i);
					}
				}
			}});
		}
	}

	private static List<Predicate<Entity>> method_10872(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		if (map.containsKey(RYM) || map.containsKey(RY)) {
			final int i = MathHelper.wrapDegrees(method_10860(map, RYM, 0));
			final int j = MathHelper.wrapDegrees(method_10860(map, RY, 359));
			list.add(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					if (entity == null) {
						return false;
					} else {
						int i = MathHelper.wrapDegrees(MathHelper.floor(entity.yaw));
						return i > j ? i >= i || i <= j : i >= i && i <= j;
					}
				}
			});
		}

		if (map.containsKey(RXM) || map.containsKey(RX)) {
			final int k = MathHelper.wrapDegrees(method_10860(map, RXM, 0));
			final int l = MathHelper.wrapDegrees(method_10860(map, RX, 359));
			list.add(new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					if (entity == null) {
						return false;
					} else {
						int i = MathHelper.wrapDegrees(MathHelper.floor(entity.pitch));
						return k > l ? i >= k || i <= l : i >= k && i <= l;
					}
				}
			});
		}

		return list;
	}

	private static <T extends Entity> List<T> method_10858(
		Map<String, String> map, Class<? extends T> class_, List<Predicate<Entity>> list, String string, World world, BlockPos blockPos
	) {
		List<T> list2 = Lists.newArrayList();
		String string2 = method_10865(map, TYPE);
		string2 = string2 != null && string2.startsWith("!") ? string2.substring(1) : string2;
		boolean bl = !string.equals("e");
		boolean bl2 = string.equals("r") && string2 != null;
		int i = method_10860(map, DX, 0);
		int j = method_10860(map, DY, 0);
		int k = method_10860(map, DZ, 0);
		int l = method_10860(map, R, -1);
		Predicate<Entity> predicate = Predicates.and(list);
		Predicate<Entity> predicate2 = Predicates.and(EntityPredicate.VALID_ENTITY, predicate);
		if (map.containsKey(DX) || map.containsKey(DY) || map.containsKey(DZ)) {
			final Box box = method_10855(blockPos, i, j, k);
			if (bl && !bl2) {
				Predicate<Entity> predicate3 = new Predicate<Entity>() {
					public boolean apply(@Nullable Entity entity) {
						return entity != null && box.intersects(entity.getBoundingBox());
					}
				};
				list2.addAll(world.method_8536(class_, Predicates.and(predicate2, predicate3)));
			} else {
				list2.addAll(world.getEntitiesInBox(class_, box, predicate2));
			}
		} else if (l >= 0) {
			Box box2 = new Box(
				(double)(blockPos.getX() - l),
				(double)(blockPos.getY() - l),
				(double)(blockPos.getZ() - l),
				(double)(blockPos.getX() + l + 1),
				(double)(blockPos.getY() + l + 1),
				(double)(blockPos.getZ() + l + 1)
			);
			if (bl && !bl2) {
				list2.addAll(world.method_8536(class_, predicate2));
			} else {
				list2.addAll(world.getEntitiesInBox(class_, box2, predicate2));
			}
		} else if (string.equals("a")) {
			list2.addAll(world.method_8536(class_, predicate));
		} else if (!string.equals("p") && (!string.equals("r") || bl2)) {
			list2.addAll(world.method_8514(class_, predicate2));
		} else {
			list2.addAll(world.method_8536(class_, predicate2));
		}

		return list2;
	}

	private static <T extends Entity> List<T> method_10856(
		List<T> list, Map<String, String> map, CommandSource commandSource, Class<? extends T> class_, String string, Vec3d vec3d
	) {
		int i = method_10860(map, C, !string.equals("a") && !string.equals("e") ? 1 : 0);
		if (string.equals("p") || string.equals("a") || string.equals("e")) {
			Collections.sort(list, new Comparator<Entity>() {
				public int compare(Entity entity, Entity entity2) {
					return ComparisonChain.start().compare(entity.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z), entity2.squaredDistanceTo(vec3d.x, vec3d.y, vec3d.z)).result();
				}
			});
		} else if (string.equals("r")) {
			Collections.shuffle(list);
		}

		Entity entity = commandSource.getEntity();
		if (entity != null && class_.isAssignableFrom(entity.getClass()) && i == 1 && list.contains(entity) && !"r".equals(string)) {
			list = Lists.newArrayList(new Entity[]{entity});
		}

		if (i != 0) {
			if (i < 0) {
				Collections.reverse(list);
			}

			list = list.subList(0, Math.min(Math.abs(i), list.size()));
		}

		return list;
	}

	private static Box method_10855(BlockPos blockPos, int i, int j, int k) {
		boolean bl = i < 0;
		boolean bl2 = j < 0;
		boolean bl3 = k < 0;
		int l = blockPos.getX() + (bl ? i : 0);
		int m = blockPos.getY() + (bl2 ? j : 0);
		int n = blockPos.getZ() + (bl3 ? k : 0);
		int o = blockPos.getX() + (bl ? 0 : i) + 1;
		int p = blockPos.getY() + (bl2 ? 0 : j) + 1;
		int q = blockPos.getZ() + (bl3 ? 0 : k) + 1;
		return new Box((double)l, (double)m, (double)n, (double)o, (double)p, (double)q);
	}

	private static BlockPos method_10864(Map<String, String> map, BlockPos blockPos) {
		return new BlockPos(method_10860(map, X, blockPos.getX()), method_10860(map, Y, blockPos.getY()), method_10860(map, Z, blockPos.getZ()));
	}

	private static Vec3d method_12890(Map<String, String> map, Vec3d vec3d) {
		return new Vec3d(method_12889(map, X, vec3d.x, true), method_12889(map, Y, vec3d.y, false), method_12889(map, Z, vec3d.z, true));
	}

	private static double method_12889(Map<String, String> map, String string, double d, boolean bl) {
		return map.containsKey(string) ? (double)MathHelper.parseInt((String)map.get(string), MathHelper.floor(d)) + (bl ? 0.5 : 0.0) : d;
	}

	private static boolean method_10873(Map<String, String> map) {
		for (String string : positionsPattern) {
			if (map.containsKey(string)) {
				return true;
			}
		}

		return false;
	}

	private static int method_10860(Map<String, String> map, String string, int i) {
		return map.containsKey(string) ? MathHelper.parseInt((String)map.get(string), i) : i;
	}

	@Nullable
	private static String method_10865(Map<String, String> map, String string) {
		return (String)map.get(string);
	}

	public static Map<String, Integer> method_4728(Map<String, String> map) {
		Map<String, Integer> map2 = Maps.newHashMap();

		for (String string : map.keySet()) {
			if (string.startsWith("score_") && string.length() > "score_".length()) {
				map2.put(string.substring("score_".length()), MathHelper.parseInt((String)map.get(string), 1));
			}
		}

		return map2;
	}

	public static boolean method_4088(String string) throws CommandException {
		Matcher matcher = field_4946.matcher(string);
		if (!matcher.matches()) {
			return false;
		} else {
			Map<String, String> map = method_4098(matcher.group(2));
			String string2 = matcher.group(1);
			int i = !"a".equals(string2) && !"e".equals(string2) ? 1 : 0;
			return method_10860(map, C, i) != 1;
		}
	}

	public static boolean method_4091(String args) {
		return field_4946.matcher(args).matches();
	}

	private static Map<String, String> method_4098(@Nullable String string) throws CommandException {
		Map<String, String> map = Maps.newHashMap();
		if (string == null) {
			return map;
		} else {
			for (String string2 : field_15390.split(string)) {
				Iterator<String> iterator = field_15391.split(string2).iterator();
				String string3 = (String)iterator.next();
				if (!field_15413.apply(string3)) {
					throw new CommandException("commands.generic.selector_argument", string2);
				}

				map.put(string3, iterator.hasNext() ? (String)iterator.next() : "");
			}

			return map;
		}
	}
}
