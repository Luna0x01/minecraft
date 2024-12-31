package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.AbstractCommand;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;

public class PlayerSelector {
	private static final Pattern field_4946 = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)\\])?$");
	private static final Pattern field_4947 = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
	private static final Pattern field_4948 = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
	private static final Set<String> positionsPattern = Sets.newHashSet(new String[]{"x", "y", "z", "dx", "dy", "dz", "rm", "r"});

	public static ServerPlayerEntity selectPlayer(CommandSource sender, String string) {
		return selectEntity(sender, string, ServerPlayerEntity.class);
	}

	public static <T extends Entity> T selectEntity(CommandSource source, String string, Class<? extends T> entityClass) {
		List<T> list = method_10866(source, string, entityClass);
		return (T)(list.size() == 1 ? list.get(0) : null);
	}

	public static Text method_6362(CommandSource source, String string) {
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

	public static <T extends Entity> List<T> method_10866(CommandSource source, String string, Class<? extends T> class_) {
		Matcher matcher = field_4946.matcher(string);
		if (matcher.matches() && source.canUseCommand(1, "@")) {
			Map<String, String> map = method_4098(matcher.group(2));
			if (!method_10867(source, map)) {
				return Collections.emptyList();
			} else {
				String string2 = matcher.group(1);
				BlockPos blockPos = method_10864(map, source.getBlockPos());
				List<World> list = method_10862(source, map);
				List<T> list2 = Lists.newArrayList();

				for (World world : list) {
					if (world != null) {
						List<Predicate<Entity>> list3 = Lists.newArrayList();
						list3.addAll(method_10859(map, string2));
						list3.addAll(method_10863(map));
						list3.addAll(method_10868(map));
						list3.addAll(method_10869(map));
						list3.addAll(method_10870(map));
						list3.addAll(method_10871(map));
						list3.addAll(method_10857(map, blockPos));
						list3.addAll(method_10872(map));
						list2.addAll(method_10858(map, class_, list3, string2, world, blockPos));
					}
				}

				return method_10856(list2, map, source, class_, string2, blockPos);
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
			Collections.addAll(list, MinecraftServer.getServer().worlds);
		}

		return list;
	}

	private static <T extends Entity> boolean method_10867(CommandSource commandSource, Map<String, String> map) {
		String string = method_10865(map, "type");
		string = string != null && string.startsWith("!") ? string.substring(1) : string;
		if (string != null && !EntityType.isEntityRegistered(string)) {
			TranslatableText translatableText = new TranslatableText("commands.generic.entity.invalidType", string);
			translatableText.getStyle().setFormatting(Formatting.RED);
			commandSource.sendMessage(translatableText);
			return false;
		} else {
			return true;
		}
	}

	private static List<Predicate<Entity>> method_10859(Map<String, String> map, String string) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final String string2 = method_10865(map, "type");
		final boolean bl = string2 != null && string2.startsWith("!");
		if (bl) {
			string2 = string2.substring(1);
		}

		boolean bl2 = !string.equals("e");
		boolean bl3 = string.equals("r") && string2 != null;
		if ((string2 == null || !string.equals("e")) && !bl3) {
			if (bl2) {
				list.add(new Predicate<Entity>() {
					public boolean apply(Entity entity) {
						return entity instanceof PlayerEntity;
					}
				});
			}
		} else {
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					return EntityType.equals(entity, string2) != bl;
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_10863(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final int i = method_10860(map, "lm", -1);
		final int j = method_10860(map, "l", -1);
		if (i > -1 || j > -1) {
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
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
		final int i = method_10860(map, "m", LevelInfo.GameMode.NOT_SET.getId());
		if (i != LevelInfo.GameMode.NOT_SET.getId()) {
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					if (!(entity instanceof ServerPlayerEntity)) {
						return false;
					} else {
						ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
						return serverPlayerEntity.interactionManager.getGameMode().getId() == i;
					}
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_10869(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final String string = method_10865(map, "team");
		final boolean bl = string != null && string.startsWith("!");
		if (bl) {
			string = string.substring(1);
		}

		if (string != null) {
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
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

	private static List<Predicate<Entity>> method_10870(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final Map<String, Integer> map2 = method_4728(map);
		if (map2 != null && map2.size() > 0) {
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					Scoreboard scoreboard = MinecraftServer.getServer().getWorld(0).getScoreboard();

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

						String string2 = entity instanceof ServerPlayerEntity ? entity.getTranslationKey() : entity.getUuid().toString();
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
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_10871(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final String string = method_10865(map, "name");
		final boolean bl = string != null && string.startsWith("!");
		if (bl) {
			string = string.substring(1);
		}

		if (string != null) {
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					return entity.getTranslationKey().equals(string) != bl;
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_10857(Map<String, String> map, BlockPos blockPos) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		final int i = method_10860(map, "rm", -1);
		final int j = method_10860(map, "r", -1);
		if (blockPos != null && (i >= 0 || j >= 0)) {
			final int k = i * i;
			final int l = j * j;
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					int i = (int)entity.squaredDistanceToCenter(blockPos);
					return (i < 0 || i >= k) && (j < 0 || i <= l);
				}
			});
		}

		return list;
	}

	private static List<Predicate<Entity>> method_10872(Map<String, String> map) {
		List<Predicate<Entity>> list = Lists.newArrayList();
		if (map.containsKey("rym") || map.containsKey("ry")) {
			final int i = method_10854(method_10860(map, "rym", 0));
			final int j = method_10854(method_10860(map, "ry", 359));
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					int i = PlayerSelector.method_10854((int)Math.floor((double)entity.yaw));
					return i > j ? i >= i || i <= j : i >= i && i <= j;
				}
			});
		}

		if (map.containsKey("rxm") || map.containsKey("rx")) {
			final int k = method_10854(method_10860(map, "rxm", 0));
			final int l = method_10854(method_10860(map, "rx", 359));
			list.add(new Predicate<Entity>() {
				public boolean apply(Entity entity) {
					int i = PlayerSelector.method_10854((int)Math.floor((double)entity.pitch));
					return k > l ? i >= k || i <= l : i >= k && i <= l;
				}
			});
		}

		return list;
	}

	private static <T extends Entity> List<T> method_10858(
		Map<String, String> map, Class<? extends T> class_, List<Predicate<Entity>> list, String string, World world, BlockPos blockPos
	) {
		List<T> list2 = Lists.newArrayList();
		String string2 = method_10865(map, "type");
		string2 = string2 != null && string2.startsWith("!") ? string2.substring(1) : string2;
		boolean bl = !string.equals("e");
		boolean bl2 = string.equals("r") && string2 != null;
		int i = method_10860(map, "dx", 0);
		int j = method_10860(map, "dy", 0);
		int k = method_10860(map, "dz", 0);
		int l = method_10860(map, "r", -1);
		Predicate<Entity> predicate = Predicates.and(list);
		Predicate<Entity> predicate2 = Predicates.and(EntityPredicate.VALID_ENTITY, predicate);
		if (blockPos != null) {
			int m = world.playerEntities.size();
			int n = world.loadedEntities.size();
			boolean bl3 = m < n / 16;
			if (map.containsKey("dx") || map.containsKey("dy") || map.containsKey("dz")) {
				final Box box = method_10855(blockPos, i, j, k);
				if (bl && bl3 && !bl2) {
					Predicate<Entity> predicate3 = new Predicate<Entity>() {
						public boolean apply(Entity entity) {
							return entity.x < box.minX || entity.y < box.minY || entity.z < box.minZ
								? false
								: !(entity.x >= box.maxX) && !(entity.y >= box.maxY) && !(entity.z >= box.maxZ);
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
				if (bl && bl3 && !bl2) {
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
		List<T> list, Map<String, String> map, CommandSource commandSource, Class<? extends T> class_, String string, BlockPos blockPos
	) {
		int i = method_10860(map, "c", !string.equals("a") && !string.equals("e") ? 1 : 0);
		if (!string.equals("p") && !string.equals("a") && !string.equals("e")) {
			if (string.equals("r")) {
				Collections.shuffle(list);
			}
		} else if (blockPos != null) {
			Collections.sort(list, new Comparator<Entity>() {
				public int compare(Entity entity, Entity entity2) {
					return ComparisonChain.start().compare(entity.squaredDistanceTo(blockPos), entity2.squaredDistanceTo(blockPos)).result();
				}
			});
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

	public static int method_10854(int i) {
		i %= 360;
		if (i >= 160) {
			i -= 360;
		}

		if (i < 0) {
			i += 360;
		}

		return i;
	}

	private static BlockPos method_10864(Map<String, String> map, BlockPos blockPos) {
		return new BlockPos(method_10860(map, "x", blockPos.getX()), method_10860(map, "y", blockPos.getY()), method_10860(map, "z", blockPos.getZ()));
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

	public static boolean method_4088(String string) {
		Matcher matcher = field_4946.matcher(string);
		if (!matcher.matches()) {
			return false;
		} else {
			Map<String, String> map = method_4098(matcher.group(2));
			String string2 = matcher.group(1);
			int i = !"a".equals(string2) && !"e".equals(string2) ? 1 : 0;
			return method_10860(map, "c", i) != 1;
		}
	}

	public static boolean method_4091(String args) {
		return field_4946.matcher(args).matches();
	}

	private static Map<String, String> method_4098(String string) {
		Map<String, String> map = Maps.newHashMap();
		if (string == null) {
			return map;
		} else {
			int i = 0;
			int j = -1;

			for (Matcher matcher = field_4947.matcher(string); matcher.find(); j = matcher.end()) {
				String string2 = null;
				switch (i++) {
					case 0:
						string2 = "x";
						break;
					case 1:
						string2 = "y";
						break;
					case 2:
						string2 = "z";
						break;
					case 3:
						string2 = "r";
				}

				if (string2 != null && matcher.group(1).length() > 0) {
					map.put(string2, matcher.group(1));
				}
			}

			if (j < string.length()) {
				Matcher matcher2 = field_4948.matcher(j == -1 ? string : string.substring(j));

				while (matcher2.find()) {
					map.put(matcher2.group(1), matcher2.group(2));
				}
			}

			return map;
		}
	}
}
